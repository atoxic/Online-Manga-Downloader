/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.sunday;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class SundaySeries extends Series
{
    private String title, path;
    private TreeMap<String, Chapter> chapters;

    public SundaySeries(Magazine myMagazine, Map<String, Object> yamlMap)
    {
        super(myMagazine);

        title = (String)yamlMap.get("title");
        path = (String)yamlMap.get("path");
        chapters = new TreeMap<String, Chapter>();
    }

    public SundaySeries(Magazine myMagazine, String myTitle, String myPath)
    {
        super(myMagazine);

        title = myTitle;
        path = myPath;
        chapters = new TreeMap<String, Chapter>();
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("path", path);

        return(ret);
    }

    public void addChapter(Chapter chapter)
    {
        chapters.put(chapter.getTitle(), chapter);
    }

    public String getPath()
    {
        return(path);
    }

    public String getOriginalTitle()
    {
        return(title);
    }
    public Collection<Chapter> getChapters()
    {
        return(chapters.values());
    }
}
