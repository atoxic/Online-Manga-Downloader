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
public abstract class Series implements YAMLable, Serializable
{
    protected Magazine magazine;

    public Series(Magazine myMagazine)
    {
        magazine = myMagazine;
    }
    public Magazine getMagazine(){ return(magazine); }
    
    public abstract String getOriginalTitle();
    public abstract Collection<Chapter> getChapters();

    public abstract void addChapter(Chapter c);

    public final HashMap getSeriesInfo()
    {
        return((HashMap)DownloadInfoServer.SERIES_INFO.get(getOriginalTitle()));
    }

    public final String getTranslatedTitle()
    {
        HashMap info = getSeriesInfo();
        return(info == null ?  getOriginalTitle() : (String)info.get("translation"));
    }

    @Override
    public final String toString()
    {
        return(getTranslatedTitle());
    }
}
