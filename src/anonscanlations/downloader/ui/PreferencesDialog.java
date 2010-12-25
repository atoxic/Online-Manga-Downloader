/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui;

import anonscanlations.downloader.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public class PreferencesDialog extends JDialog
{
    private DownloaderWindow window;
    private JPanel content;

    private JCheckBox autoUpdate, submit;

    public PreferencesDialog(DownloaderWindow myWindow)
    {
        super(myWindow, "Preferences", true);

        window = myWindow;

        WindowPrefsSaver.add("prefsDialog", this, false);

        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        setupUI();
        pack();
    }

    private void setupUI()
    {
        autoUpdate = new JCheckBox("Check server automatically on start",
                PreferencesManager.PREFS.getBoolean(PreferencesManager.KEY_SERVERCHECK, false));
        content.add(autoUpdate);
        submit = new JCheckBox("Enable submitting edited data on save (IP GETS LOGGED!)",
                PreferencesManager.PREFS.getBoolean(PreferencesManager.KEY_SUBMIT, false));
        content.add(submit);

        JPanel choice = new JPanel();
        choice.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton ok = new JButton("Save");
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                DownloaderUtils.debug("ok");

                PreferencesManager.PREFS.putBoolean(PreferencesManager.KEY_SERVERCHECK,
                                                autoUpdate.isSelected());
                PreferencesManager.PREFS.putBoolean(PreferencesManager.KEY_SUBMIT,
                                                submit.isSelected());

                // change button state for "Save" or "Save and Submit"

                window.info.refereshButtonState();

                PreferencesDialog.this.dispose();
            }
        });
        choice.add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                DownloaderUtils.debug("cancel");
                PreferencesDialog.this.dispose();
            }
        });
        choice.add(cancel);

        content.add(choice);
    }
}
