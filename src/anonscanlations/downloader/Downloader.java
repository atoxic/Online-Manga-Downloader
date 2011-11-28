/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import java.net.*;

import java.util.concurrent.locks.*;

import anonscanlations.downloader.chapter.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * This is the thread that processes all download jobs
 * @author /a/non
 */
public class Downloader extends Thread
{
    private static TempDownloaderFrame frame;
    private static Downloader currentThread;
    
    private List<List<DownloadJob>> jobs;
    private boolean die, suspended;
    private Exception error;
    private final Object waitForJobs;
    private final Lock processing;
    private Downloader()
    {
        jobs = Collections.synchronizedList(new ArrayList<List<DownloadJob>>());
        
        die = false;
        suspended = false;
        error = null;
        
        waitForJobs = new Object();
        processing = new ReentrantLock();
    }
    public void addJobs(ArrayList<DownloadJob> job)
    {
        DownloaderUtils.debug("Downloader Operation: Enter AJ");
        jobs.add(job);
        synchronized(waitForJobs)
        {
            waitForJobs.notify();
        }
        DownloaderUtils.debug("Downloader Operation: Exit AJ");
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
        DownloaderUtils.debug("Downloader Operation: Enter Pause");
        synchronized(waitForJobs)
        {
            suspended = true;
            waitForJobs.notify();
        }
        DownloaderUtils.debug("Downloader Operation: Exit Pause");
    }
    public void unpause()
    {
        DownloaderUtils.debug("Downloader Operation: Enter Unpause");
        synchronized(waitForJobs)
        {
            suspended = false;
            waitForJobs.notify();
        }
        DownloaderUtils.debug("Downloader Operation: Exit Unpause");
    }
    public void waitUntilFinished() throws Exception
    {
        while(true)
        {
            try
            {
                processing.lock();
                DownloaderUtils.debug("WUF Lock Acquired");
                if(error != null)
                {
                    DownloaderUtils.debug("WUF Throwing Error");
                    ExecutionException e = new ExecutionException(error);
                    error = null;
                    throw e;
                }
                if(jobs.isEmpty())
                {
                    DownloaderUtils.debug("WUF Thread Finished");
                    return;
                }
            }
            finally
            {
                DownloaderUtils.debug("WUF Lock Released");
                processing.unlock();
            }
        }
    }
    @Override
    public void run()
    {
        while(!die)
        {
            try
            {
                /*
                 * So everything done to the thread after this
                 * (adding jobs, suspending) happens after I start waiting.
                 * This is important if those actions were triggered by
                 * the downloader finishing the queue.
                 */
                synchronized(waitForJobs)
                {
                    while(!die && (jobs.isEmpty() || suspended))
                    {
                        DownloaderUtils.debug("Thread Wait");
                        waitForJobs.wait();
                        DownloaderUtils.debug("Thread Wake");
                    }
                    if(die)
                        break;
                }
            }
            catch(InterruptedException ie)
            {
                DownloaderUtils.error("Waiting loop interrupted", ie, false);
            }
            
            try
            {
                processing.lock();
                List<DownloadJob> top = jobs.remove(0);
                for(DownloadJob job : top)
                {
                    DownloaderUtils.debug("Running job: " + job);
                    if(frame != null)
                        frame.setStatus(job.toString());
                    job.run();
                }
            }
            catch(Exception e)
            {
                error = e;
            }
            finally
            {
                processing.unlock();
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
                    DownloaderUtils.debug("run Spot 1");
                    
                    if(frame != null)
                        frame.setStatus("Initializing");
                    
                    DownloaderUtils.debug("run Spot 2");
                    
                    currentThread.pause();
                    currentThread.addJobs(chapter.init());
                    currentThread.unpause();
                    currentThread.waitUntilFinished();
                    
                    DownloaderUtils.debug("run Spot 3");

                    if(frame != null)
                        frame.setStatus("Downloading");
                    
                    DownloaderUtils.debug("run Spot 4");

                    currentThread.pause();
                    currentThread.addJobs(chapter.download(directory));
                    currentThread.unpause();
                    currentThread.waitUntilFinished();
                    
                    DownloaderUtils.debug("run Spot 5");

                    if(frame != null)
                        frame.setStatus("Finished");
                    
                    DownloaderUtils.debug("run Spot 6");
                }
                catch(ExecutionException e)
                {
                    DownloaderUtils.errorGUI("Error in executing download jobs", e.getException(), false);

                    if(frame != null)
                        frame.setStatus("Error: " + e.getException().getMessage());
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("Error in spawning download jobs", e, false);

                    if(frame != null)
                        frame.setStatus("Error: " + e.getMessage());
                }
                
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
                        if((chapter instanceof NicoNicoChapter
                            || chapter instanceof NicoNicoAceChapter)
                            && !nicoLogin)
                        {
                            NicoNicoLoginDownloadJob.DIALOG.setVisible(true);
                            synchronized(NicoNicoLoginDownloadJob.DIALOG.lock)
                            {
                                NicoNicoLoginDownloadJob.DIALOG.lock.wait();
                            }
                            nicoLogin = true;
                        }
                        else if(chapter instanceof SundayChapter)
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
                        
                        DownloaderUtils.debug("autodetect Spot 2");
                        
                        if(frame != null)
                            frame.setStatus("Downloading");

                        currentThread.pause();
                        currentThread.addJobs(chapter.download(directory));
                        currentThread.unpause();
                        currentThread.waitUntilFinished();
                        
                        DownloaderUtils.debug("autodetect Spot 3");
                        
                        if(frame != null)
                            frame.setStatus("Finished");
                        return;
                    }
                    catch(Exception e)
                    {
                        DownloaderUtils.error("Handler failed: " + chapter.getClass(),
                                e instanceof ExecutionException ? 
                                    ((ExecutionException)e).getException() :
                                    e, false);
                    }
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