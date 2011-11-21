package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.util.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ByteArrayDownloadJob extends DownloadJob
{
    protected URL url;
    protected byte[] bytes;
    public ByteArrayDownloadJob(String _description, URL _url)
    {
        super(_description);
        url = _url;
        bytes = null;
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("ByteArrayDownloadJob: " + url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 File Not Found: " + url);

        InputStream in = conn.getInputStream();
        bytes = new byte[conn.getContentLength()];
        int read, offset = 0;
        while((read = in.read(bytes, offset, bytes.length - offset)) != -1)
        {
            offset += read;
        }
        in.close();
    }
}

