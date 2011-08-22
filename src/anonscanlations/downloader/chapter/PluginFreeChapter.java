/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class PluginFreeChapter extends Chapter implements Serializable
{
    public static final int nsn = 15;

    private URL url;

    private transient String hCN, downloadURL, sHN;
    private transient int cKVInt, total, sIS;

    public PluginFreeChapter(URL _url)
    {
        url = _url;
        cKVInt = 0;
        total = 0;
        sIS = 0;
    }

    public void init() throws IOException
    {
        PageDownloadJob index = new PageDownloadJob("Get index page", new URL(url, "index.shtml"), "Shift_JIS")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                // content Key Value
                String cKV = title(page, "'cKV'");
                if(cKV == null)
                    throw new Exception("no cKV");
                DownloaderUtils.debug("cKV: " + cKV);
                cKVInt = Integer.parseInt(cKV);

                // root directory
                hCN = title(page, "'hCN'");
                if(hCN == null)
                    throw new Exception("no hCN");
                DownloaderUtils.debug("hCN: " + hCN);
            }
        };
        Downloader.getDownloader().addJob(index);

        PageDownloadJob initVal = new PageDownloadJob("Get initVal page", new URL(url, "InitVal.html"), "Shift_JIS")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                int index = page.indexOf("<DIV ID='DATA'>");
                if(index == -1)
                    throw new Exception("No data");
                String initVal = PluginFreeDecrypt.expand(page.substring(index + 15, page.indexOf("</DIV>", index)), cKVInt);

                DownloaderUtils.debug("initVal: " + initVal);

                // page number
                String tPN = title(initVal, "'tPN'");
                if(tPN == null)
                    throw new Exception("No total page number");
                total = Integer.parseInt(tPN);
                DownloaderUtils.debug("\t\t\ttotal: " + total);

                // for decoding page individual files
                String sISString = title(initVal, "'sIS'");
                if(sISString == null)
                    throw new Exception("No sIS");
                sIS = Integer.parseInt(sISString);
                DownloaderUtils.debug("\t\t\tsIS: " + sIS);

                // title
                sHN = title(initVal, "'sHN'");
                if(sHN == null)
                    throw new Exception("No sHN");
                DownloaderUtils.debug("\t\t\tsHN: " + sHN);

                downloadURL = new URL(new URL("http://" + url.getHost()), "cgi-bin/widget.cgi?a=" + hCN + sHN + "/" + sHN).toString();
            }
        };
        Downloader.getDownloader().addJob(initVal);
    }

    public void download(File directory) throws IOException
    {
        final File finalDirectory = directory;
        //*
        for(int i = 1; i <= total; i++)
        {
            final int finalIndex = i;
            String prefix = downloadURL + "_" + String.format("%03d", i) + "_" + nsn;

            final ImageDownloadJob topLeft   =   new ImageDownloadJob("Page " + i + " top left",
                                                            new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 0, 0) + ".jpg"));
            final ImageDownloadJob topRight  =   new ImageDownloadJob("Page " + i + " top right",
                                                            new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 1, 0) + ".jpg"));
            final ImageDownloadJob botLeft   =   new ImageDownloadJob("Page " + i + " bottom left",
                                                            new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 0, 1) + ".jpg"));
            final ImageDownloadJob botRight  =   new ImageDownloadJob("Page " + i + " bottom right",
                                                            new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 1, 1) + ".jpg"));
            DownloadJob combine = new DownloadJob("Combine page " + i)
            {
                public void run() throws Exception
                {
                    BufferedImage complete = new BufferedImage(topLeft.getImage().getWidth() + topRight.getImage().getWidth(),
                                                        topLeft.getImage().getHeight() + botLeft.getImage().getHeight(),
                                                        BufferedImage.TYPE_INT_RGB);

                    Graphics2D g = complete.createGraphics();
                    g.drawImage(topLeft.getImage(),    0,                                 0, null);
                    g.drawImage(topRight.getImage(),   topLeft.getImage().getWidth(),     0, null);
                    g.drawImage(botLeft.getImage(),    0,                                 topLeft.getImage().getHeight(), null);
                    g.drawImage(botRight.getImage(),   topLeft.getImage().getWidth(),     topLeft.getImage().getHeight(), null);

                    ImageIO.write(complete, "JPEG", DownloaderUtils.fileName(finalDirectory, sHN, finalIndex, "jpg"));
                }
            };
            Downloader.getDownloader().addJob(topLeft);
            Downloader.getDownloader().addJob(topRight);
            Downloader.getDownloader().addJob(botLeft);
            Downloader.getDownloader().addJob(botRight);
            Downloader.getDownloader().addJob(combine);
        }
    }

    private String title(String page, String ID)
    {
        int index = page.indexOf("ID=" + ID);
        if(index != -1)
        {
            index = page.indexOf("title='", index);
            if(index != -1)
                return(page.substring(index + 7, page.indexOf('\'', index + 7)));
        }
        return(null);
    }
}
