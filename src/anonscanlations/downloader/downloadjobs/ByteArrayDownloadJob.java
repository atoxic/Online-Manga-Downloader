package anonscanlations.downloader.downloadjobs;

import java.io.*;
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
        sendPOSTData(conn);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 File Not Found: " + url);

        InputStream in = conn.getInputStream();
        bytes = DownloaderUtils.readAllBytes(in);
    }
}

