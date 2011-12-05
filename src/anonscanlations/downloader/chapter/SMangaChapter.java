package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.image.*;
import javax.imageio.*;

import com.flagstone.transform.*;
import com.flagstone.transform.image.*;
import com.flagstone.transform.util.image.*;
import com.flagstone.transform.movieclip.*;
import com.flagstone.transform.text.*;
import com.flagstone.transform.font.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class SMangaChapter extends Chapter
{
    private URL url;
    private String title;
    public SMangaChapter(URL _url)
    {
        url = _url;
        title = null;
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        // TODO what about proxies?
        if(!url.getHost().contains("s-manga") || !url.toString().endsWith(".html"))
            throw new Exception("Malformed URL");
        // Do nothing
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        return(list);
    }
    
    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        final File finalDirectory = directory;
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        URL swf = new URL(url.toString().substring(0, url.toString().length() - 5) + ".swf");
        list.add(new ByteArrayDownloadJob("All Pages", swf)
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                ByteArrayInputStream bais = null;
                Movie m = null;
                try
                {
                    bais = new ByteArrayInputStream(getBytes());
                    m = new Movie();
                    m.decodeFromStream(bais);
                }
                finally
                {
                    if(bais != null)
                        bais.close();
                }
                getTitle(m);
                saveImages(m, finalDirectory);
            }
        });
        return(list);
    }
    
    private ArrayList<Integer> getTitleIDs(Movie m) throws Exception
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for(MovieTag tag : m.getObjects())
        {
            if(tag instanceof DefineMovieClip)
            {
                for(MovieTag tag2 : ((DefineMovieClip)tag).getObjects())
                {
                    if(tag2 instanceof Place2)
                    {
                        Place2 p = (Place2)tag2;
                        if(p.getTransform() != null &&
                                Math.abs(p.getTransform().getTranslateY() + 2720) <= 40)
                            ret.add(p.getIdentifier());
                    }
                }
            }
        }
        return(ret);
    }
    
    private void getTitle(Movie m) throws Exception
    {
        int fontID = -1;
        ArrayList<TextSpan> titleSpans = new ArrayList<TextSpan>();
        ArrayList<Integer> titleIDs = getTitleIDs(m);
        for(MovieTag tag : m.getObjects())
        {
            if(tag instanceof DefineText && titleIDs.contains(((DefineText)tag).getIdentifier()))
            {
                DownloaderUtils.debug("tag: " + tag);
                for(TextSpan span : ((DefineText)tag).getSpans())
                {
                    DownloaderUtils.debug("\t\tspan: " + span);
                    if(span.getIdentifier() != null)
                        fontID = span.getIdentifier();
                    titleSpans.add(span);
                }
            }
        }
        java.util.List<Integer> fontCodes = null;
        for(MovieTag tag : m.getObjects())
        {
            if(tag instanceof DefineFont3 && ((DefineFont3)tag).getIdentifier() == fontID)
                fontCodes = ((DefineFont3)tag).getCodes();
            else if(tag instanceof DefineFont2 && ((DefineFont2)tag).getIdentifier() == fontID)
                fontCodes = ((DefineFont2)tag).getCodes();
            if(fontCodes != null)
            {
                ArrayList<Integer> titleCodes = new ArrayList<Integer>();
                for(TextSpan titleSpan : titleSpans)
                    for(GlyphIndex i : titleSpan.getCharacters())
                        titleCodes.add(fontCodes.get(i.getGlyphIndex()));
                int[] titleCodeArray = new int[titleCodes.size()];
                for(int i = 0; i < titleCodes.size(); i++)
                    titleCodeArray[i] = titleCodes.get(i);
                title = new String(titleCodeArray, 0, titleCodeArray.length);

                DownloaderUtils.debug("title: " + title);
                return;
            }
        }
    }
    
    private void saveImages(Movie m, File directory) throws Exception
    {
        int i = 1, j;
        byte[] bytes;
        String ext;
        for(MovieTag tag : m.getObjects())
        {
            bytes = null;
            ext = null;
            if(tag instanceof DefineJPEGImage2)
            {
                byte[] image = ((DefineJPEGImage2)tag).getImage();
                // look for SOI
                for(j = 0; j < image.length - 4; j++)
                {
                    if(image[j] == (byte)0xff
                            && image[j + 1] == (byte)0xd8
                            && image[j + 2] == (byte)0xff 
                            && image[j + 3] == (byte)0xe0)
                        break;
                }
                if(j == image.length - 4)
                    continue;
                bytes = new byte[image.length - j];
                System.arraycopy(image, j, bytes, 0, bytes.length);
                ext = "jpeg";
            }
            else if(tag instanceof DefineImage)
            {
                BufferedImageEncoder enc = new BufferedImageEncoder();
                enc.setImage((DefineImage)tag);
                BufferedImage image = enc.getBufferedImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", baos);
                bytes = baos.toByteArray();
                ext = "png";
            }
            if(bytes != null)
            {
                final File f = DownloaderUtils.fileName(directory, title, i, ext);
                if(!f.exists())
                    DownloaderUtils.safeWrite(bytes, f);
                i++;
            }
        }
    }
}
