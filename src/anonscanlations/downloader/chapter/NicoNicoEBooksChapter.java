package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.json.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;
import anonscanlations.downloader.chapter.crypto.*;

/** Downloader for NicoNico E-Books, such as NicoNico Ace
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoEBooksChapter extends Chapter
{
    public static final Pattern IDMATCH = Pattern.compile("bk([0-9]+)$");
    private static final int RENEW_TIME = 3300000;

    private URL url;

    private transient String username, title, userid, bookid, dl_key, maki_address;
    private transient char[] password;
    private transient ArrayList<String> images;
    private transient NicoNicoLoginDownloadJob login;
    private transient boolean is_trial, use_drm;
    private transient long dl_key_time;

    public NicoNicoEBooksChapter(URL _url)
    {
        this(_url, null, null);
    }
    public NicoNicoEBooksChapter(URL _url, String _username, char[] _password)
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

        BookInfoDownloadJob bookDownload = new BookInfoDownloadJob();

        JSONDownloadJob lastRead = new JSONDownloadJob("Get last read",
                                            new URL("http://bkapi.seiga.nicovideo.jp/user/last_read?book_id=" + bookid));

        EPubDownloadJob ePubInfo = new EPubDownloadJob("Getting ePubInfo", null)
        {
            byte[] key;

            @Override
            public void run() throws Exception
            {
                setupDJ(this);
                addPOSTData("streaming", "init");
                url = new URL(maki_address);
                super.run();
            }

            public void doByteInput(ByteArrayInputStream byte_input) throws Exception
            {
                if(use_drm)
                {
                    byte[] array = DownloaderUtils.readAllBytes(byte_input);
                    key = NicoNicoEBooksDecrypt.createKey(array, userid.getBytes("UTF-8"), bookid.getBytes("UTF-8"));
                }
            }

            public void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception
            {
                if(!e.getName().endsWith(".xhtml"))
                    return;

                String page = use_drm ? new String(NicoNicoEBooksDecrypt.decrypt(DownloaderUtils.readAllBytes(input), key))
                                    : DownloaderUtils.readAllLines(input, "UTF-8");
                Document d = Jsoup.parse(page);
                for(Element elm : d.select("img"))
                    if(elm.hasAttr("class") && elm.attr("class").equals("img-screen"))
                        images.add(elm.attr("src"));
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

        final NicoNicoLoginDownloadJob renewLogin = new NicoNicoLoginDownloadJob(username, password)
        {
            {
                description = "Renew login";
            }
            @Override
            public void run() throws Exception
            {
                if(System.currentTimeMillis() - dl_key_time >= RENEW_TIME)
                {
                    super.run();
                }
            }
        };
        final BookInfoDownloadJob renewKey = new BookInfoDownloadJob()
        {
            {
                description = "Renew download key";
            }
            @Override
            public void run() throws Exception
            {
                if(System.currentTimeMillis() - dl_key_time >= RENEW_TIME)
                {
                    super.run();
                }
            }
        };
        
        for(int i = 0; i < images.size(); i++)
        {
            final File f = DownloaderUtils.fileName(directory, title, i + 1,
                            images.get(i).substring(images.get(i).lastIndexOf('.') + 1));
            if(f.exists())
                continue;
            
            EPubDownloadJob file = new EPubDownloadJob(DownloaderUtils.pageOutOf(i, 0, images.size()), maki)
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
                        key = NicoNicoEBooksDecrypt.createKey(array, userid.getBytes("UTF-8"), bookid.getBytes("UTF-8"));
                    }
                }

                public void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception
                {
                    if(read)
                        return;
                    read = true;

                    byte[] array = DownloaderUtils.readAllBytes(input);
                    DownloaderUtils.safeWrite(use_drm ? NicoNicoEBooksDecrypt.decrypt(array, key) : array, f);
                }
            };
            setupDJ(file);
            file.addPOSTData("streaming", "resources");
            file.addPOSTData("resources", "contents/" + images.get(i));
            list.add(renewLogin);
            list.add(renewKey);
            list.add(file);
        }

        return(list);
    }

    private void setupDJ(DownloadJob dj)
    {
        dj.addPOSTData("trial", is_trial ? "true" : "false");
        dj.addPOSTData("bookid", bookid);
        dj.addPOSTData("userid", userid);
        dj.addRequestProperty("Referer", "http://seiga.nicovideo.jp/book/static/swf/nicobookplayer.swf?1.0.5");
        dj.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        dj.addRequestProperty("x-nicobook-dl-key", dl_key);
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

    private class BookInfoDownloadJob extends JSONDownloadJob
    {
        public BookInfoDownloadJob()
        {
            super("Get book download info", null);
        }

        @Override
        public void run() throws Exception
        {
            url = new URL("http://bkapi.seiga.nicovideo.jp/book/" + bookid + "/download");
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
    }
}
