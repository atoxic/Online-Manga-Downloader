/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.mangaonweb;

import java.util.*;
import java.io.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebSite extends Site
{
    public static final MangaOnWebSite SITE = new MangaOnWebSite();
    private MangaOnWebSite()
    {
    }

    public String getName()
    {
        return("Manga On Web");
    }

    public Collection<Magazine> getMagazines()
            throws IOException
    {
        ArrayList<Magazine> mags = new ArrayList<Magazine>();
        SimpleMagazine mag = new SimpleMagazine("漫画 on Web");
        mags.add(mag);
        TreeMap<String, Series> series = new TreeMap<String, Series>();

        final String SEARCH = "alt=\"";

        for(int i = 0; ; i++)
        {
            String page = DownloaderUtils.getPage("http://mangaonweb.com/comicContentsPage.do?sortType=0&comicSelect=false&freeContents=true&genre_no=0&searchText=&offset=" + (i * 25), "UTF-8");

            DownloaderUtils.debug("page: " + i);

            int index = 0;
            while((index = page.indexOf("<TD class=\"cont_heading_td\">", index)) != -1)
            {
                index = page.indexOf("no=", index) + 3;
                String ctsn = page.substring(index, page.indexOf('&', index));

                index = page.indexOf(SEARCH, index) + SEARCH.length();
                String chapterTitle = DownloaderUtils.unescapeHTML(page.substring(index, page.indexOf('"', index)));

                index = page.indexOf(SEARCH, index) + SEARCH.length();
                String seriesTitle = DownloaderUtils.unescapeHTML(page.substring(index, page.indexOf('"', index)));

                MangaOnWebChapter chapter = new MangaOnWebChapter(ctsn, chapterTitle);
                SimpleSeries s;
                if(series.containsKey(seriesTitle))
                    s = (SimpleSeries)series.get(seriesTitle);
                else
                {
                    s = new SimpleSeries(seriesTitle);
                    series.put(seriesTitle, s);
                    mag.addSeries(s);
                }
                s.addChapter(chapter);
            }

            // no next
            if(page.indexOf("cimages/design/00_common/moji/next_line.png") == -1)
                break;
        }

        return(mags);
    }
}
