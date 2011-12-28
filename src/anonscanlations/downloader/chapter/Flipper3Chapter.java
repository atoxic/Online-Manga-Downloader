package anonscanlations.downloader.chapter;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.imageio.*;

import org.jsoup.nodes.*;

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
        DownloaderUtils.checkHTTP(url);
        url = DownloaderUtils.getRedirectURL(url);
        JSoupDownloadJob page = new JSoupDownloadJob("Get book.xml", new URL(url, "book.xml"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                
                Document d = response.parse();
                title = JSoupUtils.elementText(d, "bookTitle");
                total = JSoupUtils.elementTextInt(d, "total");
                maxMagString = JSoupUtils.elementText(d, "maxMagnification");
                maxMag = Float.parseFloat(maxMagString);
                pageWidth = JSoupUtils.elementTextInt(d, "pageWidth");
                pageHeight = JSoupUtils.elementTextInt(d, "pageHeight");
                sliceWidth = JSoupUtils.elementTextInt(d, "sliceWidth");
                sliceHeight = JSoupUtils.elementTextInt(d, "sliceHeight");
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
        
        final int gridW = (int)Math.ceil(1.0f * pageWidth * maxMag / sliceWidth);
        final int gridH = (int)Math.ceil(1.0f * pageHeight * maxMag / sliceHeight);
        DownloaderUtils.debug("grid: " + gridW + ", " + gridH);
        for(int i = 1; i <= total; i++)
        {
            final File f = DownloaderUtils.fileName(directory, title, i, "jpg");
            if(f.exists())
                continue;
            
            final ImageDownloadJob _grid[][] = new ImageDownloadJob[gridW][gridH];
            for(int y = 0; y < gridH; y++)
            {
                for(int x = 0; x < gridW; x++)
                {
                    _grid[x][y] = new ImageDownloadJob("Page " + i + " (" + x + ", " + y + ")",
                                    new URL(url, "page" + i + "/x" + maxMagString + "/" + (x + 1 + y * gridW) + ".jpg"));
                    list.add(_grid[x][y]);
                }
            }
            CombineDownloadJob combine = new CombineDownloadJob("Combine page " + i, f, (int)(pageWidth * maxMag),
                                                                (int)(pageHeight * maxMag), sliceWidth, sliceHeight)
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
}
