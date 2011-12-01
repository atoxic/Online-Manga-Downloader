package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.util.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class JSoupDownloadJob extends DownloadJob
{
    private boolean init;

    protected URL url;
    protected Connection conn;
    protected Connection.Response response;
    protected Map<String, String> cookies;

    public JSoupDownloadJob(String _desc, URL _url)
    {
        super(_desc);
        url = _url;
        init = false;
    }

    protected void init() throws Exception
    {
        init = true;
        conn = Jsoup.connect(url.toString()).followRedirects(true).timeout(10000);
        for(Map.Entry<String, String> e : headers.entrySet())
            conn.header(e.getKey(), e.getValue());
        if(cookies != null)
        {
            for(Map.Entry<String, String> cookie : cookies.entrySet())
                conn.cookie(cookie.getKey(), cookie.getValue());
        }
        if(!data.isEmpty())
            conn.data(data).method(Connection.Method.POST);
        else
            conn.method(Connection.Method.GET);
    }

    public void run() throws Exception
    {
        if(!init)
            init();
        DownloaderUtils.debug("JSoupDJ (" + getClass() + "): URL: " + url);
        response = conn.execute();
        DownloaderUtils.debug("JSoupDJ (" + getClass() + "): Response Headers: " + response.headers());
        //long length = Long.parseLong(response.header("Content-Length"));
    }

    public byte[] getBytes()
    {
        return(response.bodyAsBytes());
    }

    public void setCookies(Map<String, String> _cookies)
    {
        cookies = _cookies;
    }
}
