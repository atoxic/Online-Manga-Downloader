package anonscanlations.downloader;

import java.util.*;
import javax.swing.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class LoginManager
{
    private static final String NICO_KEY = "NICO", KEY_KEY = "KEY", RUSH_KEY = "RUSH";

    private NicoLoginDialog         nico;
    private KeyURLDialog            key;
    private ComicRushLoginDialog    rush;
    private Map<String, Boolean>    checked;

    public LoginManager()
    {
        checked = new HashMap<String, Boolean>();
        nico = new NicoLoginDialog();
        key = new KeyURLDialog();
        rush = new ComicRushLoginDialog();
    }
    public NicoLoginDialog getNicoLogin()
    {
        return(nico);
    }
    public KeyURLDialog getKey()
    {
        return(key);
    }
    public ComicRushLoginDialog getComicRushLogin()
    {
        return(rush);
    }
    private boolean showed(String _key)
    {
        return(checked.containsKey(_key) && checked.get(_key));
    }

    public JFrame showDialog(Chapter chapter)
    {
        // Trying Nico; need login (only once)
        if((chapter instanceof NicoNicoChapter
            || chapter instanceof NicoNicoEBooksChapter)
            && !showed(NICO_KEY))
        {
            checked.put(NICO_KEY, true);
            nico.setVisible(true);
            return(nico);
        }
        else if(chapter instanceof CLIPChapter
                && !showed(RUSH_KEY))
        {
            checked.put(RUSH_KEY, true);
            rush.setVisible(true);
            return(rush);
        }
        else if(chapter instanceof SundayChapter
                && !showed(KEY_KEY))
        {
            checked.put(KEY_KEY, true);
            key.setVisible(true);
            return(key);
        }
        return(null);
    }
}
