/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.yahoocomic;

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
    private String title;

    private TreeMap<String, Chapter> chapters;

    public YahooComicSeries(Magazine myMagazine, Map<String, Object> yamlMap)
    {
        super(myMagazine);

        title = (String)yamlMap.get("title");
        chapters = new TreeMap<String, Chapter>();
    }

    public YahooComicSeries(Magazine myMagazine)
    {
        super(myMagazine);

        title = "";
        chapters = new TreeMap<String, Chapter>();
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);

        return(ret);
    }

    public void addChapter(Chapter chapter)
    {
        chapters.put(chapter.getTitle(), chapter);
    }

    public String getOriginalTitle(){ return(title); }
    public Collection<Chapter> getChapters(){ return(chapters.values()); }

    public void parsePage(String url) throws IOException
    {
        String page = DownloaderUtils.getPage(url, "EUC-JP");
        int index = page.indexOf("/series_main.jpg\" alt=\"");
        title = DownloaderUtils.unescapeHTML(page.substring(index + 23, page.indexOf("\"></td>", index)));

        index = 0;
        while((index = page.indexOf("javascript:freeview('", index + 1)) != -1)
        {
            String string = page.substring(index + 21, page.indexOf("')", index));
            
            if(chapters.containsKey(string))
                continue;

            YahooComicChapter chapter = new YahooComicChapter(string);
            chapter.setSeries(this);
            chapter.parseXML();
            chapters.put(string, chapter);
        }
    }
}

