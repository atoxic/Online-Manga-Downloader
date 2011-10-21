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
public abstract class Chapter implements Serializable
{
    protected static Downloader downloader(){ return(Downloader.getDownloader()); }

    public abstract void init() throws Exception;
    public abstract void download(File directory) throws Exception;
}
