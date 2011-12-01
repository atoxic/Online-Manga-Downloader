package anonscanlations.downloader.downloadjobs;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ImageDownloadJob extends JSoupDownloadJob
{
    protected BufferedImage image;
    public ImageDownloadJob(String _description, URL _url)
    {
        super(_description, _url);
        url = _url;
        image = null;
    }
    public BufferedImage getImage()
    {
        return(image);
    }
    @Override
    public void run() throws Exception
    {
        super.run();

        ByteArrayInputStream bais = null;
        try
        {
            bais = new ByteArrayInputStream(response.bodyAsBytes());
            image = ImageIO.read(bais);
        }
        finally
        {
            if(bais != null)
                bais.close();
        }
    }
}

