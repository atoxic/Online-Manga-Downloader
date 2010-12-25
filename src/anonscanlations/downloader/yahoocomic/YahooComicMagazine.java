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
public class YahooComicMagazine extends Magazine
{
    private TreeMap<String, Series> series;

    private String title;

    public YahooComicMagazine()
    {
        series = new TreeMap<String, Series>();
    }

    public YahooComicMagazine(Map<String, Object> yamlMap)
    {
        this();

        title = (String)yamlMap.get("title");
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);

        return(ret);
    }

    public void addSeries(Series s)
    {
        series.put(s.getOriginalTitle(), s);
    }

    public String getOriginalTitle()
    {
        return(title);
    }
    public Collection<Series> getSeries() { return(series.values()); }

    public void parsePage(String url) throws IOException
    {
        String page = DownloaderUtils.getPage(url, "EUC-JP");
        int index = page.indexOf("/magazine_main.jpg\" alt=\"");
        title = page.substring(index + 25, page.indexOf("\"></td>", index));

        HashMap<Thread, YahooComicSeries> threads = new HashMap<Thread, YahooComicSeries>();

        index = 0;
        while((index = page.indexOf("<small><a href=\"/magazine/", index + 1)) != -1)
        {
            final String string = (new URL(new URL(url), page.substring(index + 26, page.indexOf("\">", index)))).toString();

            final YahooComicSeries comicSeries = new YahooComicSeries(this);
            // multithread it to increase speed
            Thread t = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        comicSeries.parsePage(string);
                    }
                    catch(Exception e)
                    {
                        DownloaderUtils.error("Couldn't parse Yahoo! Comic page", e, false);
                    }
                }
            };

            t.start();
            threads.put(t, comicSeries);
        }

        for(Map.Entry<Thread, YahooComicSeries> t: threads.entrySet())
        {
            try
            {
                t.getKey().join();
                series.put(t.getValue().getOriginalTitle(), t.getValue());
            }
            catch(Exception e)
            {
                DownloaderUtils.error("Couldn't join thread", e, false);
            }
        }
    }
}
