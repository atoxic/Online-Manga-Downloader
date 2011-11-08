package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;
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
    public static final Pattern IDMATCH = Pattern.compile("bk([0-9]+)$");

    private URL url;

    private transient String username, title, userid, bookid, dl_key, maki_address;
    private transient char[] password;
    private transient ArrayList<String> images;
    private transient NicoNicoLoginDownloadJob login;

    public NicoNicoAceChapter(URL _url, String _username, char[] _password)
    {
        // TODO: actually use URL to parse.  It's assumed that the book id is 1
        url = _url;
        username = _username;
        password = _password;
        title = null;
        userid = null;
        dl_key = null;
        maki_address = null;
        bookid = null;
        images = new ArrayList<String>();
        login = null;
    }

    public void init() throws Exception
    {
        String file = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
        Matcher matcher = IDMATCH.matcher(file);
        if(!matcher.matches())
            throw new Exception("Book ID not found");
        bookid = matcher.group(1);

        login = new NicoNicoLoginDownloadJob(username, password);

        JSONDownloadJob service = new JSONDownloadJob("Get service info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/service/status"));

        JSONDownloadJob user = new JSONDownloadJob("Get user info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/user/status"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                userid = obj.getJSONObject("user").get("id").toString();
                DownloaderUtils.debug("userid: " + userid);
            }
        };

        JSONDownloadJob book = new JSONDownloadJob("Get book info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/book/" + bookid))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                title = obj.getJSONObject("book").getString("name");
                DownloaderUtils.debug("title: " + title);
            }
        };

        JSONDownloadJob bookDownload = new JSONDownloadJob("Get book download info",
                                            new URL("http://bkapi.seiga.nicovideo.jp/book/" + bookid + "/download"))
        {
            @Override
            public void run() throws Exception
            {
                super.run();

                dl_key = obj.getString("dl_key");
                maki_address = obj.getString("maki_address");
                DownloaderUtils.debug("dl_key: " + dl_key);
                DownloaderUtils.debug("maki_address: " + maki_address);
            }
        };

        JSONDownloadJob lastRead = new JSONDownloadJob("Get last read",
                                            new URL("http://bkapi.seiga.nicovideo.jp/user/last_read?book_id=" + bookid));

        ZipDownloadJob ePubInfo = new ZipDownloadJob("Getting ePubInfo", null)
        {
            @Override
            public void run() throws Exception
            {
                post = "streaming=init&trial=true&bookid=" + bookid + "&userid=" + userid;

                super.run();

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

                    input.closeEntry();
                }
                input.close();
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

            ZipDownloadJob file = new ZipDownloadJob("Page " + (i + 1),
                                            "streaming=resources&trial=true&bookid=" + bookid +
                                            "&resources=" + URLEncoder.encode("contents/" + finalImage, "UTF-8") + "&userid=" + userid)
            {
                @Override
                public void run() throws Exception
                {
                    super.run();

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

    private class JSONDownloadJob extends PageDownloadJob
    {
        protected JSONObject obj;

        public JSONDownloadJob(String _desc, URL _url)
        {
            super(_desc, _url, "UTF-8");
        }

        @Override
        public void run() throws Exception
        {
            this.cookies = login.getCookies();
            url = new URL(url.toString() + "?" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

            super.run();

            obj = new JSONObject(page);
        }
    }

    private class ZipDownloadJob extends DownloadJob
    {
        protected ZipInputStream input;
        protected String post;

        public ZipDownloadJob(String _desc, String _post)
        {
            super(_desc);
            post = _post;
        }

        public void run() throws Exception
        {
            HttpURLConnection conn = (HttpURLConnection) (new URL(maki_address)).openConnection();

            conn.setRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("x-nicobook-dl-key", dl_key);

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(post);
            wr.flush();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                throw new Exception("404 Page Not Found: " + maki_address);

            input = new ZipInputStream(conn.getInputStream());
        }
    }
}
