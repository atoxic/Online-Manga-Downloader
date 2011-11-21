package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.regex.*;
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
public class NicoNicoChapter extends Chapter
{
    public static final Pattern IDMATCH = Pattern.compile(".*?([0-9]+)$");

    private URL url;

    private transient String username, title, id;
    private transient char[] password;
    private transient HashMap<String, NicoImage> images;
    public NicoNicoChapter(URL _url, String _username, char[] _password)
    {
        url = _url;
        username = _username;
        password = _password;
        title = null;
    }

    private class NicoImage
    {
        private String id, se_path;
        private ArrayList<String> comments, user_hashes;
        public NicoImage()
        {
            id = null;
            se_path = null;
            comments = new ArrayList<String>();
            user_hashes = new ArrayList<String>();
        }
        public void setID(String _id){ id = _id; }
        public void setSEPath(String _se_path){ se_path = _se_path; }
        public void addComment(String comment, String hash)
        {
            comments.add(comment);
            user_hashes.add(hash);
        }
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        Matcher matcher = IDMATCH.matcher(url.toString());
        if(!matcher.matches())
            throw new Exception("ID not found");
        id = matcher.group(1);
        final NicoNicoLoginDownloadJob login = new NicoNicoLoginDownloadJob(username, password);
        PageDownloadJob info = new PageDownloadJob("Get info", new URL("http://seiga.nicovideo.jp/api/theme/info?id=" + id), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                addRequestProperty("Cookie", login.getCookies());
                super.run();

                int index = page.indexOf("<title>");
                if(index == -1)
                    throw new Exception("Title not found");
                title = page.substring(index + 7, page.indexOf("</", index));
            }
        };
        PageDownloadJob data = new PageDownloadJob("Get data", new URL("http://seiga.nicovideo.jp/api/theme/data?theme_id=" + id), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                addRequestProperty("Cookie", login.getCookies());
                super.run();
                parseData(page);
            }
        };
        list.add(login);
        list.add(info);
        list.add(data);
        return(list);
    }

    private void parseData(String page) throws Exception
    {
        images = new HashMap<String, NicoImage>();

        Piccolo parser = new Piccolo();
        InputSource is = new InputSource(new StringReader(page));
        is.setEncoding("UTF-8");

        parser.setContentHandler(new DefaultHandler()
        {
            private boolean inImageTag, inIDTag, inSETag;
            private NicoImage image;

            {
                inImageTag = false;
                inIDTag = false;
                inSETag = false;
                image = null;
            }

            @Override
            public void characters(char[] ch, int start, int length)
            {
                if(inIDTag)
                    image.setID(new String(ch, start, length));
                else if(inSETag)
                    image.setSEPath(new String(ch, start, length));
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts)
            {
                if(localName.equals("image"))
                {
                    inImageTag = true;
                    image = new NicoImage();
                }
                else if(inImageTag)
                {
                    if(localName.equals("id"))
                        inIDTag = true;
                    else if(localName.equals("se_path"))
                        inSETag = true;
                }
            }
            @Override
            public void endElement(String uri, String localName, String qName)
            {
                if(localName.equals("image"))
                {
                    inImageTag = false;
                    if(image.id != null)
                        images.put(image.id, image);
                }
                else if(inImageTag)
                {
                    if(localName.equals("id"))
                        inIDTag = false;
                    else if(localName.equals("se_path"))
                        inSETag = false;
                }
            }
        });
        parser.parse(is);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        int i = 1;
        for(NicoImage image : images.values())
        {
            FileDownloadJob page = new FileDownloadJob("Page " + i,
                            new URL("http://lohas.nicoseiga.jp/thumb/" + image.id + "l?"),
                            DownloaderUtils.fileName(directory, title, i, "jpg"));
            list.add(page);
            if(image.se_path != null)
            {
                FileDownloadJob sfx = new FileDownloadJob("Page " + i + " SFX",
                            new URL("http://lohas.nicoseiga.jp/" + image.se_path),
                            DownloaderUtils.fileName(directory, title, i, "mp3"));
                list.add(sfx);
            }
            
            i++;
        }
        return(list);
    }
}