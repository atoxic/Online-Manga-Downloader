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
public class ReComicDisplayField extends PrefixedDisplayField
{
    public ReComicDisplayField(String myKey, String myLabel, boolean editable)
    {
        super(myKey, myLabel, editable, "http://www.recomic.jp/");
    }

    protected JFormattedTextField.AbstractFormatter getFormatter()
    {
        return(new ReComicFormatter());
        /*
        try
        {
            MaskFormatter formatter = new MaskFormatter("UUU-U#U########");
            formatter.setValidCharacters("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-");
            return(formatter);
        }
        catch(ParseException e)
        {
            DownloaderUtils.error("Couldn't parse MaskFormatter", e, false);
        }
        return(null);
        //*/
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

class ReComicFormatter extends JFormattedTextField.AbstractFormatter
{
    private boolean isReComicID(String text)
    {
        if(!text.startsWith("PGN-A3W"))
            return(false);

        try
        {
            Integer.parseInt(text.substring(7));
        }
        catch(NumberFormatException nfe)
        {
            return(false);
        }

        return(true);
    }

    @Override
    protected DocumentFilter getDocumentFilter()
    {
        return(new DocumentFilter()
        {
            private String getText(DocumentFilter.FilterBypass fb)
                                    throws BadLocationException
            {
                return(fb.getDocument().getText(0, fb.getDocument().getLength()));
            }

            @Override
            public void remove(DocumentFilter.FilterBypass fb,
                                int offset, int length)
                                    throws BadLocationException
            {
                String result = getText(fb);
                result = result.substring(0, offset) + result.substring(offset + length);
                if(isReComicID(result))
                    fb.remove(offset, length);
            }

            @Override
            public void insertString(DocumentFilter.FilterBypass fb,
                                int offset, String string, AttributeSet attr)
                                    throws BadLocationException
            {
                String result = getText(fb);
                result = result.substring(0, offset) + string + result.substring(offset);
                if(isReComicID(result))
                    fb.insertString(offset, string, attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb,
                                int offset, int length,
                                String text, AttributeSet attrs)
                                    throws BadLocationException
            {
                String result = getText(fb);
                result = result.substring(0, offset) + text + result.substring(offset + length);
                if(isReComicID(result))
                    fb.replace(offset, length, text, attrs);
            }
        });
    }

    public Object stringToValue(String text)
    {
        if(!isReComicID(text))
            return(null);
        return(text);
    }
    public String valueToString(Object obj)
    {
        if(!(obj instanceof String))
            return(null);
        String text = (String)obj;
        if(!isReComicID(text))
            return(null);
        return(text);
    }
}