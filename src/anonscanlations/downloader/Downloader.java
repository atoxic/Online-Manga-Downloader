/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import java.net.*;

import anonscanlations.downloader.actibook.*;

/**
 *
 * @author Administrator
 */
public class Downloader extends Thread
{
    private static Downloader currentThread;
    private static List<DownloadJob> jobs;
    
    private boolean die, suspended;
    private final Object finished, waitForJobs;
    private Downloader()
    {
        jobs = Collections.synchronizedList(new ArrayList<DownloadJob>());
        die = false;
        suspended = false;
        finished = new Object();
        waitForJobs = new Object();
    }
    public void addJob(DownloadJob job)
    {
        System.out.println("Add Job");
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
    public void waitUntilFinished() throws InterruptedException
    {
        synchronized(finished)
        {
            finished.wait();
        }
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
                        System.out.println("Wait");
                        waitForJobs.wait();
                    }
                }
                
                if(die)
                    break;
                
                DownloadJob job = jobs.remove(0);
                System.out.println("Running job: " + job);
                job.run();
            }
            catch(InterruptedException ie)
            {
            }
            catch(Exception ex)
            {
                DownloaderUtils.error("Error in Downloader", ex, false);
            }
        }
    }

    public static Downloader getDownloader()
    {
        return(currentThread);
    }
    public static void main(String[] args) throws Exception
    {
        currentThread = new Downloader();
        currentThread.start();

        ActibookChapter chapter = new ActibookChapter(new URL("http://www.square-enix.com/jp/magazine/ganganonline/comic/ryushika/viewer/001/_SWF_Window.html"));

        System.out.println("Init");
        currentThread.pause();
        chapter.init();
        currentThread.pause();
        
        // wait until init is finished
        currentThread.waitUntilFinished();
        
        System.out.println("Download");
        currentThread.pause();
        chapter.download(new File("D:\\test\\"));
        currentThread.pause();

        // wait until download is finished
        currentThread.waitUntilFinished();
        currentThread.kill();
    }
}
