/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public class PCViewerChapter extends Chapter implements Serializable
{
    protected HashMap<String, String> params;
    private String dataFolder;
    private int rangeStart, rangeEnd;

    public PCViewerChapter(URL _url)
    {
        super(_url);
        
        title = "";
        params = null;
        dataFolder = null;
        rangeStart = rangeEnd = 0;
    }

    // generate params for URL query string
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

    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        DownloaderUtils.debug("PCVC Given URL: " + url);
        params = DownloaderUtils.getQueryMap(url);

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(entry.getKey().startsWith("key"))
            {
                title += entry.getValue();
            }
        }

        JSoupDownloadJob xml = new JSoupDownloadJob("Get XML file", 
                                    new URL(new URL(params.get("xmlurl")),
                                            "content_dl.php?dtype=0&z=&x=0&re=0&ad=0&pre=&p=&" + getParams()))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                DownloaderUtils.debug("PCVC XML: " + response.body());
                Document d = response.parse();
                dataFolder = JSoupUtils.elementAttr(d, "DataFileFolder", "path");

                String pageList = JSoupUtils.elementAttr(d, "SamplePageList", "list");
                String range[] = pageList.split("-");
                if(range.length == 2)
                {
                    rangeStart = Integer.parseInt(range[0]);
                    rangeEnd = Integer.parseInt(range[1]);
                }
                else
                {
                    rangeStart = 0;
                    rangeEnd = Integer.parseInt(JSoupUtils.elementAttr(d, "TotalPageCount", "pagecount")) - 1;
                }

                DownloaderUtils.debug("PCVC dataFolder: " + dataFolder);
                DownloaderUtils.debug("PCVC rangeStart: " + rangeStart);
                DownloaderUtils.debug("PCVC rangeEnd: " + rangeEnd);
            }
        };
        list.add(xml);

        return(list);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        URL baseURL = new URL(params.get("xmlurl"));

        for(int i = rangeStart; i <= rangeEnd; i++)
        {
            final File f = DownloaderUtils.fileName(directory, i, "jpg");
            if(f.exists())
                continue;
            PCViewerDownloadJob job = new PCViewerDownloadJob(DownloaderUtils.pageOutOf(i, rangeStart, rangeEnd - rangeStart + 1),
                        new URL(baseURL,
                            "content_dl.php?dtype=1&p=" + dataFolder + "&z=&x=0&re=0&ad=0&pre=&pno=" + i + "&" + getParams()),
                        f);
            list.add(job);
        }
        return(list);
    }
}
