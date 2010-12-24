/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui.field;

import java.awt.*;
import javax.swing.*;

import javax.swing.text.*;
import java.text.*;

/**
 *
 * @author /a/non
 */
public class ReComicDisplayField extends PrefixedDisplayField
{
    public ReComicDisplayField(String myKey, String myLabel, boolean editable)
    {
        super(myKey, myLabel, editable, "http://www.recomic.jp/");
    }

    protected JFormattedTextField.AbstractFormatter getFormatter()
    {
        try
        {
            MaskFormatter formatter = new MaskFormatter("UUU-U#U########");
            formatter.setValidCharacters("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-");
            return(formatter);
        }
        catch(ParseException e)
        {

        }
        return(null);
    }

    @Override
    public String getLabel(Object value)
    {
        if(value == null || value.equals(""))
            return(label);
        else
            return("<html><a href=\"http://www.recomic.jp/"
                    + value + "\">ReComic</a>");
    }

    @Override
    protected void setupField(JTextField field)
    {
        super.setupField(field);
        if(field instanceof JFormattedTextField)
            ((JFormattedTextField)field).setColumns(15);
    }

    @Override
    public String getValue(JTextField field)
    {
        String value = super.getValue(field);

        if(value.equals("-"))
            return("");
        else
            return(value);
    }
}
