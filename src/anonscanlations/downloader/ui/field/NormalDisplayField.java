/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui.field;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author /a/non
 */
public class NormalDisplayField extends DisplayField
{
    public NormalDisplayField(String myKey, String myLabel, boolean editable)
    {
        super(myKey, myLabel, editable);
    }

    protected JFormattedTextField.AbstractFormatter getFormatter()
    {
        return(null);
    }
}
