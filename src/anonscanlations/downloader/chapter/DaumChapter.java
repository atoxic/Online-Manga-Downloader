package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;

import org.xml.sax.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class DaumChapter extends Chapter
{
    public static final Pattern IDMATCH = Pattern.compile("^([0-9]+)$");
    private URL url;
    private String ID, title;
    private Map<String, String> cookies;
    private ArrayList<String> images;
    
    public DaumChapter(URL _url)
    {
        url = _url;
        ID = null;
        title = null;
        cookies = null;
        images = new ArrayList<String>();
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        if(!url.toString().contains("cartoon.media.daum.net/webtoon/viewer"))
            throw new Exception("Invalid URL");
        String file = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
        Matcher matcher = IDMATCH.matcher(file);
        if(!matcher.matches())
            throw new Exception("ID not found");
        ID = matcher.group(1);
        DownloaderUtils.debug("ID: " + ID);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        JSoupDownloadJob getCookies = new JSoupDownloadJob("Get cookies", url)
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                DaumChapter.this.cookies = response.cookies();
            }
        };
        list.add(getCookies);
        JSoupDownloadJob xml = new JSoupDownloadJob("Get XML",
                                new URL("http://cartoon.media.daum.net/webtoon/viewer_images.xml?webtoon_episode_id=" + ID))
        {
            @Override
            public void run() throws Exception
            {
                this.setCookies(DaumChapter.this.cookies);
                super.run();
                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(response.body()));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new TagContentHandler()
                {
                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        String tag = tags.peek(), str = new String(ch, start, length);
                        if(tag.equals("url") && str.trim().length() > 0)
                            images.add(str);
                        else if(tag.equals("title"))
                            title = str;
                    }
                });
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };
        list.add(xml);
        
        return(list);
    }
    
    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        int i = 1;
        for(String image : images)
        {
            File f = DownloaderUtils.fileName(directory, title, i, "jpg");
            if(f.exists())
            {
                i++;
                continue;
            }
            
            FileDownloadJob file = new FileDownloadJob(DownloaderUtils.pageOutOf(i, 1, images.size()), new URL(image), f);
            list.add(file);
            i++;
        }
        return(list);
    }
}
