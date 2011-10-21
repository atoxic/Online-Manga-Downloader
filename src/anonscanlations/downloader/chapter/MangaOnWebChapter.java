package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import org.w3c.dom.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebChapter extends Chapter
{
    private String ctsn;
    private URL url;

    private transient String cookies, cdn, crcod;
    private transient ArrayList<String> paths;

    public MangaOnWebChapter(URL _url)
    {
        url = _url;
    }

    public void init() throws Exception
    {
        String urlString = url.toString();
        int index = urlString.indexOf("ctsn=");
        if(index == -1)
            throw new Exception("No ctsn");
        int endIndex = urlString.indexOf('&', index);
        if(endIndex == -1)
            endIndex = urlString.length();
        ctsn = urlString.substring(urlString.indexOf('=', index) + 1, endIndex);
        DownloaderUtils.debug("ctsn: " + ctsn);
        PageDownloadJob mainPage = new PageDownloadJob("Get the main page", new URL("http://mangaonweb.com/viewer.do?ctsn=" + ctsn), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                // save the cookie
                String headerName = null;
                for(int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
                {
                    if(headerName.equals("Set-Cookie"))
                    {
                        MangaOnWebChapter.this.cookies = conn.getHeaderField(i);
                    }
                }

                cdn = param(page, "cdn");
                crcod = param(page, "crcod");
            }
        };
        downloader().addJob(mainPage);

        PageDownloadJob xml = new PageDownloadJob("Get XML file", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.url = new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=book.xml&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000));
                this.cookies = MangaOnWebChapter.this.cookies;

                super.run();

                paths = new ArrayList<String>();

                Document d = DownloaderUtils.makeDocument(page);
                Element doc = d.getDocumentElement();

                NodeList pages = doc.getElementsByTagName("page");
                for(int i = 0; i < pages.getLength(); i++)
                {
                    Element e = (Element)pages.item(i);
                    paths.add(e.getAttribute("path"));
                }
            }
        };
        downloader().addJob(xml);
    }

    public void download(File directory) throws Exception
    {
        final File finalDirectory = directory;
        byte[] key = {99, 49, 51, 53, 100, 54, 56, 56, 57, 57, 99, 56, 50, 54, 99, 101, 100, 55, 99, 52, 57, 98, 99, 55, 54, 97, 97, 57, 52, 56, 57, 48};
        final BlowfishKey bfkey = new BlowfishKey(key);
        for(int i = 0; i < paths.size(); i++)
        {
            final int finalIndex = i;
            // rid is just a random number from 0-9999
            ByteArrayDownloadJob page = new ByteArrayDownloadJob("Page " + i, new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=" + paths.get(i) + "&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000)), cookies)
            {
                @Override
                public void run() throws Exception
                {


                    super.run();
                    bfkey.decrypt(buf, 0);

                    RandomAccessFile output = new RandomAccessFile(DownloaderUtils.fileName(finalDirectory, ctsn, finalIndex, "jpg"), "rw");
                    output.write(buf);
                    output.close();
                }
            };
            downloader().addJob(page);
        }
    }

    // get something like "param=return&"
    private static String param(String page, String param)
    {
        int index = 0, endIndex;
        index = page.indexOf(param + "=");
        endIndex = page.indexOf('&', index);
        if(page.indexOf('"', index) < endIndex)
            endIndex = page.indexOf('"', index);
        return(page.substring(index + param.length() + 1, endIndex));
    }
}
