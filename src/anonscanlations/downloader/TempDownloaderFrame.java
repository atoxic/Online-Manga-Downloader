package anonscanlations.downloader;

import anonscanlations.downloader.chapter.Chapter;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class TempDownloaderFrame extends javax.swing.JFrame {

    private ExamplesFrame examples;
    private LogFrame log;

    /** Creates new form TempDownloaderFrame */
    public TempDownloaderFrame() {
        // lazy init
        examples = null;

        log = new LogFrame();
        DownloaderUtils.LOGEDITOR = log.getEditor();

        PreferencesManager.registerWindow("OMD0.1.0_tempdlframe", this, false);

        initComponents();
    }

    public void setStatus(String status)
    {
        statusBar.setText("Status: " + status);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        radioButtonGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        downloadDirectoryField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        nicoRadioButton = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        clubSundayRadioButton = new javax.swing.JRadioButton();
        clubSundayKeyURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        nicoAceRadioButton = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        emailField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jRadioButton9 = new javax.swing.JRadioButton();
        downloadButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        viewerURLField = new javax.swing.JTextField();
        statusBar = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        manual = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Online Manga Downloader 0.1.5.4 Return of the Bug Fixes");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                TempDownloaderFrame.this.windowClosing(evt);
            }
        });

        jLabel1.setText("Download Directory");

        downloadDirectoryField.setText(PreferencesManager.PREFS.get(PreferencesManager.KEY_DOWNLOADDIR, new java.io.File(".").getAbsoluteFile().getParent()));
        downloadDirectoryField.setName("Directory"); // NOI18N

        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Viewer URL");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Type"));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText(Chapters.ACTIBOOK.getName());
        jRadioButton1.setActionCommand("ACTIBOOK");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        radioButtonGroup.add(jRadioButton2);
        jRadioButton2.setText(Chapters.CROCHETTIME.getName());
        jRadioButton2.setActionCommand("CROCHETTIME");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(jRadioButton3);
        jRadioButton3.setText(Chapters.MANGAONWEB.getName());
        jRadioButton3.setActionCommand("MANGAONWEB");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(nicoRadioButton);
        nicoRadioButton.setText(Chapters.NICONICO.getName());
        nicoRadioButton.setActionCommand("NICONICO");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), nicoRadioButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        nicoRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonItemStateChanged(evt);
            }
        });

        radioButtonGroup.add(jRadioButton5);
        jRadioButton5.setText(Chapters.PCVIEWER.getName());
        jRadioButton5.setActionCommand("PCVIEWER");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(jRadioButton6);
        jRadioButton6.setText(Chapters.PLUGINFREE.getName());
        jRadioButton6.setActionCommand("PLUGINFREE");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(clubSundayRadioButton);
        clubSundayRadioButton.setText(Chapters.CLUBSUNDAY.getName());
        clubSundayRadioButton.setActionCommand("CLUBSUNDAY");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), clubSundayRadioButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        clubSundayRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonItemStateChanged(evt);
            }
        });

        clubSundayKeyURL.setEnabled(false);

        jLabel3.setText("Previous URL");

        radioButtonGroup.add(jRadioButton7);
        jRadioButton7.setText(Chapters.POCO.getName());
        jRadioButton7.setActionCommand("POCO");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton7, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(jRadioButton4);
        jRadioButton4.setText(Chapters.NICONICO2.getName());
        jRadioButton4.setActionCommand("NICONICO2");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        radioButtonGroup.add(nicoAceRadioButton);
        nicoAceRadioButton.setText(Chapters.NICONICOACE.getName());
        nicoAceRadioButton.setActionCommand("NICONICOACE");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), nicoAceRadioButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        nicoAceRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonItemStateChanged(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("NicoNico Account Login"));

        emailField.setEnabled(false);

        jLabel5.setText("Password");

        jLabel4.setText("E-Mail");

        passwordField.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5))
        );

        radioButtonGroup.add(jRadioButton9);
        jRadioButton9.setText(Chapters.YAHOOBOOKSTORE.getName());
        jRadioButton9.setActionCommand("YAHOOBOOKSTORE");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, manual, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jRadioButton9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton7)
                    .addComponent(jRadioButton6)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nicoRadioButton)
                            .addComponent(nicoAceRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jRadioButton5)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(clubSundayRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clubSundayKeyURL, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                    .addComponent(jRadioButton9))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nicoRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nicoAceRadioButton))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clubSundayRadioButton)
                    .addComponent(clubSundayKeyURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        downloadButton.setText("Download");
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        statusBar.setEditable(false);
        statusBar.setText("Status:");
        statusBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusBarActionPerformed(evt);
            }
        });

        jButton1.setText("Examples");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Log");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(manual);
        manual.setText("Manual");

        buttonGroup1.add(jRadioButton8);
        jRadioButton8.setSelected(true);
        jRadioButton8.setText("Auto-detect");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(manual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 394, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(downloadDirectoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton))
                            .addComponent(viewerURLField, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(downloadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 345, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton))
                    .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(jRadioButton8))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(downloadDirectoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewerURLField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(manual)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downloadButton)
                    .addComponent(exitButton)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        
        File downloadDirectory;
        try
        {
            downloadDirectory = new File(downloadDirectoryField.getText());
            if(!downloadDirectory.exists())
                downloadDirectory.mkdirs();
            else if(!downloadDirectory.isDirectory())
            {
                DownloaderUtils.errorGUI("Directory not found or not a directory", null, false);
                return;
            }
        }
        catch(Exception e)
        {
            DownloaderUtils.errorGUI("Could not find or create directories", e, false);
            return;
        }

        URL viewerURL;
        try
        {
            viewerURL = new URL(viewerURLField.getText());
        }
        catch(Exception e)
        {
            DownloaderUtils.errorGUI("Malformed URL", e, false);
            return;
        }

        if(manual.isSelected())
        {
            Chapters c = Chapters.valueOf(radioButtonGroup.getSelection().getActionCommand());
            Chapter chapter = null;

            switch(c)
            {
                case ACTIBOOK:          chapter = new ActibookChapter(viewerURL);           break;
                case CROCHETTIME:       chapter = new CrochetTimeChapter(viewerURL);        break;
                case MANGAONWEB:        chapter = new MangaOnWebChapter(viewerURL);         break;
                case PCVIEWER:          chapter = new PCViewerChapter(viewerURL);           break;
                case PLUGINFREE:        chapter = new PluginFreeChapter(viewerURL);         break;
                case POCO:              chapter = new PocoChapter(viewerURL);               break;
                case NICONICO2:         chapter = new NicoNicoChapter2(viewerURL);          break;
                case YAHOOBOOKSTORE:    chapter = new YahooBookstoreChapter(viewerURL);     break;
                case CLUBSUNDAY:
                {
                    URL keyURL;
                    try
                    {
                        keyURL = new URL(clubSundayKeyURL.getText());
                    }
                    catch(Exception e)
                    {
                        DownloaderUtils.errorGUI("Malformed URL", e, false);
                        return;
                    }
                    chapter = new SundayChapter(viewerURL, keyURL);
                    break;
                }
                case NICONICO:
                {
                    chapter = new NicoNicoChapter(viewerURL, emailField.getText(), passwordField.getPassword());
                    break;
                }
                case NICONICOACE:
                {
                    chapter = new NicoNicoAceChapter(viewerURL, emailField.getText(), passwordField.getPassword());
                    break;
                }
            }

            if(chapter != null)
                Downloader.runChapter(chapter, downloadDirectory);
        }
        // auto detection
        else
        {
            ArrayList<Chapter> chapters = new ArrayList<Chapter>();
            chapters.add(new ActibookChapter(viewerURL));
            chapters.add(new PCViewerChapter(viewerURL));
            chapters.add(new PluginFreeChapter(viewerURL));
            chapters.add(new YahooBookstoreChapter(viewerURL));
            chapters.add(new CrochetTimeChapter(viewerURL));
            chapters.add(new MangaOnWebChapter(viewerURL));
            chapters.add(new PocoChapter(viewerURL));
            chapters.add(new NicoNicoChapter2(viewerURL));
            chapters.add(new SundayChapter(viewerURL));
            chapters.add(new NicoNicoAceChapter(viewerURL));
            chapters.add(new NicoNicoChapter(viewerURL));

            Downloader.autodetectChapter(chapters, downloadDirectory);
        }
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        WindowEvent windowClosing = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
	this.dispatchEvent(windowClosing);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Download Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            downloadDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void statusBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusBarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusBarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(examples == null)
        {
            examples = new ExamplesFrame();
        }
        examples.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        /*
        JEditorPane editor = log.getEditor();

        StringBuilder sb = new StringBuilder();
        for(String s : DownloaderUtils.LOG)
            sb.append(s).append('\n');

        editor.setText(sb.toString());
        // */

        log.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void radioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioButtonItemStateChanged
        emailField.setEnabled(manual.isSelected() && (nicoRadioButton.isSelected() || nicoAceRadioButton.isSelected()));
        passwordField.setEnabled(manual.isSelected() && (nicoRadioButton.isSelected() || nicoAceRadioButton.isSelected()));
        clubSundayKeyURL.setEnabled(manual.isSelected() && clubSundayRadioButton.isSelected());
    }//GEN-LAST:event_radioButtonItemStateChanged

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        PreferencesManager.PREFS.put(PreferencesManager.KEY_DOWNLOADDIR,
                                downloadDirectoryField.getText());
    }//GEN-LAST:event_windowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TempDownloaderFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField clubSundayKeyURL;
    private javax.swing.JRadioButton clubSundayRadioButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JTextField downloadDirectoryField;
    private javax.swing.JTextField emailField;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JRadioButton manual;
    private javax.swing.JRadioButton nicoAceRadioButton;
    private javax.swing.JRadioButton nicoRadioButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.ButtonGroup radioButtonGroup;
    private javax.swing.JTextField statusBar;
    private javax.swing.JTextField viewerURLField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
