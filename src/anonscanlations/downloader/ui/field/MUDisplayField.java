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
        return(new MUFormatter());
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

class MUFormatter extends JFormattedTextField.AbstractFormatter
{
    @Override
    protected DocumentFilter getDocumentFilter()
    {
        return(new DocumentFilter()
        {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb,
                                int offset, String string, AttributeSet attr)
                                    throws BadLocationException
            {
                String onlyNums = string.replaceAll("[^0-9]", "");
                fb.insertString(offset, onlyNums, attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, 
                                int offset, int length,
                                String text, AttributeSet attrs)
                                    throws BadLocationException
            {
                String onlyNums = text.replaceAll("[^0-9]", "");
                fb.replace(offset, length, onlyNums, attrs);
            }
        });
    }

    public Object stringToValue(String text)
    {
        try
        {
            Number num = Integer.parseInt(text);
            return(num);
        }
        catch(NumberFormatException nfe)
        {
            return(null);
        }
    }
    public String valueToString(Object obj)
    {
        if(obj instanceof Number)
            return(obj.toString());
        if(obj instanceof String)
        {
            try
            {
                Number num = Integer.parseInt((String)obj);
                return((String)obj);
            }
            catch(NumberFormatException nfe)
            {
            }
        }
        return(null);
    }
}
