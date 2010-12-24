/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui.field;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author /a/non
 */
public abstract class PrefixedDisplayField extends DisplayField
{
    protected String prefix;

    public PrefixedDisplayField(String myKey, String myLabel, boolean editable, String myPrefix)
    {
        super(myKey, myLabel, editable);

        prefix = myPrefix;
    }

    @Override
    public JTextField addField(JPanel fieldPanel, Object value)
    {
        JPanel fieldLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel fieldLabel = new JLabel(prefix);
        fieldLabelPanel.add(fieldLabel);

        JTextField field = super.addField(fieldLabelPanel, value);

        fieldLabelPanel.setMaximumSize(new Dimension(10000000,
                                fieldLabelPanel.getPreferredSize().height));
        fieldLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldLabelPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        fieldPanel.add(fieldLabelPanel);

        return(field);
    }
}
