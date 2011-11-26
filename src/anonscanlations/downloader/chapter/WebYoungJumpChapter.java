package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;

import java.net.*;
import com.flagstone.transform.*;
import com.flagstone.transform.image.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public class WebYoungJumpChapter extends Chapter
{
    private URL url;
    private String title, titleURL;
    private int numPages;
    public WebYoungJumpChapter(URL _url)
    {
        url = _url;
        title = titleURL = null;
        numPages = 0;
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        PageDownloadJob check = new PageDownloadJob("Check if it's a Young Jump viewer", url, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                
                int index = page.indexOf("ln=");
                if(index == -1)
                    throw new Exception("Parameters not found in page");
                String str = page.substring(index, page.indexOf('"', index));
                HashMap<String, String> params = 
                        DownloaderUtils.getQueryMapFromQueryString(str);
                numPages = Integer.parseInt(params.get("ln"));
                titleURL = params.get("worksuri");
            }
        };
        PageDownloadJob getTitle = new PageDownloadJob("Get title URL", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                title = WebYoungJumpChapter.this.url.toString();
                if(titleURL == null)
                    return;
                
                url = new URL(titleURL);
                super.run();
                
                int index = page.indexOf("images/title.png");
                if(index == -1)
                    return;
                index = page.indexOf("alt=\"", index);
                if(index == -1)
                    return;
                index += 5;
                
                title = page.substring(index, page.indexOf('"', index));
                
                DownloaderUtils.debug("title: " + title);
            }
        };
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(check);
        list.add(getTitle);
        return(list);
    }

    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        final File finalDirectory = directory;
        
        for(int i = 1; i <= numPages; i++)
        {
            final int finalIndex = i;
            list.add(new DownloadJob("Page " + i)
            {
                @Override
                public void run() throws Exception
                {
                    Movie m = new Movie();
                    m.decodeFromUrl(new URL(url, "page/" + finalIndex + ".swf"));
                    for(MovieTag tag : m.getObjects())
                    {
                        if(tag.getClass() == DefineJPEGImage.class)
                        {
                            byte[] bytes = ((DefineJPEGImage)tag).getImage();
                            FileOutputStream fos = new FileOutputStream(
                                                        DownloaderUtils.fileName(finalDirectory,
                                                                                title, finalIndex,
                                                                                "jpeg"));
                            fos.write(bytes);
                            fos.close();
                        }
                    }
                }
            });
        }
        
        return(list);
    }
}
