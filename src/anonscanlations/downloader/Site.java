/*
 * Coded by /a/non, for /a/no
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public abstract class Site
{
    public abstract String getName();
    public abstract Collection<Magazine> getMagazines()
            throws IOException;
}
