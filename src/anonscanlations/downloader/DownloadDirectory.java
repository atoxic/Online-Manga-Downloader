/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

import java.io.*;

/**
 *
 * @author Administrator
 */
public class DownloadDirectory
{
    private File file;
    private DownloadDirectory(File myFile)
    {
        file = myFile;
    }
    public File getFile(){ return(file); }
    public static DownloadDirectory makeDirectory(String path)
    {
        File file = new File(path);
        if(file.isDirectory() || !file.exists())
        {
            if(!file.exists())
                file.mkdirs();
            return(new DownloadDirectory(file));
        }
        return(null);
    }
}
