package anonscanlations.downloader.pcviewer;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PCViewerDownloadJob extends FileDownloadJob
{
    protected File realFile;
    public PCViewerDownloadJob(String _description, URL _url, File _file)
    {
        this(_description, _url, _file, null);
    }
    public PCViewerDownloadJob(String _description, URL _url, File _file, String _cookies)
    {
        super(_description, _url, null, _cookies);
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
