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
public abstract class Magazine implements YAMLable, Serializable
{
    public abstract String getOriginalTitle();
    public abstract Collection<Series> getSeries();
    public abstract void addSeries(Series s);

    public final HashMap getMagazineInfo()
    {
        return((HashMap)DownloadInfoServer.MAGAZINE_INFO.get(getOriginalTitle()));
    }

    public final String getTranslatedTitle()
    {
        HashMap info = getMagazineInfo();
        return(info == null ?  getOriginalTitle() : (String)info.get("translation"));
    }

    @Override
    public final String toString()
    {
        return(getTranslatedTitle());
    }
}
