package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.net.*;

import org.jsoup.nodes.*;
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
        JSoupDownloadJob check = new JSoupDownloadJob("Check if it's a Young Jump viewer", url)
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                // in case of redirects
                WebYoungJumpChapter.this.url = response.url();
                Document d = response.parse();
                String str = JSoupUtils.elementAttr(d, "param[name=FlashVars]", "value");
                HashMap<String, String> params = 
                        DownloaderUtils.getQueryMapFromQueryString(str);
                numPages = Integer.parseInt(params.get("ln"));
                titleURL = params.get("worksuri");
            }
        };
        JSoupDownloadJob getTitle = new JSoupDownloadJob("Get title URL", null)
        {
            @Override
            public void run() throws Exception
            {
                if(titleURL != null)
                {
                    url = new URL(titleURL);
                    super.run();

                    Document d = response.parse();
                    title = JSoupUtils.elementAttr(d, "img[src=images/title.png]", "alt");
                    if(title == null)
                        title = JSoupUtils.elementText(d, "title");
                }
                if(title == null)
                    title = WebYoungJumpChapter.this.url.toString();
                
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
        
        for(int i = 1; i <= numPages; i++)
        {
            final File f = DownloaderUtils.fileName(directory, title, i, "jpeg");
            if(f.exists())
                continue;
            list.add(new ByteArrayDownloadJob(DownloaderUtils.pageOutOf(i, 1, numPages), 
                                            new URL(url, "page/" + i + ".swf"))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();
                    ByteArrayInputStream bais = null;
                    Movie m = null;
                    try
                    {
                        bais = new ByteArrayInputStream(getBytes());
                        m = new Movie();
                        m.decodeFromStream(bais);
                    }
                    finally
                    {
                        if(bais != null)
                            bais.close();
                    }
                    for(MovieTag tag : m.getObjects())
                        if(tag.getClass() == DefineJPEGImage.class)
                            DownloaderUtils.safeWrite(((DefineJPEGImage)tag).getImage(), f);
                }
            });
        }
        
        return(list);
    }
}
