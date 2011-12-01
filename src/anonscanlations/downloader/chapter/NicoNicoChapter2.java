package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * Special free stuff on Nico like http://seiga.nicovideo.jp/nanoace/watch/5001
 * as opposed to original, user-drawn stuff
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoChapter2 extends Chapter implements Serializable
{
    protected URL url;
    protected String title, themeID;
    protected TreeSet<String> images;

    protected NicoNicoChapter2(){}
    public NicoNicoChapter2(URL _url)
    {
        url = _url;
        title = themeID = null;
        images = new TreeSet<String>();
    }

    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        JSoupDownloadJob index = new JSoupDownloadJob("Get the given url", url)
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                String page = response.body();
                int index = page.indexOf("theme_id");
                if(index == -1)
                    throw new Exception("theme_id not found");
                index = page.indexOf('\'', index);
                if(index == -1 || page.indexOf('\'', index + 1) == -1)
                    throw new Exception("theme_id not found");
                themeID = page.substring(index + 1, page.indexOf('\'', index + 1));
            }
        };

        JSoupDownloadJob theme = new JSoupDownloadJob("Get theme XML", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://seiga.nicovideo.jp/api/theme/info?id=" + themeID + "&t=" + Math.random());

                super.run();

                Document d = response.parse();
                title = JSoupUtils.elementText(d, "title");
                /*
                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(response.body()));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    private boolean inTitleTag = false;

                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        if(inTitleTag)
                            title = new String(ch, start, length);
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        if(localName.equals("title"))
                            inTitleTag = true;
                    }
                    @Override
                    public void endElement(String uri, String localName, String qName) throws SAXException
                    {
                        if(localName.equals("title"))
                            throw DownloaderUtils.DONE;
                    }
                });

                parser.setEntityResolver(new DefaultEntityResolver());
                try
                {
                    parser.parse(is);
                }
                catch(SAXException e)
                {
                    if(!e.equals(DownloaderUtils.DONE))
                        throw e;
                }
                // */
            }
        };

        JSoupDownloadJob data = new JSoupDownloadJob("Get data XML", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://seiga.nicovideo.jp/api/comic/data?r=" + Math.random() + "&theme%5Fid=" + themeID);

                super.run();

                Document d = response.parse();
                for(Element e : d.select("key"))
                    images.add(e.ownText());
            }
        };

        list.add(index);
        list.add(theme);
        list.add(data);
        return(list);
    }

    public static void decrypt(byte[] bytes) throws Exception
    {
        java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
        long _loc_4 = 0;
        md5.reset();
        md5.update(("" + bytes.length).getBytes());
        String _loc_2 = "";
        for(byte b : md5.digest())
        {
            _loc_2 += String.format("%02x", b);
        }
        ArrayList<Integer> _loc_3 = new ArrayList<Integer>();
        while(_loc_4 < 8)
        {
            _loc_3.add(Integer.parseInt(_loc_2.substring((int)(_loc_4 * 2), (int)(_loc_4 * 2) + 2), 16));
            _loc_4 = _loc_4 + 1;
        }
        int _loc_5 = _loc_3.size();
        _loc_4 = 0;
        while(_loc_4 < bytes.length)
        {
            bytes[(int)_loc_4] = (byte)(bytes[(int)_loc_4] ^ _loc_3.get((int)(_loc_4 % _loc_5)));
            _loc_4 = _loc_4 + 1;
        }
    }

    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        int i = 1;
        for(String image : images)
        {
            final File f = DownloaderUtils.fileName(directory, title, i, "jpg");
            if(f.exists())
            {
                i++;
                continue;
            }
            JSoupDownloadJob page = new JSoupDownloadJob("Page " + i, new URL("http://eco.nicoseiga.jp/comic/" + image))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();

                    byte[] bytes = response.bodyAsBytes();
                    decrypt(bytes);
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bytes);
                    fos.close();
                }
            };
            list.add(page);

            i++;
        }
        
        return(list);
    }
}
