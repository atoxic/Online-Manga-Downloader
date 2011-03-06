/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

/**
 *
 * @author /a/non
 */
public class SimpleSeries extends Series
{
     private String title;

    public SimpleSeries()
    {
    }

    public SimpleSeries(String myTitle)
    {
        title = myTitle;
    }

    public String getOriginalTitle()
    {
        return(title);
    }
}
