/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.yahoocomic;

import java.util.*;
import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.pcviewer.*;

/**
 *
 * @author /a/non
 */
public class YahooComicChapter extends Chapter implements Serializable
{
    private Series series;
    private String key1, xmlurl, shd, dataFolder;
    private int rangeStart, rangeEnd;

    public YahooComicChapter(Series mySeries, Map<String, Object> yamlMap)
    {
        series = mySeries;
        key1 = (String)yamlMap.get("key1");
        xmlurl = (String)yamlMap.get("xmlurl");
        dataFolder = (String)yamlMap.get("dataFolder");
        shd = (String)yamlMap.get("shd");
        rangeStart = (Integer)yamlMap.get("rangeStart");
        rangeEnd = (Integer)yamlMap.get("rangeEnd");
    }

    public YahooComicChapter(Series mySeries, String url)
    {
        series = mySeries;

        key1 = getParam(url, "key1");
        try
        {
            xmlurl = URLDecoder.decode(getParam(url, "xmlurl"), "UTF-8");
        }
        catch(Exception e)
        {
            // eat it up
        }
        shd = getParam(url, "shd");
        rangeStart = rangeEnd = 0;
    }
    
    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("key1", key1);
        ret.put("xmlurl", xmlurl);
        ret.put("shd", shd);
        ret.put("dataFolder", dataFolder);
        ret.put("rangeStart", rangeStart);
        ret.put("rangeEnd", rangeEnd);

        return(ret);
    }

    public void parseXML() throws IOException
    {
        URL xml = new URL(new URL(xmlurl), "/content_dl.php?dtype=0&key1=" + key1 + "&z=&x=0&shd=" + shd + "&re=0&ad=0&pre=&p=");
        String page = DownloaderUtils.getPage(xml.toString(), "EUC-JP");

        int index = page.indexOf("<DataFileFolder path=\"");
        dataFolder = page.substring(index + 22, page.indexOf("\">", index));

        index = page.indexOf("<SamplePageList list=\"");
        String rangeString = page.substring(index + 22, page.indexOf("\">", index));
        String range[] = rangeString.split("-");

        try
        {
            rangeStart = Integer.parseInt(range[0]);
            rangeEnd = Integer.parseInt(range[1]);
        }
        catch(NumberFormatException nfe)
        {
            // EAT IT
        }
    }

    private String getParam(String url, String param)
    {
        int index = url.indexOf(param + "="), endIndex = url.indexOf('&', index);
        if(endIndex == -1)
            endIndex = url.length();
        return(url.substring(index + param.length() + 1, endIndex));
    }

    public Series getSeries()
    {
        return(series);
    }

    public String getTitle()
    {
        return(key1.substring(key1.lastIndexOf('-') + 1));
    }

    public int getNumPages()
    {
        return(rangeEnd - rangeStart);
    }

    public boolean download(DownloadListener dl) throws IOException
    {
        URL baseURL = new URL(xmlurl);

        String saveTitle = series.getTranslatedTitle() + "_c" + getTitle() + "_";
        saveTitle = saveTitle.replace(' ', '_');

        File temp = File.createTempFile("pcviewer_temp", ".bin");

        for(int i = rangeStart; i <= rangeEnd; i++)
        {
            if(dl.isDownloadAborted())
                return(true);

            URL url = new URL(baseURL,
                        "content_dl.php?dtype=1&key1=" + key1 + "&z=&x=0&shd=" +
                            shd + "&re=0&ad=0&pre=&pno=" + i + "&p=" + dataFolder);
            DownloaderUtils.downloadFile(url, temp.getAbsolutePath());
            PCViewerDecrypt.decryptFile(temp.getAbsolutePath(),
                    saveTitle + String.format("%03d", i) + ".jpg");

            dl.downloadProgressed(this, i - (rangeStart - 1));
        }

        dl.downloadFinished(this);

        return(true);
    }
}
