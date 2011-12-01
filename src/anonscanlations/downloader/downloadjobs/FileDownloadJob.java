package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class FileDownloadJob extends ByteArrayDownloadJob
{
    protected File file;
    public FileDownloadJob(String _description, URL _url, File _file)
    {
        super(_description, _url);
        file = _file;
    }
    @Override
    public void run() throws Exception
    {
        super.run();

        DownloaderUtils.safeWrite(bytes, file);
    }
}

