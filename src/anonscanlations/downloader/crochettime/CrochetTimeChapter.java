/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.crochettime;

import java.io.*;
import java.util.*;
import java.net.*;

import anonscanlations.downloader.*;

// the images are zlib-comrpessed then encrypted
import com.jcraft.jzlib.*;

/**
 *
 * @author /a/non
 */
public class CrochetTimeChapter extends Chapter
{
    private String path, title;
    private int total;

    public CrochetTimeChapter()
    {
    }

    public CrochetTimeChapter(String path, String title)
    {
        this.path = path;
        this.title = title;
    }

    public boolean init() throws Exception
    {
        File temp = File.createTempFile("crochettime_temp_", ".bin");
        temp.deleteOnExit();
        ArrayList<String> list = getList(temp);
        if(list == null)
            return(false);
        total = list.size();
        temp.delete();

        return(true);
    }

    public String getTitle()
    {
        return(title);
    }
    public int getTotal()
    {
        return(total);
    }

    public ArrayList<String> getList(File temp) throws Exception
    {
        ((StringBuilder)CrochetTimeDecrypt.FORMATTER.out()).setLength(0);
        CrochetTimeDecrypt.FORMATTER.format("%08x", (int)(32767 * Math.random()));
        String filepath = "/home/dotbook/rs2_contents/voyager-store_contents/" + path + "/";
        String filelistURL = CrochetTimeDecrypt.scrambleURL(filepath + path + "_pc_image_crochet.book.bmit&B"
                                                        + CrochetTimeDecrypt.FORMATTER.out().toString());
        boolean listExists = DownloaderUtils.downloadFile(new URL("http://shangrila.voyager-store.com/dBmd?" + filelistURL),
                                    temp.getAbsolutePath());
        if(!listExists)
            return(null);

        ArrayList<String> filelist = CrochetTimeDecrypt.fileList(temp);
        if(filelist.isEmpty())
            return(null);
        return(filelist);
    }

    public boolean download(DownloadListener dl) throws Exception
    {
        // for the file list and downloaded files
        File temp = File.createTempFile("crochettime_temp_", ".bin"),
        // for the decrypt files
                tempOut = File.createTempFile("crochettime_tempout_", ".bin");
        temp.deleteOnExit();
        tempOut.deleteOnExit();

        // 1) get file list
        DownloaderUtils.debug("===PART 1===");
        ArrayList<String> filelist = getList(temp);
        if(filelist == null)
            return(false);
        String filepath = "/home/dotbook/rs2_contents/voyager-store_contents/" + path + "/";

        // 3) get files
        DownloaderUtils.debug("===PART 2===");
        for(int i = 0; i < filelist.size(); i++)
        {
            if(dl.isDownloadAborted())
                return(true);
            DownloaderUtils.debug("page " + i + ": " + filepath + filelist.get(i));
            String fileURL = CrochetTimeDecrypt.scrambleURL(filepath + filelist.get(i));

            boolean fileExists = DownloaderUtils.downloadFile(new URL("http://shangrila.voyager-store.com/dBmd?" + fileURL),
                                    temp.getAbsolutePath());
            if(!fileExists)
                return(false);

            CrochetTimeDecrypt.decryptFile(temp, tempOut);

            BufferedInputStream input = new BufferedInputStream(new ZInputStream(new FileInputStream(tempOut)));
            FileOutputStream output = new FileOutputStream(dl.downloadPath(this, i));
            byte[] buf = new byte[1024];
            while(input.read(buf) != -1)
            {
                output.write(buf);
            }

            input.close();
            output.close();

            dl.downloadIncrement(this);
        }

        dl.downloadFinished(this);

        return(true);
    }
}
