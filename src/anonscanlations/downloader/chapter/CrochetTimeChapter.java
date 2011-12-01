/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public class CrochetTimeChapter extends Chapter
{
    private String dbmd, basePath, suffix;

    private URL url;
    private String path, filepath;
    private ArrayList<String> list;

    public CrochetTimeChapter(URL _url)
    {
        url = _url;
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> ret = new ArrayList<DownloadJob>();
        JSoupDownloadJob mainPage = new JSoupDownloadJob("Get the page", url)
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                String page = response.body();
                int index = page.indexOf("var book"), start = -1, end = -1;
                // Voyager
                if(index != -1)
                {
                    start = page.indexOf('\'', index) + 1;
                    end = page.indexOf('_', start);
                    path = page.substring(start, end);
                    
                    dbmd = "http://shangrila.voyager-store.com/dBmd?";
                    basePath = "/home/dotbook/rs2_contents/voyager-store_contents/";
                    suffix = "_pc_image_crochet";
                    filepath = basePath + path + "/";
                }
                // Kondansha Bitway
                else
                {
                    index = page.indexOf("BOOK=");
                    if(index == -1)
                        throw new Exception("Book path not found");

                    start = page.indexOf('=', index) + 1;
                    end = page.indexOf('.', start);
                    path = page.substring(start, end);

                    dbmd = "http://comic.bitway.ne.jp/kc/cgi-bin/dBmd.cgi?";
                    basePath = "/opt/pccs/share2/cplus/t_files//";
                    suffix = "";
                    filepath = basePath;
                }

                DownloaderUtils.debug("path: " + path);
            }
        };
        ByteArrayDownloadJob getList = new ByteArrayDownloadJob("Get the file list", null)
        {
            @Override
            public void run() throws Exception
            {
                String filelistURL = CrochetTimeDecrypt.scrambleURL(filepath + path + suffix +
                                        ".book.bmit&B" + String.format("%08x", (int)(32767 * Math.random())));
                url = new URL(dbmd + filelistURL);
                
                super.run();

                list = CrochetTimeDecrypt.fileList(bytes);
                if(list.isEmpty())
                    throw new Exception("No file list");
            }
        };
        ret.add(mainPage);
        ret.add(getList);
        return(ret);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> jobsList = new ArrayList<DownloadJob>();

        // for the file list and downloaded files
        final File finalDirectory = directory;

        for(int i = 0; i < list.size(); i++)
        {
            final int finalIndex = i;
            final File f = DownloaderUtils.fileName(finalDirectory, path, finalIndex, "jpg");
            if(f.exists())
                continue;

            ByteArrayDownloadJob page = new ByteArrayDownloadJob("Page " + i,
                                        new URL(dbmd + CrochetTimeDecrypt.scrambleURL(filepath + list.get(i))))
            {
                @Override
                public void run() throws Exception
                {
                    super.run();
                    
                    CrochetTimeDecrypt.decrypt(bytes);
                    
                    BufferedInputStream input = new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)));
                    FileOutputStream output = new FileOutputStream(f);
                    byte[] buf = new byte[1024];
                    try
                    {
                        while(input.read(buf) != -1)
                        {
                            output.write(buf);
                        }
                    }
                    catch(Exception e)
                    {
                        input.close();
                        input = new BufferedInputStream(new ByteArrayInputStream(bytes));
                        while(input.read(buf) != -1)
                        {
                            output.write(buf);
                        }
                    }
                    finally
                    {
                        input.close();
                        output.close();
                    }
                }
            };

            jobsList.add(page);
        }
        return(jobsList);
    }
}
