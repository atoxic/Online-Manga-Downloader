package anonscanlations.downloader.chapter;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.imageio.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * Grand Jump, Monthly Heroes
 * @author /a/non
 */
public class Flipper3Chapter extends Chapter
{
    private URL url;
    private String title, maxMagString;
    private int total, pageWidth, pageHeight, sliceWidth, sliceHeight;
    // float just in case
    private float maxMag;
    
    public Flipper3Chapter(URL _url)
    {
        url = _url;
        title = null;
        total = 0;
        maxMag = 1;
        maxMagString = "1";
        pageWidth = pageHeight = sliceWidth = sliceHeight = 0;
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        PageDownloadJob page = new PageDownloadJob("Get book.xml", new URL(url, "book.xml"), "UTF-8")
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
                    public void characters(char[] ch, int start, int length)
                    {
                        String tag = tags.peek(), str = new String(ch, start, length);
                        if(tag.equals("bookTitle"))
                            title = str;
                        else if(tag.equals("total"))
                            total = Integer.parseInt(str);
                        else if(tag.equals("maxMagnification"))
                        {
                            maxMag = Float.parseFloat(str);
                            maxMagString = str;
                        }
                        else if(tag.equals("pageWidth"))
                            pageWidth = Integer.parseInt(str);
                        else if(tag.equals("pageHeight"))
                            pageHeight = Integer.parseInt(str);
                        else if(tag.equals("sliceWidth"))
                            sliceWidth = Integer.parseInt(str);
                        else if(tag.equals("sliceHeight"))
                            sliceHeight = Integer.parseInt(str);
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
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(page);
        return(list);
    }
    
    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        final File finalDirectory = directory;
        
        final int gridW = (int)Math.ceil(1.0f * pageWidth * maxMag / sliceWidth);
        final int gridH = (int)Math.ceil(1.0f * pageHeight * maxMag / sliceHeight);
        DownloaderUtils.debug("grid: " + gridW + ", " + gridH);
        for(int i = 1; i <= total; i++)
        {
            final int finalIndex = i;
            final ImageDownloadJob grid[][] = new ImageDownloadJob[gridW][gridH];
            for(int y = 0; y < gridH; y++)
            {
                for(int x = 0; x < gridW; x++)
                {
                    grid[x][y] = new ImageDownloadJob("Page " + i + " (" + x + ", " + y + ")",
                                    new URL(url, "page" + i + "/x" + maxMagString + "/" + (x + 1 + y * gridW) + ".jpg"));
                    list.add(grid[x][y]);
                }
            }
            DownloadJob combine = new DownloadJob("Combine page " + i)
            {
                public void run() throws Exception
                {
                    BufferedImage complete = new BufferedImage((int)(pageWidth * maxMag),
                                                                (int)(pageHeight * maxMag),
                                                                BufferedImage.TYPE_INT_RGB);

                    Graphics2D g = complete.createGraphics();
                    for(int y = 0; y < gridH; y++)
                    {
                        for(int x = 0; x < gridW; x++)
                        {
                            g.drawImage(grid[x][y].getImage(), x * sliceWidth, y * sliceHeight, null);
                        }
                    }
                    ImageIO.write(complete, "JPEG", DownloaderUtils.fileName(finalDirectory, title, finalIndex, "jpg"));
                }
            };
            list.add(combine);
        }
        
        return(list);
    }
}
