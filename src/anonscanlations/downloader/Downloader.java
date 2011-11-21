/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.zip.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import anonscanlations.downloader.chapter.*;
import anonscanlations.downloader.downloadjobs.*;
import anonscanlations.downloader.extern.*;

/**
 * This is the thread that processes all download jobs
 * @author /a/non
 */
public class Downloader extends Thread
{
    private static TempDownloaderFrame frame;
    private static Downloader currentThread;
    private static List<List<DownloadJob>> jobs;
    
    private boolean die, suspended;
    private Exception error;
    private final Object finished, waitForJobs;
    private Downloader()
    {
        jobs = Collections.synchronizedList(new ArrayList<List<DownloadJob>>());
        die = false;
        suspended = false;
        error = null;
        finished = new Object();
        waitForJobs = new Object();
    }
    public void addJobs(ArrayList<DownloadJob> job)
    {
        synchronized(waitForJobs)
        {
            jobs.add(job);
            waitForJobs.notify();
        }
    }
    private void kill()
    {
        synchronized(waitForJobs)
        {
            die = true;
            waitForJobs.notify();
        }
    }
    public void pause()
    {
        synchronized(waitForJobs)
        {
            suspended = true;
            waitForJobs.notify();
        }
    }
    public void unpause()
    {
        synchronized(waitForJobs)
        {
            suspended = false;
            waitForJobs.notify();
        }
    }
    public void waitUntilFinished() throws Exception
    {
        if(jobs.isEmpty())
            return;
        Exception errorCopy = null;
        synchronized(finished)
        {
            finished.wait();
            if(error != null)
            {
                errorCopy = error;
                error = null;
            }
        }
        if(errorCopy != null)
            throw errorCopy;
        return;
    }
    @Override
    public void run()
    {
        while(!die)
        {
            try
            {
                List<DownloadJob> top;
                
                /*
                 * So everything done to the thread after this
                 * (adding jobs, suspending) happens after I start waiting.
                 * This is important if those actions were triggered by
                 * the downloader finishing the queue.
                 */
                synchronized(waitForJobs)
                {
                    if(jobs.isEmpty())
                    {
                        synchronized(finished)
                        {
                            finished.notifyAll();
                        }
                    }
                    while(!die && (jobs.isEmpty() || suspended))
                    {
                        waitForJobs.wait();
                    }
                    if(die)
                        break;
                    top = jobs.remove(0);
                }
                
                for(DownloadJob job : top)
                {
                    DownloaderUtils.debug("Running job: " + job);
                    if(frame != null)
                        frame.setStatus(job.toString());
                    job.run();
                }
            }
            catch(InterruptedException ie)
            {
            }
            catch(Exception e)
            {
                error = e;
            }
        }
    }

    public static Downloader getDownloader()
    {
        return(currentThread);
    }

    public static Thread runChapter(Chapter _chapter, File _directory)
    {
        final Chapter chapter = _chapter;
        final File directory = _directory;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    if(frame != null)
                        frame.setStatus("Initializing");

                    currentThread.pause();
                    currentThread.addJobs(chapter.init());
                    currentThread.unpause();
                    currentThread.waitUntilFinished();

                    if(frame != null)
                        frame.setStatus("Downloading");

                    currentThread.pause();
                    currentThread.addJobs(chapter.download(directory));
                    currentThread.unpause();
                    currentThread.waitUntilFinished();

                    if(frame != null)
                        frame.setStatus("Finished");
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("Error in spawning or executing download jobs", e, false);

                    if(frame != null)
                        frame.setStatus("Error");
                }
                currentThread.unpause();
            }
        };
        t.start();
        return(t);
    }

    public static Thread autodetectChapter(ArrayList<Chapter> _chapters, File _directory)
    {
        final ArrayList<Chapter> chapters = _chapters;
        final File directory = _directory;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                boolean nicoLogin = false;

                for(Chapter chapter : chapters)
                {
                    try
                    {
                        // Trying Nico; need login (only once)
                        if((chapter.getClass() == NicoNicoChapter.class
                            || chapter.getClass() == NicoNicoAceChapter.class)
                            && !nicoLogin)
                        {
                            NicoNicoLoginDownloadJob.DIALOG.setVisible(true);
                            synchronized(NicoNicoLoginDownloadJob.DIALOG.lock)
                            {
                                NicoNicoLoginDownloadJob.DIALOG.lock.wait();
                            }
                            nicoLogin = true;
                        }
                        else if(chapter.getClass() == SundayChapter.class)
                        {
                            SundayChapter.DIALOG.setVisible(true);
                            synchronized(SundayChapter.DIALOG.lock)
                            {
                                SundayChapter.DIALOG.lock.wait();
                            }
                        }

                        DownloaderUtils.debug("Trying handler: " + chapter.getClass());
                        if(frame != null)
                            frame.setStatus("Trying handler: " + chapter.getClass());

                        currentThread.pause();
                        currentThread.addJobs(chapter.init());
                        currentThread.unpause();
                        currentThread.waitUntilFinished();

                        if(frame != null)
                            frame.setStatus("Downloading");

                        currentThread.pause();
                        currentThread.addJobs(chapter.download(directory));
                        currentThread.unpause();
                        currentThread.waitUntilFinished();
                        
                        if(frame != null)
                            frame.setStatus("Finished");
                        
                        currentThread.unpause();
                        return;
                    }
                    catch(Exception e)
                    {
                        DownloaderUtils.error("Handler failed: " + chapter.getClass(), e, false);
                    }
                    currentThread.unpause();
                }
                if(frame != null)
                    frame.setStatus("Error: could not autodetect");
            }
        };
        t.start();
        return(t);
    }

    public static void init() throws Exception
    {
        currentThread = new Downloader();
        currentThread.start();
    }

    public static void main(String[] args) throws Exception
    {
        // unnecessary, but just to appear like a real viewer
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
        // in order to handle custom protocols
        System.setProperty("java.protocol.handler.pkgs", "anonscanlations.downloader.chapter");

        // initialize backend
        init();

        // Try to use native look and feel
        try
        {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            // couldn't find native look and feel: it couldn't be helped
            DownloaderUtils.error("Could not set look and feel to native LnF", e, false);
        }
        frame = new TempDownloaderFrame();
        frame.setVisible(true);
    }
}