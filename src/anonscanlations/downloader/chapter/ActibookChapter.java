/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ActibookChapter extends Chapter implements Serializable
{
    private URL url;
    private String zoom, title;
    private int start, total;

    public ActibookChapter()
    {
    }

    public ActibookChapter(URL myURL)
    {
        url = myURL;
        total = 0;
        start = 0;
        zoom = "1";
    }

    private static Node getNode(Element doc, String tagName)
    {
        NodeList list = doc.getElementsByTagName(tagName);
        if(list.getLength() < 1)
            return(null);
        Node element = list.item(0);
        Node contents = element.getFirstChild();
        return(contents);
    }

    private static int getIntContents(Element doc, String tagName)
    {
        Node contents = getNode(doc, tagName);
        if(contents == null)
            return(-1);

        int ret = -1;
        try
        {
            ret = Integer.parseInt(contents.getNodeValue());
        }
        catch(NumberFormatException nfe)
        {
            return(-1);
        }
        return(ret);
    }

    public void init() throws Exception
    {
        PageDownloadJob bookXML = new PageDownloadJob("book.xml for page range and title", new URL(url, "books/db/book.xml"), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(page));
                Document d = builder.parse(is);
                Element doc = d.getDocumentElement();

                // get start and total
                start = getIntContents(doc, "start");
                total = getIntContents(doc, "total");
                if(start == -1 || total == -1)
                    throw new Exception("No page range");

                Node nameContents = getNode(doc, "name");
                if(nameContents == null)
                    throw new Exception("No name");
                title = nameContents.getNodeValue();
            }
        };
        PageDownloadJob viewerXML = new PageDownloadJob("viewer.xml for zoom level", new URL(url, "books/db/viewer.xml"), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(page));
                Document d = builder.parse(is);
                Element doc = d.getDocumentElement();

                Node zoomsContents = getNode(doc, "zoom_s");
                if(zoomsContents == null)
                    throw new Exception("No zoom element");
                String[] zooms = zoomsContents.getNodeValue().split(",");
                if(zooms.length == 0)
                    throw new Exception("No zoom values");
                zoom = zooms[zooms.length - 1];
            }
        };
        Downloader.getDownloader().addJob(bookXML);
        Downloader.getDownloader().addJob(viewerXML);
    }

    public void download(File directory) throws Exception
    {
        for(int i = start; i < start + total; i++)
        {
            FileDownloadJob page = new FileDownloadJob("Page " + i, new URL(url, "books/images/" + zoom + "/" + i + ".jpg"),
                                                                    DownloaderUtils.fileName(directory, title, i, "jpg"));
            Downloader.getDownloader().addJob(page);
        }
    }
}
