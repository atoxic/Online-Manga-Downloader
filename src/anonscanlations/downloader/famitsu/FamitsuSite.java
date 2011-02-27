/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.famitsu;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.pcviewer.*;

/**
 *
 * @author /a/non
 */
public class FamitsuSite extends Site
{
    public static final FamitsuSite SITE = new FamitsuSite();
    private FamitsuSite()
    {
    }

    public String getName(){ return("Famitsu Comic Clear"); }

    private static void pcviewerPage(FamitsuSeries series, String link)
            throws IOException
    {
        String page = DownloaderUtils.getPage(link, "Shift_JIS");
        int index = 0;
        while((index = page.indexOf("window.open('", index + 1)) != -1)
        {
            String chapterLink = page.substring(index + 13, page.indexOf("',", index));
            chapterLink = DownloaderUtils.unescapeHTML(chapterLink);
            DownloaderUtils.debug("\t\tchapter: " + chapterLink);

            FamitsuChapter chapter = new FamitsuChapter(chapterLink);
            chapter.parseXML();
            series.addChapter(chapter);
        }
    }
    
    public ArrayList<Magazine> getMagazines()
            throws IOException
    {
        ArrayList<Magazine> mags = new ArrayList<Magazine>();
        FamitsuMagazine mag = new FamitsuMagazine();

        String page = DownloaderUtils.getPage("http://www.famitsu.com/comic_clear/cl_list/", "Shift_JIS");
        int index = 0;
        while((index = page.indexOf("cc-list-title2", index)) != -1)
        {
            index = page.indexOf("http", index);
            String link = page.substring(index, page.indexOf('"', index));

            index = page.indexOf('>', index) + 1;
            String name = page.substring(index, page.indexOf('<', index));

            FamitsuSeries series = new FamitsuSeries(name);

            // Famitsu has three different viewers, depending on the URL
            // todo: make yo_ and co_ work
            if(link.contains("se_"))
            {
                DownloaderUtils.debug("name: " + name);
                DownloaderUtils.debug("\tlink: " + link);

                pcviewerPage(series, link);
            }
            else if(link.contains("yo_"))
            {
                DownloaderUtils.debug("Famitsu \"yo_\" comics not yet supported");
            }
            else if(link.contains("co_"))
            {
                DownloaderUtils.debug("Famitsu \"co_\" comics not yet supported");
            }

            mag.addSeries(series);
        }

        mags.add(mag);
        return(mags);
    }
}
