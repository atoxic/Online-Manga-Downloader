package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.net.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

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
    
    private transient String username, cookies;
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
            DownloadJob redirect = new DownloadJob("Redirect to page")
            {
                @Override
                public void run() throws Exception
                {
                    DownloaderUtils.debug("url: " + originalURL);
                    
                    HttpURLConnection conn = (HttpURLConnection) originalURL.openConnection();
                    DownloaderUtils.debug("cookies: " + cookies.substring(0, cookies.indexOf(";")));
                    conn.setRequestProperty("Cookie", cookies.substring(0, cookies.indexOf(";")));
                    conn.setInstanceFollowRedirects(false);
                    DownloaderUtils.debug("response: " + conn.getResponseCode());
                    if(conn.getResponseCode() < 300 && conn.getResponseCode() > 302)
                        return;
                    String headerName = null;
                    for(int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
                    {
                        if(headerName.equals("Location"))
                        {
                            url = new URL(originalURL, conn.getHeaderField(i));
                        }
                    }
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
        
        PageDownloadJob metadata = new PageDownloadJob("Get metadata", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://release-stg.clip-studio.com/api/getMetadata?" +
                            "content%5Fid=" + params.get("content_id") +
                            "&service%5Fid=" + params.get("service_id") +
                            "&option" + System.currentTimeMillis());
                
                super.run();
                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    private boolean getTitle = false;
                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        if(getTitle)
                            title = new String(ch, start, length);
                    }
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                            throws SAXException
                    {
                        if(localName.equals("response") && atts.getValue("errno") != null
                                && !atts.getValue("errno").equals("0"))
                            throw new SAXException("browsePermission returned error");
                        if(localName.equals("title"))
                            getTitle = true;
                    }
                    @Override
                    public void endElement(String uri, String localName, String qName)
                    {
                        if(localName.equals("title"))
                            getTitle = false;
                    }
                });
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };
        list.add(metadata);
        
        PageDownloadJob pageList = new PageDownloadJob("Get page list", null, "UTF-8")
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
                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                            throws SAXException
                    {
                        if(localName.equals("page") && atts.getValue("no") != null)
                            pages.add(Integer.parseInt(atts.getValue("no")));
                    }
                });
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };
        list.add(pageList);
        
        return(list);
    }

    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        final File finalDirectory = directory;
        DownloaderUtils.debug("title: " + title);
        for(int page : pages)
            DownloaderUtils.debug("page: " + page);
        
        // Have to get the first page, get the hash, then decode the first page
        final ByteArrayDownloadJob firstPage = 
                new ByteArrayDownloadJob("Page 1",
                    new URL("http://release-stg.clip-studio.com/api/getExpandedImageFile?" +
                        "password=&hostname=&content%5Fid=" + params.get("content_id") + 
                        "&path=/" + params.get("service_id") + "/" 
                                    + params.get("content_id") + "/" 
                                    + params.get("site_id") + "/" 
                                    + String.format("%03d", pages.get(0)) + "-1.jpg" +
                        "&service%5Fid=" + params.get("service_id")));
        ByteArrayDownloadJob getTime = 
                new ByteArrayDownloadJob("Get time key", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://drm.clip-studio.com/drm/ViewerServlet?" + 
                            "hash=" + CLIPDecrypt.createHash(firstPage.getBytes()) +
                            "&getTime=" + System.currentTimeMillis());
                super.run();
                time = bytes;
                DownloaderUtils.debug("time: " + Arrays.toString(time));
            }
        };
        ByteArrayDownloadJob getKey = 
                new ByteArrayDownloadJob("Get key", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://drm.clip-studio.com/drm/ViewerServlet?" + 
                            "requestID=" + CLIPDecrypt.createRequestID(time) +
                            "&getKey=" + System.currentTimeMillis());
                
                super.run();
                key = bytes;
                DownloaderUtils.debug("key: " + Arrays.toString(key));
                decodeKey = CLIPDecrypt.decodeKey(key, time);
                DownloaderUtils.debug("key: " + Arrays.toString(key));
                DownloaderUtils.debug("decodeKey: " + Arrays.toString(decodeKey));
            }
        };
        DownloadJob firstPageDecode = new DownloadJob("Decode page 1")
        {
            @Override
            public void run() throws Exception
            {
                byte[] dec = CLIPDecrypt.decodeBinary(firstPage.getBytes(), decodeKey);
                FileOutputStream fos = new FileOutputStream(DownloaderUtils.fileName(finalDirectory, title, pages.get(0), "jpg"));
                fos.write(dec);
                fos.close();
            }
        };
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(firstPage);
        list.add(getTime);
        list.add(getKey);
        list.add(firstPageDecode);
        
        for(int i = 1; i < pages.size(); i++)
        {
            final int page = pages.get(i);
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
                    FileOutputStream fos = 
                            new FileOutputStream(DownloaderUtils.fileName(finalDirectory, 
                                                    title, page, "jpg"));
                    fos.write(dec);
                    fos.close();
                }
            };
            list.add(pageJob);
        }
        
        return(list);
    }
    
    private class ComicRushLoginDownloadJob extends DownloadJob
    {
        public ComicRushLoginDownloadJob()
        {
            super("Login to Comic Rush");
        }
        
        @Override
        public void run() throws Exception
        {
            /*
            if(username == null && password == null)
            {
                username = DIALOG.getEMail();
                password = DIALOG.getPassword();
            }
            // */

            URL url = new URL("https://www.comic-rush.jp/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setRequestProperties(conn);

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("do=1&x=38&y=32&mailaddress=" + username + "&password=");
            wr.write(password);
            wr.flush();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                throw new Exception("404 Page Not Found: " + url);
            String headerName = null;
            for(int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
            {
                if(headerName.equals("Set-Cookie") && !conn.getHeaderField(i).contains("deleted"))
                {
                    cookies = conn.getHeaderField(i);
                    DownloaderUtils.debug("cookies: " + cookies);
                }
            }
        }
    }
}
