package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;
import anonscanlations.downloader.chapter.crypto.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebChapter extends Chapter
{
    private String ctsn;
    private URL url;

    private transient Map<String, String> cookies;
    private transient String cdn, crcod;
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
        JSoupDownloadJob mainPage = new JSoupDownloadJob("Get the main page", new URL("http://mangaonweb.com/viewer.do?ctsn=" + ctsn))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                // TODO convert to parse
                String page = response.body();

                // save the cookie
                MangaOnWebChapter.this.cookies = response.cookies();

                cdn = param(page, "cdn");
                crcod = param(page, "crcod");
            }
        };
        list.add(mainPage);

        JSoupDownloadJob xml = new JSoupDownloadJob("Get XML file", null)
        {
            @Override
            public void run() throws Exception
            {
                this.url = new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=book.xml&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000));
                setCookies(MangaOnWebChapter.this.cookies);

                super.run();

                paths = new ArrayList<String>();
                Document d = response.parse();
                for(Element e : d.select("page"))
                    paths.add(e.attr("path"));
            }
        };
        list.add(xml);
        return(list);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        byte[] keyBytes = {99, 49, 51, 53, 100, 54, 56, 56, 57, 57, 99, 56, 50, 54, 99, 101,
                        100, 55, 99, 52, 57, 98, 99, 55, 54, 97, 97, 57, 52, 56, 57, 48};
        final BlowfishKey bfkey = new BlowfishKey(keyBytes);
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

                    byte[] bytes = getBytes();
                    bfkey.decrypt(bytes, 0);
                    DownloaderUtils.safeWrite(bytes, f);
                }
            };
            page.setCookies(cookies);
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
