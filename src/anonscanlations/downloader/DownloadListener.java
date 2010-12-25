/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

/**
 *
 * @author Administrator
 */
public abstract class DownloadListener
{
    private boolean aborted;
    protected String dir;
    public DownloadListener()
    {
        dir = PreferencesManager.PREFS.get("downloadDir", "./");
    }
    public DownloadListener(String myDir)
    {
        aborted = false;
        dir = myDir;
    }

    public abstract void downloadProgressed(Chapter c, int page);
    public abstract void downloadFinished(Chapter c);
    public abstract void setDownloadLength(int length);
    
    public synchronized void abortDownload()
    {
        aborted = true;
    }
    public synchronized boolean isDownloadAborted()
    {
        return(aborted);
    }
    
    public synchronized String getDownloadDirectory()
    {
        return(dir);
    }
    public synchronized boolean downloadFile()
    {
        if(aborted)
            return(false);
        return(true);
    }
}
