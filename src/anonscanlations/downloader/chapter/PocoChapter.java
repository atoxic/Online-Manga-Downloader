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
    protected URL url;
    protected String title, story;
    protected ArrayList<String> images;

    protected PocoChapter(){}
    public PocoChapter(URL _url)
    {
        url = _url;
        title = story = null;
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

        PageDownloadJob load = new PageDownloadJob("Get data", new URL("http://www.poco2.jp/viewerset/common/load.php"), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        if(localName.equals("File"))
                        {
                            title = atts.getValue("title");
                            story = atts.getValue("story");
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
        load.setPOSTData("partid=" + partid);

        list.add(load);

        return(list);
    }
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        int i = 1;
        for(String image : images)
        {
            File f = DownloaderUtils.fileName(directory, title + "_" + story, i, "jpg");
            if(f.exists())
                continue;
            FileDownloadJob page = new FileDownloadJob("Page " + i, new URL(image), f);
            list.add(page);
            i++;
        }

        return(list);
    }
}
