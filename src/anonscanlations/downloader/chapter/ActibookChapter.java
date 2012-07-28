/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public class ActibookChapter extends Chapter implements Serializable
{
    private static final int DOKI_GRID_W = 400, DOKI_GRID_H = 400;

    private String zoom, type;
    private int start, total;
    private float w, h;

    public ActibookChapter(URL _url)
    {
        super(_url);
        
        zoom = "1";
        type = null;
        start = total = 0;
        w = h = 0;
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        if(!url.getProtocol().equals("http"))
            throw new IOException("Can only use http");
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        JSoupDownloadJob bookXML = new JSoupDownloadJob("book.xml for page range and title", new URL(url, "books/db/book.xml"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Document d = response.parse();
                title = JSoupUtils.elementText(d, "name");
                type = JSoupUtils.elementText(d, "to_type");
                w = JSoupUtils.elementTextFloat(d, "w");
                h = JSoupUtils.elementTextFloat(d, "h");
                start = JSoupUtils.elementTextInt(d, "start");
                total = JSoupUtils.elementTextInt(d, "total");
            }
        };
        JSoupDownloadJob viewerXML = new JSoupDownloadJob("viewer.xml for zoom level", new URL(url, "books/db/viewer.xml"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                String zooms = JSoupUtils.elementText(response.parse(), "zoom_s");
                String[] zoomLevels = zooms.split(",");
                zoom = zoomLevels[zoomLevels.length - 1];
            }
        };
        list.add(bookXML);
        list.add(viewerXML);
        return(list);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        if(type.equals("normal"))
        {
            if(w == -1 || h == -1)
                throw new Exception("No dimensions");
            final float zoomVal = Float.parseFloat(zoom);
            final int gridW = (int)Math.ceil(w * zoomVal / DOKI_GRID_W);
            final int gridH = (int)Math.ceil(h * zoomVal / DOKI_GRID_H);
            for(int i = start; i < start + total; i++)
            {
                final File f = DownloaderUtils.fileName(directory, i, "png");
                 if(f.exists())
                    continue;
                final ImageDownloadJob _grid[][] = new ImageDownloadJob[gridW][gridH];
                for(int y = 0; y < gridH; y++)
                {
                    for(int x = 0; x < gridW; x++)
                    {
                        _grid[x][y] = new ImageDownloadJob("Page " + i + " (" + x + ", " + y + ")",
                                        new URL(url, "books/images/" + zoom + "/g_" + i + "/x" + (x + 1) + "y" + (y + 1) + ".jpg"));
                        list.add(_grid[x][y]);
                    }
                }
                CombineDownloadJob combine = new CombineDownloadJob("Combine page " + i, f, (int)(w * zoomVal),
                                                                    (int)(h * zoomVal), DOKI_GRID_W, DOKI_GRID_H)
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
        }
        else if(type.equals("rich"))
        {
            for(int i = start; i < start + total; i++)
            {
                File f = DownloaderUtils.fileName(directory, i, "jpg");
                if(f.exists())
                    continue;
                FileDownloadJob page = new FileDownloadJob(DownloaderUtils.pageOutOf(i, start, total),
                                            new URL(url, "books/images/" + zoom + "/" + i + ".jpg"), f);
                list.add(page);
            }
        }
        else
        {
            throw new Exception("This type of Actibook is unknown: \"" + type + ".\"  Please report this bug.");
        }
        return(list);
    }
}
