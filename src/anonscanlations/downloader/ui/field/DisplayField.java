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

    public String getLabel(Object value){ return(label); }

    public void addLabel(JPanel fieldPanel, Object value)
    {
        fieldPanel.add(DownloaderUtils.makeHyperlinkLabel(getLabel(value)));
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
