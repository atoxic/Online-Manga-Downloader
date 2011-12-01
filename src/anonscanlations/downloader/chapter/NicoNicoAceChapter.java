package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;
import java.net.*;
import java.security.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
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
    private transient long dl_key_time;

    public NicoNicoAceChapter(URL _url)
    {
        this(_url, null, null);
    }
    public NicoNicoAceChapter(URL _url, String _username, char[] _password)
    {
        url = _url;
        username = _username;
        password = _password;
        title = null;
        userid = null;
        dl_key = null;
        dl_key_time = 0;
        maki_address = null;
        bookid = null;
        images = new ArrayList<String>();
        login = null;
        is_trial = true;
        use_drm = false;
    }

    @Override
    public void getRequiredInfo(LoginManager s) throws Exception
    {
        if(username == null || password == null)
        {
            username = s.getNicoLogin().getEMail();
            password = s.getNicoLogin().getPassword();
        }
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
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
                try
                {
                    super.run();
                }
                catch(IOException e)
                {
                    if(e.getMessage().startsWith("401"))
                        throw new IOException("Incorrect login");
                    throw e;
                }

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
                try
                {
                    super.run();
                }
                catch(IOException e)
                {
                    if(e.getMessage().startsWith("401"))
                        throw new IOException("Your NicoNico account isn't registered to view BookWalker chapters");
                    throw e;
                }

                dl_key = obj.getString("dl_key");
                dl_key_time = System.currentTimeMillis();
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

        EPubDownloadJob ePubInfo = new EPubDownloadJob("Getting ePubInfo", null)
        {
            byte[] key;

            @Override
            public void run() throws Exception
            {
                addPOSTData("streaming", "init");
                addPOSTData("trial", is_trial ? "true" : "false");
                addPOSTData("bookid", bookid);
                addPOSTData("userid", userid);
                url = new URL(maki_address);

                addRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
                addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                addRequestProperty("x-nicobook-dl-key", dl_key);
                
                super.run();
            }

            public void doByteInput(ByteArrayInputStream byte_input) throws Exception
            {
                if(use_drm)
                {
                    byte[] array = DownloaderUtils.readAllBytes(byte_input);
                    key = createKey(array);
                }
            }

            public void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception
            {
                if(!e.getName().endsWith(".xhtml"))
                    return;

                String page;

                if(use_drm)
                {
                    byte[] array = DownloaderUtils.readAllBytes(input);
                    ARC4 arc4 = new ARC4(key);
                    page = new String(arc4.arc4Crypt(array));
                }
                else
                {
                    page = DownloaderUtils.readAllLines(input, "UTF-8");
                }

                Document d = Jsoup.parse(page);
                for(Element elm : d.select("img"))
                {
                    if(elm.hasAttr("class") && elm.attr("class").equals("img-screen"))
                        images.add(elm.attr("src"));
                }
            }
        };

        list.add(login);
        list.add(service);
        list.add(user);
        list.add(book);
        list.add(bookDownload);
        list.add(lastRead);
        list.add(ePubInfo);
        return(list);
    }

    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        URL maki = new URL(maki_address);
        
        final JSONDownloadJob renewKey = new JSONDownloadJob("Renew key",
                                            new URL("http://bkapi.seiga.nicovideo.jp/book/" + bookid + "/download"))
        {
            @Override
            public void run() throws Exception
            {
                if(System.currentTimeMillis() - dl_key_time >= 3300000)
                {
                    try
                    {
                        super.run();
                    }
                    catch(IOException e)
                    {
                        if(e.getMessage().startsWith("401"))
                            throw new IOException("Your NicoNico account isn't registered to view BookWalker chapters");
                        throw e;
                    }

                    dl_key = obj.getString("dl_key");
                    dl_key_time = System.currentTimeMillis();
                    DownloaderUtils.debug("dl_key: " + dl_key);
                }
            }
        };
        
        for(int i = 0; i < images.size(); i++)
        {
            final File f = DownloaderUtils.fileName(directory, title, i + 1,
                            images.get(i).substring(images.get(i).lastIndexOf('.') + 1));
            if(f.exists())
                continue;
            
            EPubDownloadJob file = new EPubDownloadJob("Page " + (i + 1), maki)
            {
                byte[] key;
                boolean read;

                {
                    key = null;
                    read = false;
                }

                public void doByteInput(ByteArrayInputStream byte_input) throws Exception
                {
                    if(use_drm)
                    {
                        byte[] array = DownloaderUtils.readAllBytes(byte_input);
                        key = createKey(array);
                    }
                }

                public void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception
                {
                    if(read)
                        return;
                    read = true;

                    byte[] array = DownloaderUtils.readAllBytes(input);
                    if(use_drm)
                    {
                        ARC4 arc4 = new ARC4(key);
                        DownloaderUtils.safeWrite(arc4.arc4Crypt(array), f);
                    }
                    else
                    {
                        DownloaderUtils.safeWrite(array, f);
                    }
                }
            };
            file.addPOSTData("streaming", "resources");
            file.addPOSTData("trial", is_trial ? "true" : "false");
            file.addPOSTData("bookid", bookid);
            file.addPOSTData("userid", userid);
            file.addPOSTData("resources", "contents/" + images.get(i));
            file.addRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
            file.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            file.addRequestProperty("x-nicobook-dl-key", dl_key);
            list.add(renewKey);
            list.add(file);
        }

        return(list);
    }

    private class JSONDownloadJob extends JSoupDownloadJob
    {
        protected JSONObject obj;

        public JSONDownloadJob(String _desc, URL _url)
        {
            super(_desc, _url);
        }

        @Override
        public void run() throws Exception
        {
            url = new URL(url.toString() + "?" + System.currentTimeMillis());
            setCookies(login.getCookies());

            super.run();

            obj = new JSONObject(response.body());
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
