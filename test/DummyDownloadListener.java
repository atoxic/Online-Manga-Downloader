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

    public void downloadProgressed(Chapter c, int page)
    {
        DownloaderUtils.debug("downloadProgressed: chapter " + c + ", page " + page);
    }
    public void downloadFinished(Chapter c)
    {
        DownloaderUtils.debug("downloadFinished: chapter " + c);
    }
    public void setDownloadRange(int min, int max)
    {
        DownloaderUtils.debug("setDownloadRange: min " + min + ", max " + max);
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
