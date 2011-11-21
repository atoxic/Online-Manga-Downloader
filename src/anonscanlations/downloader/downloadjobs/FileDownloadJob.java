package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class FileDownloadJob extends DownloadJob
{
    protected URL url;
    protected File file;
    public FileDownloadJob(String _description, URL _url, File _file)
    {
        super(_description);
        url = _url;
        file = _file;
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("FileDownloadJob: " + url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn);
        sendPOSTData(conn);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 File Not Found: " + url);

        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
        InputStream in = conn.getInputStream();
        byte[] buf = new byte[1024];
        int read;
        while((read = in.read(buf)) != -1)
            output.write(buf, 0, read);

        in.close();
        output.close();
    }
}

