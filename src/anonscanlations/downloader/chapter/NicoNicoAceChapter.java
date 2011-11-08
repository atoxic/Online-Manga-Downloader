package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.json.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoAceChapter extends Chapter
{
    private URL url;

    private transient String username, cookies, title, userid, bookid, dl_key, maki_address;
    private transient char[] password;
    private transient ArrayList<String> images;

    public NicoNicoAceChapter(URL _url, String _username, char[] _password)
    {
        // TODO: actually use URL to parse.  It's assumed that the book id is 1
        url = _url;
        username = _username;
        password = _password;
        cookies = null;
        title = null;
        userid = null;
        dl_key = null;
        maki_address = null;

        bookid = null;
        images = new ArrayList<String>();
    }

    public void init() throws Exception
    {
        bookid = "584";

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
                        NicoNicoAceChapter.this.cookies = conn.getHeaderField(i);
                    }
                }
            }
        };

        PageDownloadJob service = new PageDownloadJob("Get service info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/service/status"),
                                            "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoAceChapter.this.cookies;
                url = new URL(url.toString() + "?" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

                super.run();

                DownloaderUtils.debug(page);
            }
        };

        PageDownloadJob user = new PageDownloadJob("Get user info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/user/status"),
                                            "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoAceChapter.this.cookies;
                url = new URL(url.toString() + "?" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

                super.run();

                JSONObject obj = new JSONObject(page);
                userid = obj.getJSONObject("user").get("id").toString();
                DownloaderUtils.debug(userid);
            }
        };

        PageDownloadJob book = new PageDownloadJob("Get book info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/book/" + bookid),
                                            "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoAceChapter.this.cookies;
                url = new URL(url.toString() + "?" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

                super.run();

                JSONObject obj = new JSONObject(page);
                title = obj.getJSONObject("book").getString("name");
                DownloaderUtils.debug(title);
            }
        };

        PageDownloadJob bookDownload = new PageDownloadJob("Get book download info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/book/" + bookid + "/download"),
                                            "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoAceChapter.this.cookies;
                url = new URL(url.toString() + "?" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

                super.run();

                JSONObject obj = new JSONObject(page);
                dl_key = obj.getString("dl_key");
                maki_address = obj.getString("maki_address");
                DownloaderUtils.debug(dl_key);
                DownloaderUtils.debug(maki_address);
            }
        };

        PageDownloadJob lastRead = new PageDownloadJob("Get last read",
                                            new URL("http://bkapi.seiga.nicovideo.jp/user/last_read?book_id=" + bookid),
                                            "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                this.cookies = NicoNicoAceChapter.this.cookies;
                url = new URL(url.toString() + "&" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

                super.run();

                JSONObject obj = new JSONObject(page);
                DownloaderUtils.debug(obj.getString("message"));
            }
        };

        DownloadJob ePubInfo = new DownloadJob("Getting ePubInfo")
        {
            public void run() throws Exception
            {
                HttpURLConnection conn = (HttpURLConnection) (new URL(maki_address)).openConnection();

                conn.setRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("x-nicobook-dl-key", dl_key);

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write("streaming=init&trial=true&bookid=" + bookid + "&userid=" + userid);
                //wr.write("streaming=resources&trial=true&bookid=1&resources=contents%2F000000460%2Fimg%2Fkgm00447%5F001%2Ejpg&userid=10638502");
                wr.flush();
                
                if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                    throw new Exception("404 Page Not Found: " + maki_address);

                //*
                ZipInputStream input = new ZipInputStream(conn.getInputStream());
                ZipEntry e;
                while((e = input.getNextEntry()) != null)
                {
                    if(!e.getName().endsWith(".xhtml"))
                    {
                        input.closeEntry();
                        continue;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String page = "", line;
                    while((line = reader.readLine()) != null)
                        page += line;

                    XMLReader parser = XMLReaderFactory.createXMLReader();
                    InputSource is = new InputSource(new StringReader(page));
                    is.setEncoding("UTF-8");

                    parser.setContentHandler(new DefaultHandler()
                    {
                        @Override
                        public void startElement(String uri, String localName, String qName, Attributes atts)
                        {
                            if(localName.equals("img"))
                                images.add(atts.getValue("src"));
                        }
                    });
                    // make it not get the dtd file
                    parser.setEntityResolver(new EntityResolver()
                    {
                        @Override
                        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
                        {
                            return(systemId.contains("dtd") ? new InputSource(new StringReader("")) : null);
                        }
                    });

                    parser.parse(is);
                    // */

                    input.closeEntry();
                }
                input.close();
                // */

                for(String s : images)
                    DownloaderUtils.debug("img 2: " + s);
            }
        };

        downloader().addJob(login);
        downloader().addJob(service);
        downloader().addJob(user);
        downloader().addJob(book);
        downloader().addJob(bookDownload);
        downloader().addJob(lastRead);
        downloader().addJob(ePubInfo);
    }

    public void download(File directory) throws Exception
    {
        final File finalDirectory = directory;

        for(int i = 0; i < images.size(); i++)
        {
            final int finalIndex = i;
            final String finalImage = images.get(finalIndex);

            DownloadJob file = new DownloadJob("Page " + (i + 1))
            {
                public void run() throws Exception
                {
                    HttpURLConnection conn = (HttpURLConnection) (new URL(maki_address)).openConnection();

                    conn.setRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("x-nicobook-dl-key", dl_key);

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write("streaming=resources&trial=true&bookid=" + bookid + "&resources=" + URLEncoder.encode("contents/" + finalImage, "UTF-8") + "&userid=" + userid);
                    DownloaderUtils.debug("Wrote: " + "streaming=resources&trial=true&bookid=1&resources=" + URLEncoder.encode("contents/" + finalImage, "UTF-8") + "&userid=" + userid);
                    wr.flush();

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                        throw new Exception("404 Page Not Found: " + maki_address);

                    ZipInputStream input = new ZipInputStream(conn.getInputStream());
                    ZipEntry e = input.getNextEntry();
                    FileOutputStream fout = new FileOutputStream(
                                                    DownloaderUtils.fileName(finalDirectory, title, finalIndex + 1,
                                                                        finalImage.substring(finalImage.lastIndexOf('.') + 1)));
                    for(int c = input.read(); c != -1; c = input.read())
                    {
                        fout.write(c);
                    }
                    fout.close();
                    input.close();
                }
            };
            downloader().addJob(file);
        }
    }
}
