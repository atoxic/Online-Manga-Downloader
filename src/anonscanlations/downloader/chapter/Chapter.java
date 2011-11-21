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
    /**
     * Makes jobs that initialize the chapter.
     * @return              A batch of jobs to initialize the chapter
     * @throws Exception    If there was a problem in making the jobs
     */
    public abstract ArrayList<DownloadJob> init() throws Exception;
    /**
     * Make jobs that download the chapter.  Assumes that the chapter is initialized
     * @param directory     The chapter to the files into
     * @return              A batch of jobs to download the chapter
     * @throws Exception    If there was a problem in making the jobs
     */
    public abstract ArrayList<DownloadJob> download(File directory) throws Exception;
}
