/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import java.math.*;
import java.security.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class SundayChapter extends Chapter
{
    private String key1, key2, key3, key4, shd;
    private URL url, keyURL;

    private transient String cookies, otk, server;
    private transient int min, max;
    private transient long timestamp;

    public SundayChapter(URL _url, URL _keyURL)
    {
        keyURL = _keyURL;
        url = _url;

        String urlString = url.toString();
        key1 = getParam(urlString, "key1");
        key2 = getParam(urlString, "key2");
        key3 = getParam(urlString, "key3");
        key4 = getParam(urlString, "key4");
        shd = getParam(urlString, "shd");
    }

    private String getParam(String url, String param)
    {
        int index = url.indexOf(param + "="), endIndex = url.indexOf('&', index);
        if(endIndex == -1)
            endIndex = url.length();
        return(url.substring(index + param.length() + 1, endIndex));
    }

    public void init() throws Exception
    {
        SessionDownloadJob session = new SessionDownloadJob(keyURL);
        MainFileDownloadJob mainFile = new MainFileDownloadJob();
        XMLDownloadJob xml = new XMLDownloadJob();
        
        Downloader.getDownloader().addJob(session);
        Downloader.getDownloader().addJob(mainFile);
        Downloader.getDownloader().addJob(xml);
    }
    
    public void download(File directory) throws Exception
    {
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        String origURL = server + "data/" + key1 + "/" + key2 + "/" + key3 + "/" + key4 + "/bin/";

        for(int i = min; i <= max; i++)
        {
            String url = origURL + key3 + "_" + key4 + "_" + String.format("%04d", i) + ".bin?e=" + timestamp,
                    toHash = "e47ec34c9cf59bca8d8eb865896b74ff88f953d3" + url,
                    hashedURL = "";
            md5.reset();
            md5.update(toHash.getBytes());
            byte[] bytes = md5.digest();
            String tmp = "";
            for(int j = 0; j < bytes.length; j++)
            {
                tmp = (Integer.toHexString(0xFF & bytes[j]));
                if (tmp.length() == 1)
                    hashedURL += "0" + tmp;
                else
                    hashedURL += tmp;
            }

            url += "&h=" + hashedURL;

            PCViewerDownloadJob file = new PCViewerDownloadJob("Page " + i, new URL(url), DownloaderUtils.fileName(directory, key3 + "_c" + key4, i, "jpg"));
            Downloader.getDownloader().addJob(file);
        }
    }

    private class SessionDownloadJob extends DownloadJob
    {
        private URL url;
        public SessionDownloadJob(URL _url)
        {
            super("Starts a new session");
            url = _url;
        }
        public void run() throws Exception
        {
            DownloaderUtils.debug("url: " + url);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setInstanceFollowRedirects(false);
            urlConn.connect();
            DownloaderUtils.debug("code: " + urlConn.getResponseCode());

            String headerName = null;
            for(int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++)
            {
                if(headerName.equals("Set-Cookie"))
                {
                    cookies = urlConn.getHeaderField(i);
                    int index = cookies.indexOf("SESS");
                    if(index != -1)
                    {
                        DownloaderUtils.debug("cookies: " + cookies);
                        break;
                    }
                }
            }

            if(urlConn.getResponseCode() == 302)
            {
                for(int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++)
                {
                    if(headerName.equals("Location"))
                    {
                        String line = urlConn.getHeaderField(i);
                        DownloaderUtils.debug("line: " + line);
                        int index = line.indexOf("&otk=");
                        otk = line.substring(index + 5, line.indexOf('&', index + 1));
                    }
                }
            }
            else
            {
                BufferedReader stream = new BufferedReader(
                                            new InputStreamReader(
                                                urlConn.getInputStream(), "UTF-8"));
                String line;
                int index;
                while((line = stream.readLine()) != null)
                {
                    index = line.indexOf("&otk=");
                    if(index != -1)
                    {
                        DownloaderUtils.debug("line: " + line);
                        otk = line.substring(index + 5, line.indexOf('\'', index));
                        if(otk.indexOf('&') != -1)
                        {
                            otk = otk.substring(0, otk.indexOf('&'));
                        }
                        break;
                    }
                }
                stream.close();
            }

            urlConn.disconnect();

            DownloaderUtils.debug("otk: " + otk);
        }
    }

    private class MainFileDownloadJob extends PageDownloadJob
    {
        public MainFileDownloadJob()
        {
            super("Downloads the main file", null, "UTF-8");
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_main.php?key1=" + key1 +
                                "&key2=" + key2 + "&key3=" + key3 +
                                "&key4=" + key4 + "&sp=-1&re=0&shd=" + shd +
                                "&otk=" + otk + "&vo=1");
            DownloaderUtils.debug("URL: " + this.url);
            this.cookies = SundayChapter.this.cookies;

            super.run();
        }
    }

    private class MainXMLDownloadJob extends PageDownloadJob
    {
        public MainXMLDownloadJob()
        {
            super("Downloads the main XML file", null, "UTF-8");
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_main.xml");
            DownloaderUtils.debug("URL: " + this.url);
            this.cookies = SundayChapter.this.cookies;

            super.run();
        }
    }

    private class XMLDownloadJob extends PageDownloadJob
    {
        public XMLDownloadJob()
        {
            super("Downloads the XML file", null, "UTF-8");
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_otk.php?key1=" + key1 +
                            "&key2=" + key2 + "&key3=" + key3 +
                            "&key4=" + key4 + "&sp=-1&re=0&otk=" + otk);
            DownloaderUtils.debug("url: " + this.url);
            this.cookies = SundayChapter.this.cookies;

            super.run();

            DownloaderUtils.debug("xml: " + page);

            if(page.equals("NG") || page.contains("pagecount=\"0\""))
                throw new Exception("XML file NG");
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(page));
            Document d = builder.parse(is);
            Element root = d.getDocumentElement();

            String serverTimestamp = ((Element)root.getElementsByTagName("ServerTimestamp").item(0))
                                        .getAttribute("val"),
                    availTimestamp = ((Element)root.getElementsByTagName("AvailableTime").item(0))
                                        .getAttribute("val");
            server = ((Element)root.getElementsByTagName("ContentsServerPath").item(0))
                                        .getAttribute("val");
            String list[] = ((Element)root.getElementsByTagName("SamplePageList").item(0))
                                        .getAttribute("list").split("-");

            timestamp = Long.parseLong(serverTimestamp) + Long.parseLong(availTimestamp);
            min = Integer.parseInt(list[0]);
            max = Integer.parseInt(list[1]);

            DownloaderUtils.debug("serverTimestamp: " + serverTimestamp);
            DownloaderUtils.debug("availTimestamp: " + availTimestamp);
            DownloaderUtils.debug("timestamp: " + timestamp);
            DownloaderUtils.debug("server: " + server);
            DownloaderUtils.debug("range: " + min + " to " + max);
        }
    }
}
