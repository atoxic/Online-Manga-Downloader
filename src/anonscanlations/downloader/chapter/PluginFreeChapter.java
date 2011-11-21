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
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public class PluginFreeChapter extends Chapter implements Serializable
{
    private static final int GRID_W = 480, GRID_H = 480;

    private URL url;

    private transient String hCN, downloadURL, title, zoom, zoomWidthS, zoomHeightS;
    private transient int cKVInt, total, sIS;

    public PluginFreeChapter(URL _url)
    {
        url = _url;
        cKVInt = 0;
        total = 0;
        sIS = 0;
    }

    public ArrayList<DownloadJob> init() throws IOException
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        PageDownloadJob index = new PageDownloadJob("Get index page", new URL(url, "index.shtml"), "Shift_JIS")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                DownloaderUtils.debug("index");

                // content Key Value
                String cKV = title(page, "'cKV'");
                if(cKV == null)
                    throw new Exception("no cKV");
                cKVInt = Integer.parseInt(cKV);

                // root directory
                hCN = title(page, "'hCN'");
                if(hCN == null)
                    throw new Exception("no hCN");
            }
        };
        list.add(index);

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

                // page number
                String tPN = title(initVal, "'tPN'");
                if(tPN == null)
                    throw new Exception("No total page number");
                total = Integer.parseInt(tPN);

                // for decoding page individual files
                String sISString = title(initVal, "'sIS'");
                if(sISString == null)
                    throw new Exception("No sIS");
                sIS = Integer.parseInt(sISString);

                // title
                title = title(initVal, "'sHN'");
                if(title == null)
                    throw new Exception("No sHN");

                // zoom values and dimensions
                zoom = titleList(initVal, "'sFN'");
                zoomWidthS = titleList(initVal, "'iWS'");
                zoomHeightS = titleList(initVal, "'iHS'");
                if(zoom == null || zoomWidthS == null || zoomHeightS == null)
                    throw new Exception("No zoom data");

                downloadURL = new URL(new URL("http://" + url.getHost()), "cgi-bin/widget.cgi?a=" + hCN + title + "/" + title).toString();
            }
        };
        list.add(initVal);

        return(list);
    }

    public ArrayList<DownloadJob> download(File directory) throws IOException
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        final File finalDirectory = directory;
        for(int i = 1; i <= total; i++)
        {
            final int finalIndex = i;
            final String prefix = downloadURL + "_" + String.format("%03d", i) + "_" + (zoom.length() < 2 ? "0" + zoom : zoom);
            final int zoomVal = Integer.parseInt(zoom),
                        w = Integer.parseInt(zoomWidthS),
                        h = Integer.parseInt(zoomHeightS);
            final int gridW = (int)Math.ceil(1.0 * w / GRID_W);
            final int gridH = (int)Math.ceil(1.0 * h / GRID_H);
            final ImageDownloadJob grid[][] = new ImageDownloadJob[gridW][gridH];
            for(int y = 0; y < gridH; y++)
            {
                for(int x = 0; x < gridW; x++)
                {
                    grid[x][y] = new ImageDownloadJob("Page " + i + " (" + x + ", " + y + ")",
                                    new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, zoomVal, x, y) + ".jpg"));
                    list.add(grid[x][y]);
                }
            }
            DownloadJob combine = new DownloadJob("Combine page " + i)
            {
                public void run() throws Exception
                {
                    BufferedImage complete = new BufferedImage(w, h,
                                                                BufferedImage.TYPE_INT_RGB);

                    Graphics2D g = complete.createGraphics();
                    for(int y = 0; y < gridH; y++)
                    {
                        for(int x = 0; x < gridW; x++)
                        {
                            g.drawImage(grid[x][y].getImage(), x * GRID_W, y * GRID_H, null);
                        }
                    }
                    ImageIO.write(complete, "JPEG", DownloaderUtils.fileName(finalDirectory, title, finalIndex, "jpg"));
                }
            };
            list.add(combine);
        }

        return(list);
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

    private String titleList(String page, String ID)
    {
        String ret = title(page, ID);
        if(ret != null && ret.indexOf(',') != -1)
        {
            return(ret.substring(0, ret.indexOf(',')));
        }
        return(null);
    }
}
