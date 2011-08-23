package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ByteArrayDownloadJob extends DownloadJob
{
    protected URL url;
    protected String cookies;
    protected byte[] buf;
    public ByteArrayDownloadJob(String _description, URL _url)
    {
        this(_description, _url, null);
    }
    public ByteArrayDownloadJob(String _description, URL _url, String _cookies)
    {
        super(_description);
        url = _url;
        cookies = _cookies;
        buf = null;
    }
    public void run() throws Exception
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(cookies != null)
            conn.setRequestProperty("Cookie", cookies);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 File Not Found: " + url);

        InputStream in = conn.getInputStream();
        buf = new byte[conn.getContentLength()];
        int read, offset = 0;
        while((read = in.read(buf, offset, buf.length - offset)) != -1)
        {
            offset += read;
        }
        in.close();
    }
}

