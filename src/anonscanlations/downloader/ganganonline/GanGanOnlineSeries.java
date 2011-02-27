/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ganganonline;

import anonscanlations.downloader.actibook.ActibookChapter;
import anonscanlations.downloader.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public class GanGanOnlineSeries extends Series implements Serializable
{
    private String url, title;

    public GanGanOnlineSeries(){}

    public GanGanOnlineSeries(String titlePanel) throws MalformedURLException
    {
        int urlIndex = titlePanel.indexOf("href=\"");
        url = new URL(new URL(GanGanOnlineSite.ROOT),
                titlePanel.substring(urlIndex + 6, titlePanel.indexOf("\">", urlIndex))).toString();

        int titleIndex = titlePanel.indexOf("alt=\"");
        title = DownloaderUtils.unescapeHTML(titlePanel.substring(titleIndex + 5, titlePanel.indexOf("\" />", titleIndex)));
    }

    public void parsePage() throws IOException
    {
        String page = DownloaderUtils.getPage(url, "Shift_JIS");
        TreeSet<String> strings = new TreeSet<String>();

        int index = 0;
        while((index = page.indexOf("javascript:Fullscreen('viewer/", index + 1)) != -1)
        {
            String string = page.substring(index + 30, page.indexOf("/_SWF_Window.html');", index));
            if(strings.contains(string))
                continue;
            strings.add(string);
            
            String urlString = (new URL(new URL(url), "viewer/" + string + "/books/")).toString();

            ActibookChapter chapter = new ActibookChapter(string, urlString);

            try
            {
                if(chapter.parseXML())
                    // add only if it doesn't get an error
                    addChapter(chapter);
            }
            catch(Exception e)
            {
                // non-fatal; don't freak out
                DownloaderUtils.error("Couldn't get chapter: " + urlString, e, false);
            }
        }
    }

    public String getURL() { return(url); }
    public String getOriginalTitle(){ return(title); }
}
