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
public abstract class Chapter implements YAMLable, Serializable
{
    public abstract Series getSeries();
    public abstract String getTitle();
    public abstract int getNumPages();

    @Override
    public String toString()
    {
        return(getTitle());
    }

    public abstract boolean download(DownloadListener dl) throws Exception;
}
