/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class DownloaderUtilsTest {

    public DownloaderUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of unescapeHTML method, of class DownloaderUtils.
     * TODO: improve?
     */
    @Test
    public void testUnescapeHTML() {
        System.out.println("unescapeHTML");
        String source = "&amp;&lt;";
        String expResult = "&<";
        String result = DownloaderUtils.unescapeHTML(source);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRedirectURL method, of class DownloaderUtils.
     */
    @Test
    public void testGetRedirectURL() throws Exception {
        System.out.println("getRedirectURL");
        URL url = new URL("http://www.example.com");
        URL expResult = new URL("http://www.iana.org/domains/example/");
        URL result = DownloaderUtils.getRedirectURL(url);
        assertEquals(expResult, result);
    }
}