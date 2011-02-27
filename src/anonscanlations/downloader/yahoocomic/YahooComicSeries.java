/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.yahoocomic;

import anonscanlations.downloader.pcviewer.PCViewerChapter;
import java.util.*;
import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class YahooComicSeries extends Series
{
    private static final String[] PARAMS = {"key1", "shd"};

    private String title;

    public YahooComicSeries()
    {
        title = "";
    }

    public String getOriginalTitle(){ return(title); }

    public void parsePage(String url) throws IOException
    {
        String page = DownloaderUtils.getPage(url, "EUC-JP");
        int index = page.indexOf("/series_main.jpg\" alt=\"");
        title = DownloaderUtils.unescapeHTML(page.substring(index + 23, page.indexOf("\"></td>", index)));
        
        TreeSet<String> strings = new TreeSet<String>();

        index = 0;
        while((index = page.indexOf("javascript:freeview('", index + 1)) != -1)
        {
            String string = page.substring(index + 21, page.indexOf("')", index));
            
            if(strings.contains(string))
                continue;
            strings.add(string);

            PCViewerChapter chapter = new PCViewerChapter(string, PARAMS);
            chapter.parseXML();
            addChapter(chapter);
        }
    }
}

