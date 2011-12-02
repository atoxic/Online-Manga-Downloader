package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.net.*;

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;
import anonscanlations.downloader.chapter.crypto.*;

/**
 * Comic Rush
 * @author /a/non
 */
public class CLIPChapter extends Chapter
{
    private URL url, originalURL;
    private Map<String, String> params;
    private String title;
    private ArrayList<Integer> pages;
    private byte[] key, time, decodeKey;

    private transient Map<String, String> cookies;
    private transient String username;
    private transient char[] password;
    
    public CLIPChapter(URL _url)
    {
        this(_url, null, null);
    }
    public CLIPChapter(URL _url, String _username, char[] _password)
    {
        url = originalURL = _url;
        params = null;
        title = null;
        pages = new ArrayList<Integer>();
        key = time = decodeKey = null;
        
        username = _username;
        password = _password;
        cookies = null;
    }

    @Override
    public void getRequiredInfo(LoginManager s) throws Exception
    {
        if(username == null || password == null)
        {
            username = s.getComicRushLogin().getEMail();
            password = s.getComicRushLogin().getPassword();
        }
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        if(!url.getProtocol().equals("http"))
            throw new IOException("Can only use http");
        if(!url.getHost().contains("comic-rush.jp"))
            throw new UnsupportedOperationException("This download currently only supports Comic Rush");
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        url = DownloaderUtils.getRedirectURL(originalURL);
        if(url.getPath().contains("login"))
        {
            list.add(new ComicRushLoginDownloadJob());
            JSoupDownloadJob redirect = new JSoupDownloadJob("Redirect to page", originalURL)
            {
                @Override
                protected void init() throws Exception
                {
                    super.init();
                    conn.followRedirects(false);
                }
                @Override
                public void run() throws Exception
                {
                    setCookies(CLIPChapter.this.cookies);
                    super.run();

                    if(response.statusCode() < 300 || response.statusCode() > 302)
                        CLIPChapter.this.url = originalURL;
                    else
                        CLIPChapter.this.url = new URL(originalURL, response.headers().get("Location"));
                }
            };
            list.add(redirect);
        }
        
        DownloadJob queryMap = new DownloadJob("Parse query")
        {
            @Override
            public void run() throws Exception
            {
                DownloaderUtils.debug("url: " + url);
                params = DownloaderUtils.getQueryMap(url);
                if(!params.containsKey("site_id") ||
                    !params.containsKey("service_id") ||
                    !params.containsKey("content_id") ||
                    !params.containsKey("hash") ||
                    !params.containsKey("extraData"))
                    throw new Exception("Invalid URL");
            }
        };
        list.add(queryMap);
        
        JSoupDownloadJob metadata = new JSoupDownloadJob("Get metadata", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://release-stg.clip-studio.com/api/getMetadata?" +
                            "content%5Fid=" + params.get("content_id") +
                            "&service%5Fid=" + params.get("service_id") +
                            "&option" + System.currentTimeMillis());
                
                super.run();
                Document d = response.parse();
                String errno = JSoupUtils.elementAttr(d, "response", "errno");
                if(errno != null && !errno.equals("0"))
                    throw new Exception("browsePermission returned error");
                title = JSoupUtils.elementText(d, "title");
            }
        };
        list.add(metadata);
        
        JSoupDownloadJob pageList = new JSoupDownloadJob("Get page list", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://release-stg.clip-studio.com/api/getExpandedContentFile?" +
                            "path=/" + params.get("service_id") + "/" 
                                    + params.get("content_id") + "/" 
                                    + params.get("site_id") + "/auth.xml" + 
                            "&hostname=&password=&option" + System.currentTimeMillis());
                
                super.run();
                Document d = org.jsoup.Jsoup.parseBodyFragment(response.body());
                for(Element e : d.select("page"))
                {
                    if(e.hasAttr("no"))
                        pages.add(Integer.parseInt(e.attr("no")));
                }
            }
        };
        list.add(pageList);
        
        return(list);
    }

    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        DownloaderUtils.debug("title: " + title);
        for(int page : pages)
            DownloaderUtils.debug("page: " + page);
        
        // Have to get the first page, get the hash, then decode the first page
        final JSoupDownloadJob firstPage =
                new JSoupDownloadJob("Page 1",
                    new URL("http://release-stg.clip-studio.com/api/getExpandedImageFile?" +
                        "password=&hostname=&content%5Fid=" + params.get("content_id") + 
                        "&path=/" + params.get("service_id") + "/" 
                                    + params.get("content_id") + "/" 
                                    + params.get("site_id") + "/" 
                                    + String.format("%03d", pages.get(0)) + "-1.jpg" +
                        "&service%5Fid=" + params.get("service_id")));
        JSoupDownloadJob getTime =
                new JSoupDownloadJob("Get time key", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://drm.clip-studio.com/drm/ViewerServlet?" + 
                            "hash=" + CLIPDecrypt.createHash(firstPage.getBytes()) +
                            "&getTime=" + System.currentTimeMillis());
                super.run();
                time = response.bodyAsBytes();
                DownloaderUtils.debug("time: " + Arrays.toString(time));
            }
        };
        JSoupDownloadJob getKey =
                new JSoupDownloadJob("Get key", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://drm.clip-studio.com/drm/ViewerServlet?" + 
                            "requestID=" + CLIPDecrypt.createRequestID(time) +
                            "&getKey=" + System.currentTimeMillis());
                
                super.run();
                key = response.bodyAsBytes();
                DownloaderUtils.debug("key: " + Arrays.toString(key));
                decodeKey = CLIPDecrypt.decodeKey(key, time);
                DownloaderUtils.debug("key: " + Arrays.toString(key));
                DownloaderUtils.debug("decodeKey: " + Arrays.toString(decodeKey));
            }
        };
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(firstPage);
        list.add(getTime);
        list.add(getKey);

        final File firstPageFile = DownloaderUtils.fileName(directory, title, pages.get(0), "jpg");
        if(!firstPageFile.exists())
        {
            DownloadJob firstPageDecode = new DownloadJob("Decode page 1")
            {
                @Override
                public void run() throws Exception
                {
                    byte[] dec = CLIPDecrypt.decodeBinary(firstPage.getBytes(), decodeKey);
                    DownloaderUtils.safeWrite(dec, firstPageFile);
                }
            };
            list.add(firstPageDecode);
        }
        
        for(int i = 1; i < pages.size(); i++)
        {
            final int page = pages.get(i);
            final File f = DownloaderUtils.fileName(directory, title, page, "jpg");
            if(f.exists())
                continue;

            ByteArrayDownloadJob pageJob =
                new ByteArrayDownloadJob("Page " + page,
                    new URL("http://release-stg.clip-studio.com/api/getExpandedImageFile?" +
                        "password=&hostname=&content%5Fid=" + params.get("content_id") + 
                        "&path=/" + params.get("service_id") + "/" 
                                    + params.get("content_id") + "/" 
                                    + params.get("site_id") + "/" 
                                    + String.format("%03d", page) + "-1.jpg" +
                        "&service%5Fid=" + params.get("service_id")))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();
                    byte[] dec = CLIPDecrypt.decodeBinary(bytes, decodeKey);
                    DownloaderUtils.safeWrite(dec, f);
                }
            };
            list.add(pageJob);
        }
        
        return(list);
    }
    
    private class ComicRushLoginDownloadJob extends JSoupDownloadJob
    {
        public ComicRushLoginDownloadJob()
        {
            super("Login to Comic Rush", null);
        }
        
        @Override
        public void run() throws Exception
        {
            if(username == null || password == null)
                throw new Exception("No login");

            url = new URL("https://www.comic-rush.jp/login");
            addPOSTData("do", "1");
            addPOSTData("x", Integer.toString((int)(Math.random() * 180)));
            addPOSTData("y", Integer.toString((int)(Math.random() * 60)));
            addPOSTData("mailaddress", username);
            addPOSTData("password", new String(password));
            super.run();
            CLIPChapter.this.cookies = response.cookies();
        }
    }
}
