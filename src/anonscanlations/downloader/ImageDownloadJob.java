package anonscanlations.downloader;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.imageio.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ImageDownloadJob extends DownloadJob
{
    protected URL url;
    protected String cookies;
    protected BufferedImage image;
    public ImageDownloadJob(String _description, URL _url)
    {
        this(_description, _url, null);
    }
    public ImageDownloadJob(String _description, URL _url, String _cookies)
    {
        super(_description);
        url = _url;
        cookies = _cookies;
    }
    public void run() throws Exception
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(cookies != null)
            conn.setRequestProperty("Cookie", cookies);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 File Not Found: " + url);

        InputStream in = conn.getInputStream();
        image = ImageIO.read(in);
    }
}

