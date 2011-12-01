package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PCViewerDownloadJob extends JSoupDownloadJob
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
        byte[] bytes = response.bodyAsBytes();
        PCViewerDecrypt.decrypt(bytes);
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes, 8, bytes.length - 8);
        out.close();
    }
}
