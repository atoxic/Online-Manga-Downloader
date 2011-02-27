/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.comichigh;

import anonscanlations.downloader.pluginfree.PluginFreeChapter;
import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ComicHighSite extends Site
{
    public final static ComicHighSite SITE = new ComicHighSite();

    private ComicHighSite()
    {
    }

    public String getName(){ return("Comic High!"); }

    public TreeMap<String, Magazine> getMagazines()
            throws IOException
    {
        TreeMap<String, Magazine> map = new TreeMap<String, Magazine>();
        ComicHighMagazine mag = new ComicHighMagazine();
        
        String page = DownloaderUtils.getPage("http://www.comichigh.jp/webcomic.html", "UTF-8");
        String[] sections = page.split("class=\"lineup\"");

        TreeSet<String> links = new TreeSet<String>();

        for(int i = 1; i < sections.length; i++)
        {
            int index = 0;
            index = sections[i].indexOf("<dt>");
            String title = sections[i].substring(index + 4, sections[i].indexOf("<", index + 4) - 1);
            DownloaderUtils.debug("title: " + title);
            ComicHighSeries series = new ComicHighSeries(mag, title);

            while((index = sections[i].indexOf("http://futabasha.pluginfree.com/weblish/futabawebhigh/", index + 1)) != -1)
            {
                String link = sections[i].substring(index, sections[i].indexOf("/transit2.html", index));

                links.add(link);
            }
            
            for(String link : links)
            {
                DownloaderUtils.debug("\tlink: " + link);
                try
                {
                    PluginFreeChapter chapter = new PluginFreeChapter(link.substring(link.indexOf('_') + 1), link, 15);
                    chapter.setSeries(series);
                    chapter.parsePages("http://futabasha.pluginfree.com/cgi-bin/widget.cgi?a=");
                    series.addChapter(chapter);
                }
                catch(IOException e)
                {
                    DownloaderUtils.error("could not get info on page: " + link, e, false);
                }
            }
            mag.addSeries(series);
            links.clear();
        }

        map.put(mag.getOriginalTitle(), mag);


        return(map);
    }
}
