/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.comichigh;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ComicHighSeries extends Series
{
    private String url, title;
    private TreeMap<String, Chapter> chapters;

    public ComicHighSeries(Magazine myMagazine, Map<String, Object> yamlMap)
    {
        super(myMagazine);

        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");

        chapters = new TreeMap<String, Chapter>();
    }

    public ComicHighSeries(Magazine myMagazine, String myURL)
    {
        super(myMagazine);

        title = null;
        url = myURL;

        chapters = new TreeMap<String, Chapter>();
    }

    public String getOriginalTitle()
    {
        return(title);
    }
    public Collection<Chapter> getChapters()
    {
        return(chapters.values());
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("url", url);

        return(ret);
    }

    public void addChapter(Chapter chapter)
    {
        chapters.put(chapter.getTitle(), chapter);
    }

    public void parsePage() throws IOException
    {
        String page = DownloaderUtils.getPage(url, "Shift_JIS");

        String cKV = title(page, "'cKV'");
        System.out.println("cKV");
    }

    private String title(String page, String ID)
    {
        int index = page.indexOf("ID=" + ID);
        if(index != -1)
        {
            index = page.indexOf("title='", index);
            if(index != -1)
                return(page.substring(index, page.indexOf('\'', index + 7)));
        }
        return(null);
    }
}
