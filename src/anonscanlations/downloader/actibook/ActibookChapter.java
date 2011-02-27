/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.actibook;

import java.util.*;
import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ActibookChapter extends Chapter implements Serializable
{
    private String title, url;
    private int start, total;

    public ActibookChapter(Map<String, Object> yamlMap)
    {
        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");
        start = (Integer)yamlMap.get("start");
        total = (Integer)yamlMap.get("total");
    }

    public ActibookChapter(String myTitle, String myURL)
    {
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

    public String getTitle()
    {
        return(title);
    }

    public int getMin()
    {
        return(start);
    }
    public int getMax()
    {
        return(total - start);
    }

    public boolean download(DownloadListener dl) throws IOException
    {
        URL baseURL = new URL(new URL(url), "images/2/");

        for(int i = start;
            DownloaderUtils.downloadFile(new URL(baseURL, i + ".jpg"),
                dl.downloadPath(this, i));
            i++)
        {
            if(dl.isDownloadAborted())
                return(true);
            dl.downloadProgressed(this, i);
        }

        dl.downloadFinished(this);

        return(true);
    }
}
