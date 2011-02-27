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

    public SundaySeries(){}
    public SundaySeries(String myTitle, String myPath)
    {
        title = myTitle;
        path = myPath;
    }
    public String getPath()
    {
        return(path);
    }

    public String getOriginalTitle()
    {
        return(title);
    }
}
