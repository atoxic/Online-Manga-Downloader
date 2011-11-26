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
import anonscanlations.downloader.downloadjobs.*;

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
        params = null;
        rangeStart = rangeEnd = 0;
        title = "";
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
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);

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
            PCViewerDownloadJob job = new PCViewerDownloadJob("Page " + i,
                        new URL(baseURL,
                            "content_dl.php?dtype=1&p=" + dataFolder + "&z=&x=0&re=0&ad=0&pre=&pno=" + i + "&" + getParams()),
                        DownloaderUtils.fileName(directory, title, i, "jpg"));
            list.add(job);
        }
        return(list);
    }
}
