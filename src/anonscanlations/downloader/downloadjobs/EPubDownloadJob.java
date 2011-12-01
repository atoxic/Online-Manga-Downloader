package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;
import java.util.zip.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public abstract class EPubDownloadJob extends JSoupDownloadJob
{
    protected HttpURLConnection conn;
    public EPubDownloadJob(String _desc, URL _url)
    {
        super(_desc, _url);
        conn = null;
    }

    @Override
    public void run() throws Exception
    {
        super.run();

        byte[] bytes = response.bodyAsBytes();

        ByteArrayInputStream byte_input = new ByteArrayInputStream(bytes);
        doByteInput(byte_input);
        byte_input.close();
        ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry e;
        
        DownloaderUtils.debug("Spot 2");
        
        while((e = input.getNextEntry()) != null)
        {
            doZipEntryInput(input, e);
            input.closeEntry();
        }
        
        DownloaderUtils.debug("Spot 5");
        input.close();
    }

    public abstract void doByteInput(ByteArrayInputStream byte_input) throws Exception;

    public abstract void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception;
}
