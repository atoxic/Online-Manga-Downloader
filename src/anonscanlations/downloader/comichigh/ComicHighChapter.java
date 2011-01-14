/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.comichigh;

import java.util.*;
import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ComicHighChapter extends Chapter implements Serializable
{
    private Series series;
    private String title, url, downloadURL;
    private int total, sIS;

    public ComicHighChapter(Series mySeries, Map<String, Object> yamlMap)
    {
        series = mySeries;
        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");
        downloadURL = (String)yamlMap.get("dlURL");
        total = (Integer)yamlMap.get("total");
        sIS = (Integer)yamlMap.get("sIS");
    }

    public ComicHighChapter(Series mySeries, String myTitle, String myURL)
    {
        series = mySeries;
        title = myTitle;
        url = myURL;
        
        downloadURL = null;
        total = 0;
        sIS = 0;
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("url", url);
        ret.put("dlURL", downloadURL);
        ret.put("total", total);
        ret.put("sIS", sIS);

        return(ret);
    }

    public boolean parsePages() throws IOException
    {
        String indexPage = DownloaderUtils.getPage(url + "/index.shtml", "Shift_JIS");
        
        // content Key Value
        String cKV = title(indexPage, "'cKV'");
        DownloaderUtils.debug("\t\t\tcKV: " + cKV);
        int cKVInt = Integer.parseInt(cKV);

        // root directory
        String hCN = title(indexPage, "'hCN'");
        DownloaderUtils.debug("\t\t\thCN: " + hCN);

        String initValEncoded = DownloaderUtils.getPage(url + "/InitVal.html", "Shift_JIS");

        int index = initValEncoded.indexOf("<DIV ID='DATA'>");
        if(index == -1)
            return(false);
        String initVal = ComicHighSite.expand(initValEncoded.substring(index + 15, initValEncoded.indexOf("</DIV>", index)), cKVInt);

        // page number
        String tPN = title(initVal, "'tPN'");
        total = Integer.parseInt(tPN) - 1;
        DownloaderUtils.debug("\t\t\ttotal: " + total);

        // for decoding page individual files
        String sISString = title(initVal, "'sIS'");
        sIS = Integer.parseInt(sISString);
        DownloaderUtils.debug("\t\t\tsIS: " + sIS);

        // title
        String sHN = title(initVal, "'sHN'");
        DownloaderUtils.debug("\t\t\tsIS: " + sHN);

        DownloaderUtils.debug("\t\t\ttarget: http://futabasha.pluginfree.com/cgi-bin/widget.cgi?a=" + hCN + sHN + "/" + sHN);
        DownloaderUtils.debug("\t\t\t" + ComicHighSite.getIpntStr(sIS, 1, 0, 0, 0));

        return(true);
    }

    public Series getSeries()
    {
        return(series);
    }

    public String getTitle()
    {
        return(title);
    }

    public int getMin()
    {
        return(1);
    }
    public int getMax()
    {
        return(total);
    }

    public boolean download(DownloadListener dl) throws IOException
    {


        return(true);
    }

    private String title(String page, String ID)
    {
        int index = page.indexOf("ID=" + ID);
        if(index != -1)
        {
            index = page.indexOf("title='", index);
            if(index != -1)
                return(page.substring(index + 7, page.indexOf('\'', index + 7)));
        }
        return(null);
    }
}
