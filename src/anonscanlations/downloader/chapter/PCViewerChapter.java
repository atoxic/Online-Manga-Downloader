/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

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
    private String dataFolder, title;
    private int rangeStart, rangeEnd;
    private URL url;

    public PCViewerChapter(URL _url)
    {
        url = _url;
        params = DownloaderUtils.getQueryMap(url);
        rangeStart = rangeEnd = 0;
        title = "";

        DownloaderUtils.debug("PCVC Given URL: " + url);

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(entry.getKey().startsWith("key"))
            {
                title += entry.getValue();
            }
        }
    }

    protected String getParams()
    {
        String ret = "";
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(entry.getKey().startsWith("key") || entry.getKey().startsWith("shd"))
            {
                ret += entry.getKey() + "=" + entry.getValue() + "&";
            }
        }
        return(ret);
    }

    public void init() throws Exception
    {
        PageDownloadJob xml = new PageDownloadJob("Get XML file", 
                                    new URL(new URL(params.get("xmlurl")),
                                            "content_dl.php?dtype=0&z=&x=0&re=0&ad=0&pre=&p=&" + getParams()),
                                    "EUC-JP")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                DownloaderUtils.debug("PCVC XML: " + page);

                int index = page.indexOf("<DataFileFolder path=\"");
                dataFolder = page.substring(index + 22, page.indexOf("\">", index));

                index = page.indexOf("<SamplePageList list=\"");
                String rangeString = page.substring(index + 22, page.indexOf("\"", index + 22));
                String range[] = rangeString.split("-");

                rangeStart = Integer.parseInt(range[0]);
                rangeEnd = Integer.parseInt(range[1]);

                DownloaderUtils.debug("PCVC dataFolder: " + dataFolder);
                DownloaderUtils.debug("PCVC rangeStart: " + rangeStart);
                DownloaderUtils.debug("PCVC rangeEnd: " + rangeEnd);
            }
        };
        downloader().addJob(xml);
    }

    public void download(File directory) throws Exception
    {
        URL baseURL = new URL(params.get("xmlurl"));

        for(int i = rangeStart; i <= rangeEnd; i++)
        {
            PCViewerDownloadJob job = new PCViewerDownloadJob("Page " + i,
                        new URL(baseURL,
                            "content_dl.php?dtype=1&p=" + dataFolder + "&z=&x=0&re=0&ad=0&pre=&pno=" + i + "&" + getParams()),
                        DownloaderUtils.fileName(directory, title, i, "jpg"));
            downloader().addJob(job);
        }
    }
}
