/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import anonscanlations.downloader.chapter.SundayChapter;
import java.io.*;
import java.util.*;
import java.net.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author Administrator
 */
public class Downloader extends Thread
{
    private static TempDownloaderFrame frame;
    private static Downloader currentThread;
    private static List<DownloadJob> jobs;
    
    private boolean die, suspended;
    private Exception error;
    private final Object finished, waitForJobs;
    private Downloader()
    {
        jobs = Collections.synchronizedList(new ArrayList<DownloadJob>());
        die = false;
        suspended = false;
        error = null;
        finished = new Object();
        waitForJobs = new Object();
    }
    public void addJob(DownloadJob job)
    {
        jobs.add(job);
        synchronized(waitForJobs)
        {
            waitForJobs.notify();
        }
    }
    private void kill()
    {
        die = true;
        synchronized(waitForJobs)
        {
            waitForJobs.notify();
        }
    }
    public void pause()
    {
        suspended = !suspended;
        synchronized(waitForJobs)
        {
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
                if(jobs.isEmpty())
                    synchronized(finished)
                    {
                        finished.notifyAll();
                    }
                synchronized(waitForJobs)
                {
                    while(!die && (jobs.isEmpty() || suspended))
                    {
                        waitForJobs.wait();
                    }
                }
                
                if(die)
                    break;
                
                DownloadJob job = jobs.remove(0);
                DownloaderUtils.debug("Running job: " + job);

                if(frame != null)
                    frame.setStatus(job.toString());
                
                job.run();
            }
            catch(InterruptedException ie)
            {
            }
            catch(Exception e)
            {
                DownloaderUtils.errorGUI("Error in executing download jobs", e, false);
                jobs.clear();
                error = e;
            }
        }
    }

    public static Downloader getDownloader()
    {
        return(currentThread);
    }

    public static void runChapter(Chapter _chapter, File _directory)
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
                    chapter.init();
                    currentThread.pause();
                    currentThread.waitUntilFinished();

                    if(frame != null)
                        frame.setStatus("Downloading");

                    currentThread.pause();
                    chapter.download(directory);
                    currentThread.pause();
                    currentThread.waitUntilFinished();

                    if(frame != null)
                        frame.setStatus("Finished");
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("Error in spawning download jobs", e, false);

                    if(frame != null)
                        frame.setStatus("Error");
                }

                System.exit(0);
            }
        };
        t.start();
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
        }
        frame = new TempDownloaderFrame();
        frame.setVisible(true);
    }
}