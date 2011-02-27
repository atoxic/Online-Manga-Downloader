/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.famitsu;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.pcviewer.*;

/**
 *
 * @author /a/non
 */
public class FamitsuChapter extends PCViewerChapter
{
    private static final String[] PARAMS = {"key1", "key2", "key3", "key4", "shd"};

    public FamitsuChapter()
    {
        
    }
    public FamitsuChapter(String title)
    {
        super(title, PARAMS);
    }

    @Override
    public String getTitle()
    {
        String key4 = params.get("key4");
        return(key4.substring(0, key4.indexOf('-')));
    }
}
