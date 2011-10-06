package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import anonscanlations.downloader.*;

/** 
 * 
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoChapter extends Chapter
{
    public static final Pattern IDMATCH = Pattern.compile(".*?([0-9]+)$");

    private URL url;

    private transient String username, cookies, title, id;
    private transient char[] password;
    private transient HashMap<String, NicoImage> images;
    public NicoNicoChapter(URL _url, String _username, char[] _password)
    {
        url = _url;
        username = _username;
        password = _password;
        cookies = null;
        title = null;
    }

    private class NicoImage
    {
        private String id, se_path;
        private ArrayList<String> comments, user_hashes;
        NicoImage(String _id)
        {
            id = _id;
            se_path = null;
            comments = new ArrayList<String>();
            user_hashes = new ArrayList<String>();
        }
        public void setSEPath(String _se_path){ se_path = _se_path; }
        public void addComment(String comment, String hash)
        {
            comments.add(comment);
            user_hashes.add(hash);
        }
    }

    public void init() throws Exception
    {
        Matcher matcher = IDMATCH.matcher(url.toString());
        if(!matcher.matches())
            throw new Exception("ID not found");
        id = matcher.group(1);
        DownloadJob login = new DownloadJob("Login to NicoNico")
        {
            public void run() throws Exception
            {
                URL url = new URL("https://secure.nicovideo.jp/secure/login?site=seiga");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write("next_url=%2Fmanga%2F&mail=" + username + "&password=");
                wr.write(password);
                wr.flush();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                    throw new Exception("404 Page Not Found: " + url);
                String headerName = null;
                for(int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
                {
                    if(headerName.equals("Set-Cookie") && !conn.getHeaderField(i).contains("deleted"))
                    {
                        NicoNicoChapter.this.cookies = conn.getHeaderField(i);
                        //DownloaderUtils.debug("cookies: " + NicoNicoChapter.this.cookies);
                    }
                }
            }
        };
        PageDownloadJob info = new PageDownloadJob("Get info", new URL("http://seiga.nicovideo.jp/api/theme/info?id=" + id), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoChapter.this.cookies;
                super.run();

                int index = page.indexOf("<title>");
                if(index == -1)
                    throw new Exception("Title not found");
                title = page.substring(index + 7, page.indexOf("</", index));
                DownloaderUtils.debug("title: " + title);
            }
        };
        PageDownloadJob data = new PageDownloadJob("Get data", new URL("http://seiga.nicovideo.jp/api/theme/data?theme_id=" + id), "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoChapter.this.cookies;
                super.run();
                parseData(page);
            }
        };
        Downloader.getDownloader().addJob(login);
        Downloader.getDownloader().addJob(info);
        Downloader.getDownloader().addJob(data);
    }

    private void parseData(String page) throws Exception
    {
        images = new HashMap<String, NicoImage>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(page));
        Document d = builder.parse(is);
        Element doc = d.getDocumentElement();

        Element imageList = (Element)doc.getElementsByTagName("image_list").item(0);
        NodeList imageElements = imageList.getChildNodes();
        for(int i = 0; i < imageElements.getLength(); i++)
        {
            Node n = imageElements.item(i);
            if(!(n instanceof Element))
                continue;
            Element image = (Element)n;
            Element image_id = (Element)image.getElementsByTagName("id").item(0);
            NicoImage nicoImage = new NicoImage(image_id.getTextContent());
            images.put(nicoImage.id, nicoImage);

            NodeList se_path = image.getElementsByTagName("se_path");
            if(se_path.getLength() > 0)
            {
                nicoImage.setSEPath(((Element)se_path.item(0)).getTextContent());
            }
        }
    }

    public void download(File directory) throws Exception
    {
        //*
        int i = 1;
        for(NicoImage image : images.values())
        {
            FileDownloadJob page = new FileDownloadJob("Page " + i,
                            new URL("http://lohas.nicoseiga.jp/thumb/" + image.id + "l?"),
                            DownloaderUtils.fileName(directory, title, i, "jpg"));
            Downloader.getDownloader().addJob(page);
            if(image.se_path != null)
            {
                FileDownloadJob sfx = new FileDownloadJob("Page " + i + " SFX",
                            new URL("http://lohas.nicoseiga.jp/" + image.se_path),
                            DownloaderUtils.fileName(directory, title, i, "mp3"));
                Downloader.getDownloader().addJob(sfx);
            }
            
            i++;
        }
        // */
    }
}