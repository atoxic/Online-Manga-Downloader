/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

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

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        if(localName.equals("DataFileFolder"))
                        {
                            dataFolder = atts.getValue("path");
                        }
                        else if(localName.equals("SamplePageList"))
                        {
                            String range[] = atts.getValue("list").split("-");

                            rangeStart = Integer.parseInt(range[0]);
                            rangeEnd = Integer.parseInt(range[1]);
                        }
                    }
                });
                parser.parse(is);

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
