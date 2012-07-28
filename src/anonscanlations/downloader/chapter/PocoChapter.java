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
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PocoChapter extends Chapter implements Serializable
{
    protected ArrayList<String> images;

    public PocoChapter(URL _url)
    {
        super(_url);
        
        images = new ArrayList<String>();
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        HashMap<String, String> params = DownloaderUtils.getQueryMap(url);

        String partid = params.get("partid");
        if(partid == null)
            throw new Exception("Invalid URL: partid not found");

        JSoupDownloadJob load = new JSoupDownloadJob("Get data", new URL("http://www.poco2.jp/viewerset/common/load.php"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                
                // 2012-07-27: Found out that some dumbasses who work at Poco don't encode ampersands
                String cleaned = response.body().replace("&", "%24");

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(cleaned));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        if(localName.equals("File"))
                        {
                            title = atts.getValue("title") + "_" + atts.getValue("story");
                        }
                        else if(localName.equals("Page"))
                        {
                            images.add(atts.getValue("image"));
                        }
                    }
                });
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };
        load.addPOSTData("partid", partid);

        list.add(load);

        return(list);
    }
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        int i = 1;
        for(String image : images)
        {
            File f = DownloaderUtils.fileName(directory, i, "jpg");
            if(f.exists())
            {
                i++;
                continue;
            }
            FileDownloadJob page = new FileDownloadJob(DownloaderUtils.pageOutOf(i, 1, images.size()), new URL(image), f);
            list.add(page);
            i++;
        }

        return(list);
    }
}
