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
public class MCSChapter extends Chapter
{
    private URL url, originalURL;
    private Map<String, String> params;
    private String title;
    private ArrayList<Integer> pages;
    private byte[] key, time, decodeKey;
    
    public MCSChapter(URL _url)
    {
        this(_url, null, null);
    }
    public MCSChapter(URL _url, String _username, char[] _password)
    {
        url = originalURL = _url;
        params = null;
        title = null;
        pages = new ArrayList<Integer>();
        key = time = decodeKey = null;
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        if(!url.getProtocol().equals("http"))
            throw new IOException("Can only use http");
        url = DownloaderUtils.getRedirectURL(originalURL);
        if(!url.getHost().contains("csa-platform.jp"))
            throw new UnsupportedOperationException("This downloader only works on the csa-platform.jp domain. Please give a viewer URL on there.");
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
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
                    !params.containsKey("ticket") ||
                    !params.containsKey("extra_data"))
                    throw new Exception("Invalid URL");
                // TODO parse content type from HTML?
                params.put("content_type", "301");
            }
        };
        list.add(queryMap);
        
        JSoupDownloadJob metadata = new JSoupDownloadJob("Get metadata", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://stx.csa-platform.jp/apix/" + params.get("content_type") +
                            "/getMetadata.php?site%5Fid=" + params.get("site_id") +
                            "&content%5Fid=" + params.get("content_id") +
                            "&service%5Fid=" + params.get("service_id") +
                            "&ticket=" + params.get("ticket") +
                            "&content%5Ftype=" + params.get("content_type") +
                            "&csa=null");
                
                super.run();
                Document d = response.parse();
                String errno = JSoupUtils.elementAttr(d, "response", "errno");
                if(errno != null && !errno.equals("0"))
                    throw new Exception("browsePermission returned error");
                title = JSoupUtils.elementText(d, "inTitleJ");
            }
        };
        list.add(metadata);
        
        JSoupDownloadJob pageList = new JSoupDownloadJob("Get page list", null)
        {
            @Override
            public void run() throws Exception
            {
                url = getExpandedContentFile("publish%2Fresolution%5Fpc%2Fdata%2Etxt");
                super.run();
                String[] params = response.body().split("&");
                for(String param : params)
                {
                    if(!param.contains("="))
                        continue;
                    String[] parts = param.split("=");
                    if(!parts[0].equals("pagenumber"))
                        continue;
                    try
                    {
                        pages.add(Integer.parseInt(parts[1]) - 1);
                    }
                    catch(NumberFormatException nfe)
                    {
                        DownloaderUtils.error("MCS page list contained non-integer page number.", nfe, false);
                    }
                }
            }
        };
        list.add(pageList);
        
        return(list);
    }

    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {        
        // Have to get the first page, get the hash, then decode the first page
        final JSoupDownloadJob firstPage =
                new JSoupDownloadJob(DownloaderUtils.pageOutOf(1, 1, pages.size()),
                    getExpandedContentFile("publish%2Fresolution%5Fpc%2Fpage" + pages.get(0) + "%5Fo%2Ejpg"));
        JSoupDownloadJob getTime =
                new JSoupDownloadJob("Get time key", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://stx.csa-platform.jp/apix/getDrmTime.php?" + 
                            "content%5Fid=" + params.get("content_id") +
                            "&hash=" + CLIPDecrypt.createHash(firstPage.getBytes()) +
                            "&csa=null");
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
                //http://stx.csa-platform.jp/apix/getDrmKey.php?request%5Fid=230234&csa=null
                url = new URL("http://stx.csa-platform.jp/apix/getDrmKey.php?" + 
                            "request%5Fid=" + CLIPDecrypt.createRequestID(time) +
                            "&csa=null");
                
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
                new ByteArrayDownloadJob(DownloaderUtils.pageOutOf(page, 1, pages.size()),
                    getExpandedContentFile("publish%2Fresolution%5Fpc%2Fpage" + page + "%5Fo%2Ejpg"))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();
                    DownloaderUtils.safeWrite(CLIPDecrypt.decodeBinary(getBytes(), decodeKey), f);
                }
            };
            list.add(pageJob);
        }
        
        return(list);
    }
    
    private URL getExpandedContentFile(String filePath) throws MalformedURLException
    {
        return(new URL("http://stx.csa-platform.jp/api/getExpandedContentFile.php?" +
                            "site%5Fid=" + params.get("site_id") +
                            "&content%5Fid=" + params.get("content_id") +
                            "&service%5Fid=" + params.get("service_id") +
                            "&ticket=" + params.get("ticket") +
                            "&content%5Ftype=" + params.get("content_type") +
                            "&csa=null" +
                            "&file%5Fpath=" + filePath));
    }
}
