/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.mangaonweb;

import java.util.*;
import java.io.*;
import java.util.regex.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebSite
{
    public static final MangaOnWebSite SITE = new MangaOnWebSite();
    private MangaOnWebSite()
    {
    }

    public String getName()
    {
        return("Manga On Web");
    }

    public Collection<Magazine> getMagazines()
            throws IOException
    {
        ArrayList<Magazine> mags = new ArrayList<Magazine>();
        SimpleMagazine mag = new SimpleMagazine("漫画 on Web");
        mags.add(mag);

        for(int i = 0; ; i++)
        {
            String page = DownloaderUtils.getPage("http://mangaonweb.com/siteSearch.do?type=0&sortType=0&freeContents=on", "UTF-8");

            // no next
            if(page.indexOf("cimages/design/00_common/moji/next_line.png") == -1)
                break;
        }

        return(mags);
    }
}
