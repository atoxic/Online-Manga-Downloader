/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ganganonline;

import java.util.*;
import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class GanGanOnlineChapter extends Chapter implements Serializable
{
    private Series series;
    private String title, url;
    private int start, total;

    public GanGanOnlineChapter(Series mySeries, Map<String, Object> yamlMap)
    {
        series = mySeries;
        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");
        start = (Integer)yamlMap.get("start");
        total = (Integer)yamlMap.get("total");
    }

    public GanGanOnlineChapter(Series mySeries, String myTitle, String myURL)
    {
        series = mySeries;
        title = myTitle;
        url = myURL;
        start = 0;
        total = 0;
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("url", url);
        ret.put("start", start);
        ret.put("total", total);

        return(ret);
    }

    public void parseXML() throws IOException
    {
        URL xmlURL = new URL(new URL(url), "db/book.xml");
        String page = DownloaderUtils.getPage(xmlURL.toString(), "UTF-8");
        int index = 0;

        index = page.indexOf("<start>");
        String startString = page.substring(index + 7, page.indexOf("</start>", index));

        index = page.indexOf("<total>");
        String totalString = page.substring(index + 7, page.indexOf("</total>", index));

        start = Integer.parseInt(startString);
        total = Integer.parseInt(totalString);
    }

    public Series getSeries()
    {
        return(series);
    }

    public String getTitle()
    {
        return(title);
    }

    public int getNumPages()
    {
        return(total);
    }

    public boolean download(DownloadListener dl) throws IOException
    {
        URL baseURL = new URL(new URL(url), "images/2/");

        String saveTitle = series.getTranslatedTitle() + "_c" + getTitle() + "_";
        saveTitle = saveTitle.replace(' ', '_');

        for(int i = start;
            DownloaderUtils.downloadFile(new URL(baseURL, i + ".jpg"),
                saveTitle + String.format("%03d", i) + ".jpg");
            i++)
        {
            if(dl.isDownloadAborted())
                return(true);
            dl.downloadProgressed(this, i - (start - 1));
        }

        dl.downloadFinished(this);

        return(true);
    }
}
