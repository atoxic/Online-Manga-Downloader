package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.chapter.crypto.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PCViewerDownloadJob extends ByteArrayDownloadJob
{
    protected File file;
    public PCViewerDownloadJob(String _description, URL _url, File _file)
    {
        super(_description, _url);
        file = _file;
    }
    @Override
    public void run() throws Exception
    {
        super.run();
        PCViewerDecrypt.decrypt(bytes);
        DownloaderUtils.safeWrite(bytes, 8, bytes.length - 8, file);
    }
}
