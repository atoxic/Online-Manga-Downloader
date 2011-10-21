package anonscanlations.downloader.chapter;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of decryptFile method, of class PCViewerDecrypt.
     */
    @Test
    public void testDecrypt() throws Exception
    {
        byte[] b = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        System.out.println("1: " + java.util.Arrays.toString(b));
        PCViewerDecrypt.decrypt(b);
        System.out.println("2: " + java.util.Arrays.toString(b));
        PCViewerDecrypt.encrypt(b);
        System.out.println("3: " + java.util.Arrays.toString(b));

        PCViewerDecrypt.encryptFile(new File("blood-mikotoka01-0001_000.jpg"), new File("dec.txt"));
    }
}