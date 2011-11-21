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

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

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

    private static int parseInt(String str)
    {
        int ret = -1;
        try
        {
            ret = Integer.parseInt(str);
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

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    private Stack<String> tags = new Stack<String>();

                    @Override
                    public void characters(char[] ch, int off, int length)
                    {
                        if(tags.size() == 2)
                        {
                            String tag = tags.peek(), str = new String(ch, off, length);
                            if(tag.equals("name"))          title = str;
                            else if(tag.equals("to_type"))  type = str;
                            else if(tag.equals("w"))        w = parseInt(str);
                            else if(tag.equals("h"))        h = parseInt(str);
                            else if(tag.equals("start"))    start = parseInt(str);
                            else if(tag.equals("total"))    total = parseInt(str);
                        }
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        tags.push(localName);
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName)
                    {
                        tags.pop();
                    }
                });
                parser.parse(is);
            }
        };
        PageDownloadJob viewerXML = new PageDownloadJob("viewer.xml for zoom level", new URL(url, "books/db/viewer.xml"), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    private boolean inZoomSTag = false;

                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        if(inZoomSTag)
                        {
                            String zooms = new String(ch, start, length);
                            String[] zoomLevels = zooms.split(",");
                            zoom = zoomLevels[zoomLevels.length - 1];
                        }
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        if(localName.equals("zoom_s"))
                            inZoomSTag = true;
                    }
                    @Override
                    public void endElement(String uri, String localName, String qName) throws SAXException
                    {
                        if(localName.equals("zoom_s"))
                            throw DownloaderUtils.DONE;
                    }
                });
                try
                {
                    parser.parse(is);
                }
                catch(SAXException e)
                {
                    if(!e.equals(DownloaderUtils.DONE))
                        throw e;
                }
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
