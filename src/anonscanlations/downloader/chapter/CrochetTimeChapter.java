/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import java.net.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;
import anonscanlations.downloader.chapter.crypto.*;

/**
 *
 * @author /a/non
 */
public class CrochetTimeChapter extends Chapter
{
    private URL url;
    private String getImage, cgi, dir, src;
    private ArrayList<String> list;
    private Map<String, String> cookies;

    public CrochetTimeChapter(URL _url)
    {
        url = _url;
        getImage = null;
        cookies = new HashMap<String, String>();
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
                
                if(!page.contains("openTTCrochet"))
                    throw new Exception("Parameter unknown");
                
                int start = page.indexOf('"', page.indexOf("openTTCrochet")) + 1;
                int end = page.indexOf('"', start);
                getImage = page.substring(start, end);
                DownloaderUtils.debug("getImage: " + getImage);
                
                cookies.putAll(response.cookies());
                DownloaderUtils.debug("cookies: " + cookies);
            }
        };
        JSoupDownloadJob getImageDJ = new JSoupDownloadJob("Get T-Time script", null)
        {
            @Override
            public void run() throws Exception
            {
                if(getImage != null)
                {
                    setCookies(cookies);
                    url = new URL(CrochetTimeChapter.this.url, getImage);
                    super.run();
                    cookies.putAll(response.cookies());
                    
                    byte[] bytes = getBytes();
                    byte[] decBytes = new byte[bytes.length - 4];
                    System.arraycopy(bytes, 4, decBytes, 0, bytes.length - 4);
                    CrochetTimeDecrypt.unscramble(decBytes);
                    String script = new String(decBytes);
                    DownloaderUtils.debug("script: " + script);
                    
                    cgi = getVariable(script, "cgi");
                    dir = getVariable(script, "dir");
                    src = getVariable(script, "src");
                    DownloaderUtils.debug("cgi: " + cgi);
                    DownloaderUtils.debug("dir: " + dir);
                    DownloaderUtils.debug("src: " + src);
                }
            }
        };
        ByteArrayDownloadJob getList = new ByteArrayDownloadJob("Get the file list", null)
        {
            @Override
            public void run() throws Exception
            {
                setCookies(cookies);
                String filelistURL = CrochetTimeDecrypt.scrambleURL(dir + "/" + src 
                                        + "&B" + String.format("%08x", (int)(32767 * Math.random())));
                url = new URL(cgi + "?" + filelistURL);
                super.run();
                cookies.putAll(response.cookies());

                list = CrochetTimeDecrypt.fileList(getBytes());
                if(list.isEmpty())
                    throw new Exception("No file list");
            }
        };
        ret.add(mainPage);
        ret.add(getImageDJ);
        ret.add(getList);
        return(ret);
    }
    
    private static String getVariable(String script, String var)
    {
        Pattern varMatch = Pattern.compile(var + "\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = varMatch.matcher(script);
        while(matcher.find())
            return(matcher.group(1));
        return(null);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> jobsList = new ArrayList<DownloadJob>();
        for(int i = 0; i < list.size(); i++)
        {
            final File f = DownloaderUtils.fileName(directory, src.split("[.]")[0], i, "jpg");
            if(f.exists())
                continue;

            ByteArrayDownloadJob page = new ByteArrayDownloadJob("Page " + i,
                                        new URL(cgi + "?" + CrochetTimeDecrypt.scrambleURL(dir + "/" + list.get(i))))
            {
                @Override
                public void run() throws Exception
                {
                    setCookies(cookies);
                    super.run();
                    cookies.putAll(response.cookies());

                    byte[] bytes = getBytes();
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
