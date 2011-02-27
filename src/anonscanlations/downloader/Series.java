/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public abstract class Series extends YAMLable implements Serializable
{
    protected Magazine magazine;
    private ArrayList<Chapter> chapters;

    public Series()
    {
        chapters = new ArrayList<Chapter>();
    }

    public Series(Magazine myMagazine)
    {
        chapters = new ArrayList<Chapter>();
        magazine = myMagazine;
    }
    public final void setMagazine(Magazine myMag){ magazine = myMag; }
    public final Magazine getMagazine(){ return(magazine); }
    
    public abstract String getOriginalTitle();

    public final Collection<Chapter> getChapters()
    {
        return(chapters);
    }
    public final void addChapter(Chapter c)
    {
        c.setSeries(this);
        chapters.add(c);
    }

    public final HashMap getSeriesInfo()
    {
        return((HashMap)DownloadInfoServer.SERIES_INFO.get(getOriginalTitle()));
    }

    public final String getTranslatedTitle()
    {
        HashMap info = getSeriesInfo();
        if(info != null)
        {
            if(info.containsKey("translation"))
                return((String)info.get("translation"));
        }
        return(getOriginalTitle());
    }

    @Override
    public final String toString()
    {
        return(getTranslatedTitle());
    }
}
