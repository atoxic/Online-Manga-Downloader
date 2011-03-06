/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

/**
 *
 * @author /a/non
 */
public class SimpleMagazine extends Magazine
{
    private String title;

    public SimpleMagazine()
    {

    }

    public SimpleMagazine(String title)
    {
        this.title = title;
    }
    public String getOriginalTitle()
    {
        return(title);
    }
}
