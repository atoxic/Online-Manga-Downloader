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
    private static final String NICO_KEY = "NICO", KEY_KEY = "KEY";
    private static final String NICO_LABEL = "<html>Is it from NicoNico Seiga?<br/>"
                                        + "If so, please input your NicoNico login information and press \"OK\" in order to authenticate.<br/>"
                                        + "If not, press \"Cancel.\"</html>";

    private LoginDialog             nico;
    private KeyURLDialog            key;
    private Map<String, Boolean>    checked;

    public LoginManager()
    {
        checked = new HashMap<String, Boolean>();
        nico = new LoginDialog("NicoNico Handler: Nico Login Information Needed",
                            NICO_LABEL);
        key = new KeyURLDialog();
    }
    public LoginDialog getNicoLogin()
    {
        return(nico);
    }
    public KeyURLDialog getKey()
    {
        return(key);
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
