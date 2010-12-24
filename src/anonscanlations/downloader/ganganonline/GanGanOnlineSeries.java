/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ganganonline;

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
    private TreeMap<String, Chapter> chapters;

    public GanGanOnlineSeries(Magazine myMagazine, Map<String, Object> yamlMap)
    {
        super(myMagazine);

        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");

        chapters = new TreeMap<String, Chapter>();
    }

    public GanGanOnlineSeries(Magazine myMagazine, String titlePanel) throws MalformedURLException
    {
        super(myMagazine);

        int urlIndex = titlePanel.indexOf("href=\"");
        url = new URL(new URL(GanGanOnlineSite.ROOT),
                titlePanel.substring(urlIndex + 6, titlePanel.indexOf("\">", urlIndex))).toString();

        int titleIndex = titlePanel.indexOf("alt=\"");
        title = DownloaderUtils.unescapeHTML(titlePanel.substring(titleIndex + 5, titlePanel.indexOf("\" />", titleIndex)));

        chapters = new TreeMap<String, Chapter>();
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("url", url);

        return(ret);
    }

    public void addChapter(Chapter chapter)
    {
        chapters.put(chapter.getTitle(), chapter);
    }

    public void parsePage() throws IOException
    {
        String page = DownloaderUtils.getPage(url, "Shift_JIS");

        int index = 0;
        while((index = page.indexOf("javascript:Fullscreen('viewer/", index + 1)) != -1)
        {
            String string = page.substring(index + 30, page.indexOf("/_SWF_Window.html');", index));
            if(chapters.containsKey(string))
                continue;
            
            String urlString = (new URL(new URL(url), "viewer/" + string + "/books/")).toString();

            GanGanOnlineChapter chapter = new GanGanOnlineChapter(this, string, urlString);
            try
            {
                chapter.parseXML();
                // add only if it doesn't get an error
                chapters.put(string, chapter);
            }
            catch(IOException ioe)
            {

            }
        }
    }

    public String getURL() { return(url); }
    public String getOriginalTitle(){ return(title); }
    public Collection<Chapter> getChapters() { return(chapters.values()); }
}
