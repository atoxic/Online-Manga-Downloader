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

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;
import anonscanlations.downloader.chapter.crypto.*;

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
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        JSoupDownloadJob index = new JSoupDownloadJob("Get index page", new URL(url, "index.shtml"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Document d = response.parse();

                // content Key Value
                String cKV = JSoupUtils.elementAttr(d, "#cKV", "title"); //title(page, "'cKV'");
                if(cKV == null)
                    throw new Exception("no cKV");
                cKVInt = Integer.parseInt(cKV);
                DownloaderUtils.debug("PFC: cKV: " + cKV);

                // root directory
                hCN = JSoupUtils.elementAttr(d, "#hCN", "title"); // title(page, "'hCN'");
                if(hCN == null)
                    throw new Exception("no hCN");
                DownloaderUtils.debug("PFC: hCN: " + hCN);
            }
        };
        list.add(index);

        JSoupDownloadJob initVal = new JSoupDownloadJob("Get initVal page", new URL(url, "InitVal.html"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Document encoded = response.parse();
                String initVal = PluginFreeDecrypt.expand(JSoupUtils.elementText(encoded, "#DATA"), cKVInt);
                Document d = org.jsoup.Jsoup.parseBodyFragment(initVal);

                // page number
                String tPN = JSoupUtils.elementAttr(d, "#tPN", "title"); //title(initVal, "'tPN'");
                if(tPN == null)
                    throw new Exception("No total page number");
                total = Integer.parseInt(tPN);

                // for decoding page individual files
                String sISString = JSoupUtils.elementAttr(d, "#sIS", "title"); //title(initVal, "'sIS'");
                if(sISString == null)
                    throw new Exception("No sIS");
                sIS = Integer.parseInt(sISString);

                // title
                title = JSoupUtils.elementAttr(d, "#sHN", "title"); //title(initVal, "'sHN'");
                if(title == null)
                    throw new Exception("No sHN");

                // zoom values and dimensions
                zoom = titleList(d, "#sFN");
                zoomWidthS = titleList(d, "#iWS");
                zoomHeightS = titleList(d, "#iHS");
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

        for(int i = 1; i <= total; i++)
        {
            final File f = DownloaderUtils.fileName(directory, title, i, "jpg");
            if(f.exists())
                continue;

            final String prefix = downloadURL + "_" + String.format("%03d", i) + "_" + (zoom.length() < 2 ? "0" + zoom : zoom);
            final int zoomVal = Integer.parseInt(zoom),
                        w = Integer.parseInt(zoomWidthS),
                        h = Integer.parseInt(zoomHeightS);
            final int gridW = (int)Math.ceil(1.0 * w / GRID_W);
            final int gridH = (int)Math.ceil(1.0 * h / GRID_H);
            final ImageDownloadJob _grid[][] = new ImageDownloadJob[gridW][gridH];
            for(int y = 0; y < gridH; y++)
            {
                for(int x = 0; x < gridW; x++)
                {
                    _grid[x][y] = new ImageDownloadJob("Page " + i + " (" + x + ", " + y + ")",
                                    new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, zoomVal, x, y) + ".jpg"));
                    list.add(_grid[x][y]);
                }
            }
            CombineDownloadJob combine = new CombineDownloadJob("Combine page " + i, f, w, h, GRID_W, GRID_H)
            {
                @Override
                public void run() throws Exception
                {
                    this.grid = _grid;
                    super.run();
                }
            };
            list.add(combine);
        }

        return(list);
    }

    private String titleList(Document d, String selector)
    {
        String ret = JSoupUtils.elementAttr(d, selector, "title");
        if(ret != null && ret.indexOf(',') != -1)
        {
            return(ret.substring(0, ret.indexOf(',')));
        }
        return(null);
    }
}
