/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui.field;

import anonscanlations.downloader.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

/**
 *
 * @author /a/non
 */
public abstract class DisplayField
{
    protected boolean editable;
    protected String key, label;
    public DisplayField(String myKey, String myLabel, boolean editable)
    {
        key = myKey;
        label = myLabel;
        this.editable = editable;
    }
    public String getKey()
    {
        return(key);
    }

    protected abstract JFormattedTextField.AbstractFormatter getFormatter();

    protected static void browse(String url)
    {
        Desktop desktop = Desktop.getDesktop();

        if(desktop.isSupported(java.awt.Desktop.Action.BROWSE))
        {
            try
            {
                URI uri = new URI(url);
                desktop.browse(uri);
            }
            catch(Exception e)
            {
                DownloaderUtils.errorGUI("couldn't browse to page: " + url, false);
            }
        }
    }

    public String getLabel(Object value){ return(label); }

    public void addLabel(JPanel fieldPanel, Object value)
    {
        JEditorPane labelPane = new JEditorPane("text/html", getLabel(value));
        labelPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelPane.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        labelPane.setEditable(false);
        labelPane.setOpaque(false);
        labelPane.setMaximumSize(new Dimension(10000000, labelPane.getPreferredSize().height));
        if(getLabel(value).startsWith("<html>"))
        {
            labelPane.addHyperlinkListener(new HyperlinkListener()
            {
                public void hyperlinkUpdate(HyperlinkEvent hle)
                {
                    if(HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()))
                    {
                        browse(hle.getURL().toString());
                    }
                }
            });
        }
        fieldPanel.add(labelPane);
    }

    public JTextField addField(JPanel fieldPanel, Object value)
    {
        JFormattedTextField.AbstractFormatter formatter = getFormatter();
        JTextField field;
        if(formatter != null)
        {
            field = new JFormattedTextField(formatter);
            ((JFormattedTextField)field).setValue(value);
        }
        else
        {
            field = value == null ? new JTextField() : new JTextField(value.toString());
        }
        setupField(field);
        fieldPanel.add(field);

        return(field);
    }

    public String getValue(JTextField field)
    {
        if(field instanceof JFormattedTextField)
        {
            Object obj = ((JFormattedTextField)field).getValue();
            return(obj == null ? "" : obj.toString().trim());
        }
        else
        {
            return(field.getText().trim());
        }
    }

    protected void setupField(JTextField field)
    {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        field.setCaretPosition(0);
        field.setMaximumSize(new Dimension(10000000, field.getPreferredSize().height));
        field.setEditable(editable);
    }
}
