package anonscanlations.downloader.downloadjobs;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ImageDownloadJob extends ByteArrayDownloadJob
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
        ByteArrayInputStream bais = null;
        try
        {
            super.run();
            bais = new ByteArrayInputStream(getBytes());
            image = ImageIO.read(bais);
        }
        catch(IOException e)
        {
            image = null;
        }
        finally
        {
            if(bais != null)
                bais.close();
        }
    }
}

