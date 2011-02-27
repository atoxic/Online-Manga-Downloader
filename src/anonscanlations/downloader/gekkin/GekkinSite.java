/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.gekkin;

import anonscanlations.downloader.pluginfree.PluginFreeChapter;
import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class GekkinSite extends Site
{
    public final static GekkinSite SITE = new GekkinSite();

    private GekkinSite()
    {
    }

    public String getName(){ return("Comic Gekkin"); }

    public TreeMap<String, Magazine> getMagazines()
            throws IOException
    {
        DownloaderUtils.debug("gekkin");

        TreeMap<String, Magazine> map = new TreeMap<String, Magazine>();
        GekkinMagazine mag = new GekkinMagazine();
        
        String page = DownloaderUtils.getPage("http://www.comic-gekkin.com/works/archive.html", "UTF-8") +
                DownloaderUtils.getPage("http://www.comic-gekkin.com/works/serialization.html", "UTF-8");

        TreeMap<String, String> links = new TreeMap<String, String>();
        int index = 0;
        while((index = page.indexOf("<p class=\"thumb\">", index)) != -1)
        {
            index = page.indexOf("works/", index);
            String link = page.substring(index, page.indexOf('"', index));
            
            index = page.indexOf("alt=\"", index);
            String title = page.substring(index + 5, page.indexOf('"', index + 5));
            links.put(title, link);
        }

        TreeSet<String> chapters = new TreeSet<String>();
        for(Map.Entry<String, String> entry : links.entrySet())
        {
            GekkinSeries series = new GekkinSeries(entry.getKey());

            page = DownloaderUtils.getPage("http://www.comic-gekkin.com/" + entry.getValue(), "UTF-8");
            DownloaderUtils.debug("title: " + entry.getKey());

            index = 0;
            while((index = page.indexOf("http://ebook.comic-gekkin.com/weblish/gekkin/", index + 1)) != -1)
            {
                String chapterLink = page.substring(index, page.indexOf('"', index));
                if(chapters.contains(chapterLink) || !chapterLink.contains("transit"))
                    continue;
                chapters.add(chapterLink);

                DownloaderUtils.debug("\tlink: " + chapterLink);

                try
                {
                    String title = chapterLink.substring(chapterLink.lastIndexOf('/', chapterLink.lastIndexOf('/') - 1) + 1, chapterLink.lastIndexOf('/'));
                    if(title.indexOf('_') != -1)
                        title = title.substring(title.indexOf('_') + 1);
                    PluginFreeChapter chapter = new PluginFreeChapter(title,
                            chapterLink.substring(0, chapterLink.lastIndexOf('/')), 18);
                    chapter.parsePages("http://ebook.comic-gekkin.com/cgi-bin/widget.cgi?a=");
                    series.addChapter(chapter);
                }
                catch(IOException e)
                {
                    DownloaderUtils.error("could not get info on page: " + chapterLink, e, false);
                }
            }
            mag.addSeries(series);
        }

        map.put(mag.getOriginalTitle(), mag);
        
        return(map);
    }
}
