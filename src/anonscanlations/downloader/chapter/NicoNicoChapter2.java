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
 * Special free stuff on Nico like http://seiga.nicovideo.jp/nanoace/watch/5001
 * as opposed to original, user-drawn stuff
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoChapter2 extends Chapter implements Serializable
{
    protected URL url;
    protected String title, themeID;
    protected ArrayList<String> images;

    protected NicoNicoChapter2(){}
    public NicoNicoChapter2(URL _url)
    {
        url = _url;
        title = themeID = null;
        images = new ArrayList<String>();
    }

    @Override
    public void init() throws Exception
    {
        PageDownloadJob index = new PageDownloadJob("Get the given url", url, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                int index = page.indexOf("theme_id");
                if(index == -1)
                    throw new Exception("theme_id not found");
                index = page.indexOf('\'', index);
                if(index == -1 || page.indexOf('\'', index + 1) == -1)
                    throw new Exception("theme_id not found");
                themeID = page.substring(index + 1, page.indexOf('\'', index + 1));
            }
        };

        PageDownloadJob theme = new PageDownloadJob("Get theme XML", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://seiga.nicovideo.jp/api/theme/info?id=" + themeID + "&t=" + Math.random());

                super.run();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
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
                try
                {
                    parser.parse(is);
                }
                catch(SAXException e)
                {
                    if(!e.equals(DownloaderUtils.DONE))
                        throw e;
                }
            }
        };

        PageDownloadJob data = new PageDownloadJob("Get data XML", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://seiga.nicovideo.jp/api/comic/data?r=" + Math.random() + "&theme%5Fid=" + themeID);

                super.run();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    private Stack<String> tags = new Stack<String>();

                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        if(tags.peek().equals("key"))
                            images.add(images.size() / 2 * 2, new String(ch, start, length));
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        tags.push(localName);
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName)
                    {
                        tags.pop();
                    }
                });
                parser.parse(is);
            }
        };

        downloader().addJob(index);
        downloader().addJob(theme);
        downloader().addJob(data);
    }

    @Override
    public void download(File directory) throws Exception
    {
        int i = 1;
        for(String image : images)
        {
            final File f = DownloaderUtils.fileName(directory, title, i, "jpg");
            ByteArrayDownloadJob page = new ByteArrayDownloadJob("Page " + i, new URL("http://eco.nicoseiga.jp/comic/" + image))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();

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

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bytes);
                    fos.close();
                }
            };
            downloader().addJob(page);

            i++;
        }
    }
}
