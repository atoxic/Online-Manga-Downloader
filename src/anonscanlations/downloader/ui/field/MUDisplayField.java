/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui.field;

import java.awt.*;
import javax.swing.*;

import javax.swing.text.*;
import java.text.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class MUDisplayField extends PrefixedDisplayField
{
    public MUDisplayField(String myKey, String myLabel, boolean editable)
    {
        super(myKey, myLabel, editable, "http://www.mangaupdates.com/series.html?id=");
    }

    protected JFormattedTextField.AbstractFormatter getFormatter()
    {
        try
        {
            MaskFormatter formatter = new MaskFormatter("#######");
            return(formatter);
        }
        catch(ParseException e)
        {
            DownloaderUtils.error("Couldn't parse MaskFormatter", e, false);
        }
        return(null);
    }
    
    @Override
    public String getLabel(Object value)
    {
        if(value == null || value.equals(""))
            return(label);
        else
            return("<html><a href=\"http://www.mangaupdates.com/series.html?id="
                    + value + "\">" + label + "</a>");
    }

    @Override
    protected void setupField(JTextField field)
    {
        super.setupField(field);
        if(field instanceof JFormattedTextField)
            ((JFormattedTextField)field).setColumns(7);
    }
}
