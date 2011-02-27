/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.pcviewer;

import java.util.*;
import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class PCViewerChapter extends Chapter implements Serializable
{
    protected HashMap<String, String> params;
    private String xmlurl, dataFolder;
    private int rangeStart, rangeEnd;

    public PCViewerChapter(){}
    public PCViewerChapter(String url, String[] myParams)
    {
        params = new HashMap<String, String>();
        for(String p : myParams)
        {
            params.put(p, getParam(url, p));
        }
        try
        {
            xmlurl = URLDecoder.decode(getParam(url, "xmlurl"), "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            DownloaderUtils.error("Couldn't decode xmlurl with UTF-8", e, false);
        }
        rangeStart = rangeEnd = 0;
    }

    protected String getParams()
    {
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, String> p : params.entrySet())
            sb.append(p.getKey() + "=" + p.getValue() + "&");
        sb.deleteCharAt(sb.length() - 1);
        return(sb.toString());
    }

    public void parseXML() throws IOException
    {
        URL xml = new URL(new URL(xmlurl), "content_dl.php?dtype=0&" + getParams() + "&z=&x=0&re=0&ad=0&pre=&p=");
        String page = DownloaderUtils.getPage(xml.toString(), "EUC-JP");

        int index = page.indexOf("<DataFileFolder path=\"");
        dataFolder = page.substring(index + 22, page.indexOf("\">", index));

        index = page.indexOf("<SamplePageList list=\"");
        String rangeString = page.substring(index + 22, page.indexOf("\"", index + 22));
        String range[] = rangeString.split("-");
        
        rangeStart = Integer.parseInt(range[0]);
        rangeEnd = Integer.parseInt(range[1]);
    }

    private String getParam(String url, String param)
    {
        int index = url.indexOf(param + "="), endIndex = url.indexOf('&', index);
        if(endIndex == -1)
            endIndex = url.length();
        return(url.substring(index + param.length() + 1, endIndex));
    }

    public String getTitle()
    {
        String key1 = params.get("key1");
        return(key1.substring(key1.lastIndexOf('-') + 1));
    }

    public int getMin()
    {
        return(rangeStart);
    }

    public int getMax()
    {
        return(rangeEnd);
    }

    public boolean download(DownloadListener dl) throws IOException
    {
        URL baseURL = new URL(xmlurl);

        File temp = File.createTempFile("pcviewer_temp", ".bin");

        for(int i = rangeStart; i <= rangeEnd; i++)
        {
            if(dl.isDownloadAborted())
                return(true);

            URL url = new URL(baseURL,
                        "content_dl.php?dtype=1&" + getParams() + "&z=&x=0&re=0&ad=0&pre=&pno=" + i + "&p=" + dataFolder);
            DownloaderUtils.downloadFile(url, temp.getAbsolutePath());
            PCViewerDecrypt.decryptFile(temp.getAbsolutePath(),
                    dl.downloadPath(this, i));

            dl.downloadProgressed(this, i);
        }
        temp.delete();

        dl.downloadFinished(this);

        return(true);
    }
}
