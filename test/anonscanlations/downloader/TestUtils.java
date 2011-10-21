package anonscanlations.downloader;

import java.io.*;
import java.util.zip.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class TestUtils
{
    // http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
    public static File createTempDirectory() throws IOException
    {
        final File temp;
        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!(temp.delete()))
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        if(!(temp.mkdir()))
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        return(temp);
    }

    public static long doChecksum(File file) throws Exception
    {
        CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());
        long fileSize = file.length();

        byte[] buf = new byte[128];
        while(cis.read(buf) >= 0);

        long checksum = cis.getChecksum().getValue();
        cis.close();
        return(checksum);
    }
}
