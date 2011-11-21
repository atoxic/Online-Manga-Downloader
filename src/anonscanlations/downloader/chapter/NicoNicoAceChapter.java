package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;
import java.net.*;
import java.security.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

import org.json.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.extern.*;
import anonscanlations.downloader.downloadjobs.*;

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
    private transient boolean is_trial, use_drm;

    public NicoNicoAceChapter(URL _url, String _username, char[] _password)
    {
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
        is_trial = true;
        use_drm = false;
    }

    public void init() throws Exception
    {
        String file = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
        Matcher matcher = IDMATCH.matcher(file);
        if(!matcher.matches())
            throw new Exception("Book ID not found");
        bookid = matcher.group(1);
        DownloaderUtils.debug("bookid: " + bookid);

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
                is_trial = obj.getBoolean("is_trial");
                use_drm = obj.getBoolean("use_drm");
                DownloaderUtils.debug("dl_key: " + dl_key);
                DownloaderUtils.debug("maki_address: " + maki_address);
                DownloaderUtils.debug("is_trial: " + is_trial);
                DownloaderUtils.debug("use_drm: " + use_drm);
            }
        };

        JSONDownloadJob lastRead = new JSONDownloadJob("Get last read",
                                            new URL("http://bkapi.seiga.nicovideo.jp/user/last_read?book_id=" + bookid));

        NicoEBooksDownloadJob ePubInfo = new NicoEBooksDownloadJob("Getting ePubInfo", null)
        {
            @Override
            public void run() throws Exception
            {
                post = "streaming=init&trial=" + is_trial + "&bookid=" + bookid + "&userid=" + userid;

                super.run();

                ZipEntry e;
                while((e = input.getNextEntry()) != null)
                {
                    if(!e.getName().endsWith(".xhtml"))
                    {
                        input.closeEntry();
                        continue;
                    }

                    String page;

                    if(use_drm)
                    {
                        byte[] array = DownloaderUtils.readToBytes(input);
                        ARC4 arc4 = new ARC4(key);
                        page = new String(arc4.arc4Crypt(array));
                    }
                    else
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String line;
                        page = "";
                        while((line = reader.readLine()) != null)
                            page += line;
                    }

                    Piccolo parser = new Piccolo();
                    InputSource is = new InputSource(new StringReader(page));
                    is.setEncoding("UTF-8");

                    parser.setContentHandler(new DefaultHandler()
                    {
                        @Override
                        public void startElement(String uri, String localName, String qName, Attributes atts)
                        {
                            if(localName.equals("img") && atts.getValue("class").equals("img-screen"))
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

            NicoEBooksDownloadJob file = new NicoEBooksDownloadJob("Page " + (i + 1),
                                            "streaming=resources&trial=" + is_trial + "&bookid=" + bookid +
                                            "&resources=" + URLEncoder.encode("contents/" + finalImage, "UTF-8") + "&userid=" + userid)
            {
                @Override
                public void run() throws Exception
                {
                    super.run();
                    
                    input.getNextEntry();
                    FileOutputStream fout = new FileOutputStream(
                                                    DownloaderUtils.fileName(finalDirectory, title, finalIndex + 1,
                                                                        finalImage.substring(finalImage.lastIndexOf('.') + 1)));

                    if(use_drm)
                    {
                        byte[] array = DownloaderUtils.readToBytes(input);
                        ARC4 arc4 = new ARC4(key);
                        fout.write(arc4.arc4Crypt(array));
                    }
                    else
                    {
                        for(int c = input.read(); c != -1; c = input.read())
                        {
                            fout.write(c);
                        }
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
            addRequestProperty("Cookie", login.getCookies());
            url = new URL(url.toString() + "?" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000));

            super.run();

            obj = new JSONObject(page);
        }
    }

    private class NicoEBooksDownloadJob extends DownloadJob
    {
        protected String post;
        protected HttpURLConnection conn;

        protected byte[] key;
        protected ZipInputStream input;

        public NicoEBooksDownloadJob(String _desc, String _post)
        {
            super(_desc);
            post = _post;
            conn = null;
            key = null;
            input = null;
        }

        public void run() throws Exception
        {
            conn = (HttpURLConnection) (new URL(maki_address)).openConnection();

            conn.setRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("x-nicobook-dl-key", dl_key);

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(post);
            wr.flush();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                throw new Exception("404 Page Not Found: " + maki_address);

            InputStream in = conn.getInputStream();
            byte[] array = DownloaderUtils.readToBytes(in);
            in.close();

            if(use_drm)
                key = createKey(array);
            input = new ZipInputStream(new ByteArrayInputStream(array));
        }
    }

    private static final int FIXED_LOCAL_FILE_HEADER_LENGTH = 30,
                            FILENAME_LENGTH_POSITION = 26,
                            EXTRAFIELD_LENGTH_POSITION = 28;
    private static final String SEED = "ojtDYr93p-h-9yt-ghOUG08fwigap1u0fnV";

    private byte[] createKey(byte[] array) throws Exception
    {
        int start = ((int)array[FILENAME_LENGTH_POSITION + 1] << 8) +
                            (int)array[FILENAME_LENGTH_POSITION];

        int length = ((int)array[EXTRAFIELD_LENGTH_POSITION + 1] << 8) +
                        (int)array[EXTRAFIELD_LENGTH_POSITION];

        byte[] salt = new byte[length];
        for(int i = 0; i < length; i++)
            salt[i] = array[FIXED_LOCAL_FILE_HEADER_LENGTH + start + i];

        int _loc_5 = salt[0] & 0xff;
        int userIdIndex = (_loc_5 & 192) >> 6;
        int identifierBookIdIndex = (_loc_5 & 48) >> 4;
        int seedIndex = (_loc_5 & 12) >> 2;
        int saltIndex = _loc_5 & 3;

        byte[][] _loc_4 = new byte[4][];
        _loc_4[userIdIndex] = userid.getBytes("UTF-8");
        _loc_4[identifierBookIdIndex] = bookid.getBytes("UTF-8");
        _loc_4[seedIndex] = SEED.getBytes("UTF-8");
        _loc_4[saltIndex] = salt;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for(byte[] ba : _loc_4)
            bos.write(ba);
        byte[] bytes = bos.toByteArray();
        bos.close();

        return(MessageDigest.getInstance("MD5").digest(bytes));
    }

    
}
