/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public abstract class Chapter implements Serializable
{
    public abstract ArrayList<DownloadJob> init() throws Exception;
    public abstract ArrayList<DownloadJob> download(File directory) throws Exception;
}
