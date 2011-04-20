/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

import java.io.*;

/**
 *
 * @author Administrator
 */
public abstract class DownloadListener
{
    private boolean aborted;
    protected DownloadDirectory directory;

    public DownloadListener(DownloadDirectory myDir)
    {
        aborted = false;
        directory = myDir;
    }

    public abstract void downloadIncrement(Chapter c);
    public abstract void downloadFinished(Chapter c);
    public abstract void setTotal(int total);
    
    public synchronized void abortDownload()
    {
        aborted = true;
    }
    public synchronized boolean isDownloadAborted()
    {
        return(aborted);
    }

    final String BAD_CHARS ="[\\\\/:*?\"<>\\|]";
    
    public synchronized String downloadPath(Chapter c, int i) throws IOException
    {
        String saveTitle = c.getTitle() + "_" + String.format("%03d", i) + ".jpg";
        saveTitle = saveTitle.replace(' ', '_');

        saveTitle = saveTitle.replaceAll(BAD_CHARS, "");

        return((new File(directory.getFile(), saveTitle)).getAbsolutePath());
    }

    public synchronized boolean downloadPage(String url, String encoding, ItemDownloadListener listener) throws IOException
    {
        if(listener == null)
            return(false);
        final String page = DownloaderUtils.getPage(url, encoding);
        if(page == null)
            return(false);
        
        listener.itemDownloaded(page);
        return(true);
    }

    public synchronized boolean downloadFile(String url, String filename, ItemDownloadListener listener) throws IOException
    {
        if(listener == null)
            return(false);
        if(DownloaderUtils.downloadFile(url, filename))
            return(false);

        listener.itemDownloaded(filename);
        return(true);
    }
}
