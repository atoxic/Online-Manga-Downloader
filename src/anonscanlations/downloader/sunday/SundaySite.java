/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.sunday;

import java.io.*;
import java.net.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class SundaySite extends Site
{
    public final static SundaySite SITE = new SundaySite();

    private SundaySite()
    {
    }

    public String getName() { return("Club Sunday"); }
    public TreeMap<String, Magazine> getMagazines()
            throws IOException
    {
        TreeMap<String, Magazine> ret = new TreeMap<String, Magazine>();

        TreeMap<String, Series> series = new TreeMap<String, Series>();

        SundayMagazine mag = new SundayMagazine();
        ret.put(mag.getOriginalTitle(), mag);
        
        for(int i = 1; ; i++)
        {
            String siteURL = "http://club.shogakukan.co.jp/magazine/SH_CSNDY/list/ALL/" + i;
            String page = DownloaderUtils.getPage(siteURL,
                                                    "UTF-8");
            int index = page.indexOf("<span class=\"current\">");
            String current = page.substring(index + 22, page.indexOf("</span>", index));

            DownloaderUtils.debug("current page: " + current);
            if(!current.equals(i + ""))
                break;

            index = 0;
            
            while(true)
            {
                index = page.indexOf("</div><a href=\"http://club.shogakukan.co.jp/magazine/SH_CSNDY/", index);
                if(index == -1)
                {
                    break;
                }
                String path = page.substring(index + 15, page.indexOf("detail", index));

                index = page.indexOf("alt=\"", index);
                String name = DownloaderUtils.unescapeHTML(page.substring(index + 5, page.indexOf('"', index + 5)));

                index = page.indexOf("openViewer('", index);
                String chapterURL = page.substring(index + 12, page.indexOf("')", index));

                SundaySeries s;
                if(series.containsKey(name))
                    s = (SundaySeries)series.get(name);
                else
                {
                    s = new SundaySeries(name, path);
                    series.put(name, s);
                    mag.addSeries(s);
                }

                SundayChapter chapter = new SundayChapter(chapterURL, siteURL);
                s.addChapter(chapter);
            }
        }
        
        return(ret);
    }
}
