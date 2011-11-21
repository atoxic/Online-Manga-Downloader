package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PCViewerDownloadJob extends FileDownloadJob
{
    protected File realFile;
    public PCViewerDownloadJob(String _description, URL _url, File _file)
    {
        super(_description, _url, null);
        realFile = _file;
    }
    @Override
    public void run() throws Exception
    {
        File temp = File.createTempFile("pcviewer_temp", ".bin");
        file = temp;
        super.run();
        PCViewerDecrypt.decryptFile(temp, realFile);
        temp.delete();
    }
}
