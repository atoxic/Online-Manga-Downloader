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

    public void init() throws Exception
    {
        HashMap<String, String> params = DownloaderUtils.getQueryMap(url);

        String partid = params.get("partid");
        if(partid == null)
            throw new Exception("Invalid URL: partid not found");

        POSTDownloadJob load = new POSTDownloadJob("Get data", new URL("http://www.poco2.jp/viewerset/common/load.php"), "partid=" + partid)
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                String page = DownloaderUtils.readAllLines(conn.getInputStream(), "UTF-8");

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
                parser.parse(is);
            }
        };

        downloader().addJob(load);
    }
    public void download(File directory) throws Exception
    {
        int i = 1;
        for(String image : images)
        {
            FileDownloadJob page = new FileDownloadJob("Page " + i, new URL(image),
                    DownloaderUtils.fileName(directory, title + "_" + story, i, "jpg"));
            downloader().addJob(page);
            i++;
        }
    }
}
