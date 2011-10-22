package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.w3c.dom.*;

import anonscanlations.downloader.*;

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

        POSTDownloadJob load = new POSTDownloadJob("Get data", new URL("http://www.poco2.jp/viewerset/common/load.php"), "UTF-8", "partid=" + partid)
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                Document d = DownloaderUtils.makeDocument(page);
                Element doc = d.getDocumentElement();

                // Get the File element
                Element fileElm = (Element)(doc.getElementsByTagName("File").item(0));
                if(!fileElm.getAttribute("errorType").equals("0"))
                    throw new Exception("Error in retreiving data");
                title = fileElm.getAttribute("title");
                story = fileElm.getAttribute("story");

                // Get pages
                NodeList pageElms = doc.getElementsByTagName("Page");
                for(int i = 0; i < pageElms.getLength(); i++)
                {
                    Element pageElm = (Element)pageElms.item(i);
                    images.add(pageElm.getAttribute("image"));
                }
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
