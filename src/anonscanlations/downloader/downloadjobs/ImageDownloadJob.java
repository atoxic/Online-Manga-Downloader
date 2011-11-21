package anonscanlations.downloader.downloadjobs;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.imageio.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ImageDownloadJob extends DownloadJob
{
    protected URL url;
    protected BufferedImage image;
    public ImageDownloadJob(String _description, URL _url)
    {
        super(_description);
        url = _url;
        image = null;
    }
    public BufferedImage getImage()
    {
        return(image);
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("ImageDownloadJob: " + url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn);
        sendPOSTData(conn);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 File Not Found: " + url);

        InputStream in = conn.getInputStream();
        image = ImageIO.read(in);
    }
}

