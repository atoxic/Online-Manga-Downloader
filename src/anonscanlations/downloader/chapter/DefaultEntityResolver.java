package anonscanlations.downloader.chapter;

import java.io.*;
import org.xml.sax.*;
import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class DefaultEntityResolver implements EntityResolver
{
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {
        return(new InputSource(new StringReader("")));
    }
}
