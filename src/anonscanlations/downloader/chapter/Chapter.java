/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public abstract class Chapter implements Serializable
{
    protected URL url;
    protected String title;
    protected transient String username;
    protected transient char[] password;
    
    protected Chapter(URL _url)
    {
        this(_url, null, null);
    }
    protected Chapter(URL _url, String _username, char[] _password)
    {
        url = _url;
        title = null;
        username = _username;
        password = _password;
    }
    
    @Override
    public void finalize() throws Throwable
    {
        if(password != null)
        {
            Arrays.fill(password, ' ');
            password = null;
        }
    }
    
    public void getRequiredInfo(LoginManager s) throws Exception {}
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
    
    public String getTitle(){ return(title); }
}
