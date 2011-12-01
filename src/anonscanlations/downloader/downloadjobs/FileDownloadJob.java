package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class FileDownloadJob extends JSoupDownloadJob
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

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(response.bodyAsBytes());
        fos.close();
    }
}

