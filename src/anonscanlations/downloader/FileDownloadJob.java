package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class FileDownloadJob extends DownloadJob
{
    protected URL url;
    protected String cookies;
    protected File file;
    public FileDownloadJob(String _description, URL _url, File _file)
    {
        this(_description, _url, _file, null);
    }
    public FileDownloadJob(String _description, URL _url, File _file, String _cookies)
    {
        super(_description);
        url = _url;
        file = _file;
        cookies = _cookies;
    }
    public void run() throws Exception
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(cookies != null)
            conn.setRequestProperty("Cookie", cookies);
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

