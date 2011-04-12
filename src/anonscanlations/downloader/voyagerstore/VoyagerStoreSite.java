/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.voyagerstore;

import java.util.*;
import java.io.*;
import java.util.regex.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.crochettime.*;

/**
 *
 * @author /a/non
 */
public class VoyagerStoreSite extends Site
{
    public static final VoyagerStoreSite SITE = new VoyagerStoreSite();
    private static final String IMAGE_PREFIX = "images/products/";
    // "series title(chapter title)"
    private static final Pattern[] PATTERNS = new Pattern[]{ Pattern.compile("(.*)\\((.*)\\)"), Pattern.compile("(.*)(?:\\s|[\u3000])(\\S*)"), Pattern.compile("(.*)(vol.\\d+)")};

    private VoyagerStoreSite()
    {
        
    }

    public String getName()
    {
        return("Voyager Store");
    }
    public Collection<Magazine> getMagazines()
            throws IOException
    {
        ArrayList<Magazine> mags = new ArrayList<Magazine>();
        SimpleMagazine mag = new SimpleMagazine("Voyager Store");
        mags.add(mag);
        TreeMap<String, Series> series = new TreeMap<String, Series>();

        String page;
        int index, endIndex;
        for(int i = 1; ; i++)
        {
            DownloaderUtils.debug("page: " + i);

            index = 0;
            page = DownloaderUtils.getPage("http://voyager-store.com/index.php?main_page=addon&module=voyager_store_asp%2Fgenre_product_list&genre=1727&columnlist=list&sort=created_desc&page=" + i, "UTF-8");
            endIndex = page.indexOf("<!-- /.horizon_box -->");
            while((index = page.indexOf(IMAGE_PREFIX, index)) < endIndex && index != -1)
            {
                index += IMAGE_PREFIX.length();
                String key = page.substring(index, page.indexOf('.', index));

                index = page.indexOf("alt=\"", index) + 5;
                String title = page.substring(index, page.indexOf('"', index));

                // try to parse the product title into the series and chapter title
                String seriesTitle = "Miscellaneous", chapterTitle = title;
                for(Pattern p : PATTERNS)
                {
                    Matcher matcher = p.matcher(title);
                    if(matcher.find())
                    {
                        seriesTitle = matcher.group(1);
                        chapterTitle = matcher.group(2);
                    }
                }
                CrochetTimeChapter chapter = new CrochetTimeChapter(key, chapterTitle);
                try
                {
                    if(chapter.checkFileList())
                    {
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
                }
                catch(Exception e)
                {

                }
            }

            if(!page.contains("./images/icon_right_1.png"))
                break;
        }

        for(Series s : series.values())
            mag.addSeries(s);
        return(mags);
    }
}
