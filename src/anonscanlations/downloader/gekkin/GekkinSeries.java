/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.gekkin;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class GekkinSeries extends Series
{
    private String title;
    private TreeMap<String, Chapter> chapters;

    public GekkinSeries(Magazine myMagazine, Map<String, Object> yamlMap)
    {
        super(myMagazine);

        title = (String)yamlMap.get("title");

        chapters = new TreeMap<String, Chapter>();
    }

    public GekkinSeries(Magazine myMagazine, String myTitle)
    {
        super(myMagazine);

        title = myTitle;

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

        return(ret);
    }

    public void addChapter(Chapter chapter)
    {
        chapters.put(chapter.getTitle(), chapter);
    }
}
