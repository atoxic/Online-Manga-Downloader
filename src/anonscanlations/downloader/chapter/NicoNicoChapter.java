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

    private transient String username, password, cookies, title, id;
    private transient ArrayList<String> ids;
    public NicoNicoChapter(URL _url, String _username, String _password)
    {
        url = _url;
        username = _username;
        password = _password;
        cookies = null;
        title = null;
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
                wr.write("next_url=%2Fmanga%2F&mail=" + username + "&password=" + password);
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

                ids = new ArrayList<String>();
                
                // Should use SAX for this
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(page));
                Document d = builder.parse(is);
                Element doc = d.getDocumentElement();
                NodeList idElements = doc.getElementsByTagName("id");
                for(int i = 0; i < idElements.getLength(); i++)
                {
                    Element idElement = (Element)idElements.item(i);
                    if(((Element)idElement.getParentNode()).getNodeName().equals("image"))
                    {
                        ids.add(idElement.getTextContent());
                    }
                }
            }
        };
        Downloader.getDownloader().addJob(login);
        Downloader.getDownloader().addJob(info);
        Downloader.getDownloader().addJob(data);
    }

    public void download(File directory) throws Exception
    {
        int i = 1;
        for(String id : ids)
        {
            FileDownloadJob page = new FileDownloadJob("Page " + i,
                            new URL("http://lohas.nicoseiga.jp/thumb/" + id + "l?"),
                            DownloaderUtils.fileName(directory, title, i, "jpg"));
            Downloader.getDownloader().addJob(page);
            i++;
        }
    }
}
