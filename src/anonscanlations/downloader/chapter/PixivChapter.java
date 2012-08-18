package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.jsoup.nodes.*;
import org.json.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author Administrator
 */
public class PixivChapter extends Chapter
{
    public static final Pattern IDMATCH = Pattern.compile("^\\/viewer\\/(?:magazines\\/\\d+\\/)?(issues|stories)\\/(\\d+)");
    
    private transient LoginDownloadJob login;
    private transient String tokenURL, token, ID, csrfToken, type;
    private transient ArrayList<String> images;
    
    public PixivChapter(URL _url)
    {
        this(_url, null, null);
    }
    public PixivChapter(URL _url, String _username, char[] _password)
    {
        super(_url, _username, _password);
        
        login = null;
        tokenURL = token = ID = csrfToken = type = null;
        images = new ArrayList<String>();
    }
    
    @Override
    public void getRequiredInfo(LoginManager s) throws Exception
    {
        if(username == null || password == null)
        {
            username = s.getPixivLogin().getEMail();
            password = s.getPixivLogin().getPassword();
        }
    }
    
    public ArrayList<DownloadJob> init() throws Exception
    {
        if(!url.toString().contains("comic.pixiv.net/viewer"))
            throw new Exception("Not a Pixiv URL (no \"comic.pixiv.net/viewer\")");
        String file = url.getPath();
        Matcher matcher = IDMATCH.matcher(file);
        if(!matcher.matches())
            throw new Exception("ID not found");
        type = matcher.group(1);
        ID = matcher.group(2);
        DownloaderUtils.debug("type: " + type);
        DownloaderUtils.debug("ID: " + ID);
        
        login = new LoginDownloadJob("Login to Pixiv", LoginDownloadJob.Type.PIXIV, username, password);
        
        final JSoupDownloadJob getPage = new JSoupDownloadJob("Get page", url)
        {
            @Override
            public void run() throws Exception
            {
                addRequestCookies(login.getResponseCookies());
                
                super.run();
                
                Document d = response.parse();
                tokenURL = d.select("meta[name=token-api-url]").attr("content");
                csrfToken = d.select("meta[name=csrf-token]").attr("content");
                DownloaderUtils.debug("csrfToken: " + csrfToken);
                DownloaderUtils.debug("tokenURL: " + tokenURL);
                DownloaderUtils.debug("responseCookies: " + response.cookies());
            }
        };
        
        JSoupDownloadJob getToken = new JSoupDownloadJob("Get token", null)
        {
            @Override
            public void run() throws Exception
            {
                addRequestCookies(login.getResponseCookies());
                addRequestCookies(getPage.getResponseCookies());
                addRequestProperty("Referer", PixivChapter.this.url.toString());
                addRequestProperty("X-CSRF-Token", csrfToken);
                addRequestProperty("X-Requested-With", "XMLHttpRequest");
                url = new URL("http://comic.pixiv.net" + tokenURL);
                postOverride = true;
                
                super.run();
                
                JSONObject obj = new JSONObject(response.body());
                JSONObject dataObj = obj.getJSONObject("data");
                token = dataObj.getString("token");
                DownloaderUtils.debug("token: " + token);
            }
        };
        
        JSoupDownloadJob json = new JSoupDownloadJob("Get JSON", null)
        {
            @Override
            public void run() throws Exception
            {
                addRequestCookies(login.getResponseCookies());
                addRequestCookies(getPage.getResponseCookies());
                addRequestProperty("Referer", PixivChapter.this.url.toString());
                addRequestProperty("X-CSRF-Token", csrfToken);
                addRequestProperty("X-Requested-With", "XMLHttpRequest");
                url = new URL("http://comic.pixiv.net/api/v1/viewer/" + type + "/" + token + "/" + ID + ".json");
                
                super.run();
                JSONObject obj = new JSONObject(response.body());
                JSONObject dataObj = obj.getJSONObject("data");
                title = dataObj.getString("title") + "_id-" + ID + "_" + dataObj.getString("sub_title");
                DownloaderUtils.debug("title: " + title);
                
                JSONArray contents = dataObj.getJSONArray("contents");
                for(int i = 0; i < contents.length(); i++)
                {
                    JSONArray pages = contents.getJSONObject(i).getJSONArray("pages");
                    for(int j = 0; j < pages.length(); j++)
                    {
                        JSONObject page = pages.getJSONObject(j);
                        if(page.has("right"))
                            images.add(page.getJSONObject("right").getJSONObject("data").getString("url"));
                        if(page.has("left"))
                            images.add(page.getJSONObject("left").getJSONObject("data").getString("url"));
                    }
                }
            }
        };
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(login);
        list.add(getPage);
        list.add(getToken);
        list.add(json);
        return(list);
    }
    
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        
        int i = 1;
        for(String image : images)
        {
            File f = DownloaderUtils.fileName(directory, i, "jpg");
            if(f.exists())
            {
                i++;
                continue;
            }
            FileDownloadJob page = new FileDownloadJob(DownloaderUtils.pageOutOf(i, 1, images.size()), new URL(image), f);
            page.addRequestProperty("Referer", url.toString());
            page.addRequestCookies(login.getResponseCookies());
            list.add(page);
            i++;
        }
        
        return(list);
    }
}
