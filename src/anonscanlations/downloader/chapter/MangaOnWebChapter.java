package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

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

    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        String urlString = url.toString();
        int index = urlString.indexOf("ctsn=");
        if(index == -1)
            throw new Exception("No ctsn");
        int endIndex = urlString.indexOf('&', index);
        if(endIndex == -1)
            endIndex = urlString.length();
        ctsn = urlString.substring(urlString.indexOf('=', index) + 1, endIndex);
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
        list.add(mainPage);

        PageDownloadJob xml = new PageDownloadJob("Get XML file", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.url = new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=book.xml&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000));
                addRequestProperty("Cookie", MangaOnWebChapter.this.cookies);

                super.run();

                paths = new ArrayList<String>();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        if(localName.equals("page"))
                        {
                            paths.add(atts.getValue("path"));
                        }
                    }
                });
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };
        list.add(xml);
        return(list);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        byte[] key = {99, 49, 51, 53, 100, 54, 56, 56, 57, 57, 99, 56, 50, 54, 99, 101, 100, 55, 99, 52, 57, 98, 99, 55, 54, 97, 97, 57, 52, 56, 57, 48};
        final BlowfishKey bfkey = new BlowfishKey(key);
        for(int i = 0; i < paths.size(); i++)
        {
            final File f = DownloaderUtils.fileName(directory, ctsn, i, "jpg");
            if(f.exists())
                continue;
            // rid is just a random number from 0-9999
            ByteArrayDownloadJob page = new ByteArrayDownloadJob("Page " + i, 
                                            new URL("http://mangaonweb.com/page.do?cdn=" + cdn
                                                    + "&cpn=" + paths.get(i) + "&crcod=" + crcod
                                                    + "&rid=" + (int)(Math.random() * 10000)))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();
                    bfkey.decrypt(bytes, 0);

                    FileOutputStream output = new FileOutputStream(f);
                    output.write(bytes);
                    output.close();
                }
            };
            page.addRequestProperty("Cookie", cookies);
            list.add(page);
        }
        return(list);
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
