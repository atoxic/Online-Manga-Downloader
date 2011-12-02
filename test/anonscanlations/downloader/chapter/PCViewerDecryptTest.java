package anonscanlations.downloader.chapter;

import anonscanlations.downloader.chapter.crypto.PCViewerDecrypt;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PCViewerDecryptTest
{
    public PCViewerDecryptTest()
    {
    }

    /**
     * Test of decryptFile method, of class PCViewerDecrypt.
     */
    @Test
    public void testDecrypt() throws Exception
    {
        final byte[] encrypted = new byte[]{16, 17, 19, 16, 20, 17, 23, 16, 24, 17, 27, 16, 13, 14, 15, 16},
                    decrypted = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

        byte[] b = Arrays.copyOf(decrypted, decrypted.length);
        PCViewerDecrypt.encrypt(b);
        assertTrue(Arrays.equals(b, encrypted));
        PCViewerDecrypt.decrypt(b);
        assertTrue(Arrays.equals(b, decrypted));
    }
}