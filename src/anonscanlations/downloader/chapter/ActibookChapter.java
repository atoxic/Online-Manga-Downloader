/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;

import org.w3c.dom.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ActibookChapter extends Chapter implements Serializable
{
    private static final int DOKI_GRID_W = 400, DOKI_GRID_H = 400;

    private URL url;
    private String zoom, title, type;
    private int start, total, w, h;

    public ActibookChapter(URL myURL)
    {
        url = myURL;
        total = 0;
        start = 0;
        zoom = "1";
        w = 0;
        h = 0;
    }

    private static int getIntContents(Element doc, String tagName)
    {
        Node contents = DownloaderUtils.getNodeText(doc, tagName);
        if(contents == null)
            return(-1);

        int ret = -1;
        try
        {
            ret = Integer.parseInt(contents.getNodeValue());
        }
        catch(NumberFormatException nfe)
        {
            return(-1);
        }
        return(ret);
    }

    public void init() throws Exception
    {
        PageDownloadJob bookXML = new PageDownloadJob("book.xml for page range and title", new URL(url, "books/db/book.xml"), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Document d = DownloaderUtils.makeDocument(page);
                Element doc = d.getDocumentElement();

                // get start and total
                start = getIntContents(doc, "start");
                total = getIntContents(doc, "total");
                if(start == -1 || total == -1)
                    throw new Exception("No page range");

                Node nameContents = DownloaderUtils.getNodeText(doc, "name");
                if(nameContents == null)
                    throw new Exception("No name");
                title = nameContents.getNodeValue();

                Node typeContents = DownloaderUtils.getNodeText(doc, "to_type");
                if(typeContents == null)
                    throw new Exception("No type");
                type = typeContents.getNodeValue();

                w = getIntContents(doc, "w");
                h = getIntContents(doc, "h");
                // no error checking here
            }
        };
        PageDownloadJob viewerXML = new PageDownloadJob("viewer.xml for zoom level", new URL(url, "books/db/viewer.xml"), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Document d = DownloaderUtils.makeDocument(page);
                Element doc = d.getDocumentElement();

                Node zoomsContents = DownloaderUtils.getNodeText(doc, "zoom_s");
                if(zoomsContents == null)
                    throw new Exception("No zoom element");
                String[] zooms = zoomsContents.getNodeValue().split(",");
                if(zooms.length == 0)
                    throw new Exception("No zoom values");
                zoom = zooms[zooms.length - 1];
            }
        };
        downloader().addJob(bookXML);
        downloader().addJob(viewerXML);
    }

    public void download(File directory) throws Exception
    {
        final File finalDirectory = directory;
        if(type.equals("normal"))
        {
            if(w == -1 || h == -1)
                throw new Exception("No dimensions");
            final float zoomVal = Float.parseFloat(zoom);
            final int gridW = (int)Math.ceil(w * zoomVal / DOKI_GRID_W);
            final int gridH = (int)Math.ceil(h * zoomVal / DOKI_GRID_H);
            for(int i = start; i < start + total; i++)
            {
                final int finalIndex = i;
                final ImageDownloadJob grid[][] = new ImageDownloadJob[gridW][gridH];
                for(int y = 0; y < gridH; y++)
                {
                    for(int x = 0; x < gridW; x++)
                    {
                        grid[x][y] = new ImageDownloadJob("Page " + i + " (" + x + ", " + y + ")",
                                        new URL(url, "books/images/" + zoom + "/g_" + i + "/x" + (x + 1) + "y" + (y + 1) + ".jpg"));
                        downloader().addJob(grid[x][y]);
                    }
                }
                DownloadJob combine = new DownloadJob("Combine page " + i)
                {
                    public void run() throws Exception
                    {
                        BufferedImage complete = new BufferedImage((int)(w * zoomVal),
                                                                    (int)(h * zoomVal),
                                                                    BufferedImage.TYPE_INT_RGB);

                        Graphics2D g = complete.createGraphics();
                        for(int y = 0; y < gridH; y++)
                        {
                            for(int x = 0; x < gridW; x++)
                            {
                                g.drawImage(grid[x][y].getImage(), x * DOKI_GRID_W, y * DOKI_GRID_H, null);
                            }
                        }
                        ImageIO.write(complete, "JPEG", DownloaderUtils.fileName(finalDirectory, title, finalIndex, "jpg"));
                    }
                };
                downloader().addJob(combine);
            }
        }
        else if(type.equals("rich"))
        {
            for(int i = start; i < start + total; i++)
            {
                FileDownloadJob page = new FileDownloadJob("Page " + i, new URL(url, "books/images/" + zoom + "/" + i + ".jpg"),
                                                                        DownloaderUtils.fileName(directory, title, i, "jpg"));
                downloader().addJob(page);
            }
        }
        else
        {
            throw new Exception("This type of Actibook is unknown: \"" + type + ".\"  Please report this bug.");
        }
    }
}
