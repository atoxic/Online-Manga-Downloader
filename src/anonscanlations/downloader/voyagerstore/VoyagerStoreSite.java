/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.voyagerstore;

import java.util.*;
import java.io.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class VoyagerStoreSite extends Site
{
    public String getName()
    {
        return("Voyager Store");
    }
    public Collection<Magazine> getMagazines()
            throws IOException
    {
        ArrayList<Magazine> mags = new ArrayList<Magazine>();
        SimpleMagazine mag = new SimpleMagazine("Voyager Store");

        return(mags);
    }
}
