/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import anonscanlations.downloader.*;

import java.io.*;

/**
 *
 * @author /a/non
 */
public class DummyDownloadListener extends DownloadListener
{
    public DummyDownloadListener()
    {
        super(DownloadDirectory.makeDirectory(
                PreferencesManager.PREFS.get(
                    PreferencesManager.KEY_DOWNLOADDIR,
                        "./downloads/")));
    }

    public void downloadIncrement(Chapter c)
    {
        DownloaderUtils.debug("downloadProgressed: chapter " + c);
    }
    public void downloadFinished(Chapter c)
    {
        DownloaderUtils.debug("downloadFinished: chapter " + c);
    }
    public void setTotal(int total)
    {
        DownloaderUtils.debug("setTotal:"  + total);
    }

    @Override
    public synchronized String downloadPath(Chapter c, int i) throws IOException
    {
        String saveTitle = "test_c" + c.getTitle()
                            + "_" + String.format("%03d", i) + ".jpg";
        saveTitle = saveTitle.replace(' ', '_');
        return((new File(directory.getFile(), saveTitle)).getAbsolutePath());
    }
}
