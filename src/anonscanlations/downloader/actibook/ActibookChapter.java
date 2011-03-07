/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.actibook;

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
    private String title, url, zoom;
    private int start, total;

    public ActibookChapter()
    {
    }

    public ActibookChapter(String myTitle, String myURL)
    {
        title = myTitle;
        url = myURL;
        start = 0;
        total = 0;
        zoom = "1";
    }

    private static int getIntContents(Element doc, String tagName)
    {
        NodeList list = doc.getElementsByTagName(tagName);
        if(list.getLength() < 1)
            return(-1);
        Node element = list.item(0);
        Node contents = element.getFirstChild();

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

    public boolean parseXML() throws IOException, ParserConfigurationException, SAXException
    {
        URL xmlURL = new URL(new URL(url), "db/book.xml");
        String page = DownloaderUtils.getPage(xmlURL.toString(), "UTF-8");
        if(page == null)
            return(false);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(page));
        Document d = builder.parse(is);
        Element doc = d.getDocumentElement();

        // get start and total
        start = getIntContents(doc, "start");
        total = getIntContents(doc, "total");
        if(start == -1 || total == -1)
            return(false);

        // page sizes
        xmlURL = new URL(new URL(url), "db/viewer.xml");
        page = DownloaderUtils.getPage(xmlURL.toString(), "UTF-8");
        is = new InputSource(new StringReader(page));
        d = builder.parse(is);
        doc = d.getDocumentElement();

        NodeList list = doc.getElementsByTagName("zoom_s");
        if(list.getLength() < 1)
            return(false);
        Node zoomsElement = list.item(0);
        Node zoomsContents = zoomsElement.getFirstChild();
        String[] zooms = zoomsContents.getNodeValue().split(",");
        if(zooms.length == 0)
            return(false);
        zoom = zooms[zooms.length - 1];

        return(true);
    }

    public String getTitle()
    {
        return(title);
    }

    public int getMin()
    {
        return(start);
    }
    public int getMax()
    {
        return(total - start);
    }

    public boolean download(DownloadListener dl) throws IOException
    {
        URL baseURL = new URL(new URL(url), "images/" + zoom + "/");

        for(int i = start;
            DownloaderUtils.downloadFile(new URL(baseURL, i + ".jpg"),
                dl.downloadPath(this, i));
            i++)
        {
            if(dl.isDownloadAborted())
                return(true);
            dl.downloadProgressed(this, i);
        }

        dl.downloadFinished(this);

        return(true);
    }
}
