package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import javax.swing.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class LoginManager
{
    private static final String NICO_KEY = "NICO", PIXIV_KEY = "PIXIV", KEY_KEY = "KEY";
    private static final String NICO_LABEL = "<html>Is it from NicoNico?<br/>"
                                        + "If so, please input your NicoNico login information and press \"OK\" in order to authenticate.<br/>"
                                        + "If not, press \"Cancel.\"</html>";
    private static final String PIXIV_LABEL = "<html>Is it from Pixiv?<br/>"
                                        + "If so, please input your Pixiv login information and press \"OK\" in order to authenticate.<br/>"
                                        + "If not, press \"Cancel.\"</html>";

    private LoginDialog             nico, pixiv;
    private KeyURLDialog            key;
    private Map<String, Boolean>    checked;

    public LoginManager()
    {
        checked = new HashMap<String, Boolean>();
        nico = new LoginDialog("NicoNico Handler: Nico Login Information Needed",
                            NICO_LABEL);
        pixiv = new LoginDialog("Pixiv Handler: Pixiv Login Information Needed",
                            PIXIV_LABEL);
        key = new KeyURLDialog();
    }
    public LoginDialog getNicoLogin()
    {
        return(nico);
    }
    public LoginDialog getPixivLogin()
    {
        return(pixiv);
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
            nico.setEMail(PasswordManager.nicoUsername);
            nico.setPassword(PasswordManager.nicoPassword);
            nico.setVisible(true);
            return(nico);
        }
        else if(chapter instanceof PixivChapter && !showed(PIXIV_KEY))
        {
            checked.put(PIXIV_KEY, true);
            pixiv.setEMail(PasswordManager.pixivUsername);
            pixiv.setPassword(PasswordManager.pixivPassword);
            pixiv.setVisible(true);
            return(pixiv);
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
