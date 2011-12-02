package anonscanlations.downloader.chapter;

import java.util.*;
import java.io.*;
import java.net.*;

import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.chapter.crypto.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * Special free stuff on Nico like http://seiga.nicovideo.jp/nanoace/watch/5001
 * as opposed to original, user-drawn stuff
 * Update (0.1.6+): Since Nico seems to have abandoned this viewer, it is now renamed "NicoNico Event"
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoEventChapter extends Chapter implements Serializable
{
    protected URL url;
    protected String title, themeID;
    protected TreeSet<String> images;

    protected NicoNicoEventChapter(){}
    public NicoNicoEventChapter(URL _url)
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
            ByteArrayDownloadJob page = new ByteArrayDownloadJob("Page " + i, new URL("http://eco.nicoseiga.jp/comic/" + image))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();

                    byte[] bytes = getBytes();
                    NicoNicoEventDecrypt.decrypt(bytes);
                    DownloaderUtils.safeWrite(bytes, f);
                }
            };
            list.add(page);

            i++;
        }
        
        return(list);
    }
}
