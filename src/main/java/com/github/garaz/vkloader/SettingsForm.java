package com.github.garaz.vkloader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author Garaz
 */
public class SettingsForm extends javax.swing.JDialog {
    private final static Logger LOGGER = Logger.getLogger(App.class.getName());
    private static MainForm mainForm;
    private CustomList siteList;
    
    private class SitesTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (selected) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
                if (!treeNode.isRoot()) {
                    initMasks((SiteObj) treeNode.getUserObject());
                }
            }
            return this;
        }
    } 
    
    private class ActionGetDir implements ActionListener {
        JFileChooser fileChoose = new JFileChooser();
        @Override
        public void actionPerformed(ActionEvent ae) {
            JComponent source = (JComponent) ae.getSource();
            fileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChoose.setCurrentDirectory(new File(".")); 
            
            source.setEnabled(false);
            if(source == jButtonCont) {
                fileChoose.setDialogTitle("Select a directory to content");
            }else if(source == jButtonArhCont) {
                fileChoose.setDialogTitle("Select the directory for archiving content");
            }else if(source == jButtonSite) {
                fileChoose.setDialogTitle("Select a directory to save sites");
            }
            int openRes = fileChoose.showOpenDialog(jPanelSettings);
            if (openRes == JFileChooser.APPROVE_OPTION) {
                String dir = fileChoose.getSelectedFile().getPath();
                if(source == jButtonCont) {
                    jTextFieldCont.setText(dir);
                } else if(source == jButtonArhCont) {
                    jTextFieldArhCont.setText(dir);
                } else if(source == jButtonSite) {
                    jTextFieldArhSite.setText(dir);
                }
            }
            source.setEnabled(true);
        }
    }
    
    public SettingsForm(MainForm form) {
        mainForm = form;
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setModal(true);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                SettingFormShown();
            }
        });
    }

    JPanel initJPanelGeneral(JPanel jPanelGeneral) {
        ActionGetDir actDir = new ActionGetDir();
        JLabel jLabelGroupId = new JLabel("Group id");
        jTextFieldGroupId = new JTextField();
        jTextFieldGroupId.setHorizontalAlignment(JTextField.RIGHT);
        JLabel jLabelComments = new JLabel("Comments");
        JScrollPane JScrollPaneComments = new JScrollPane();
        jTextAreaComments = new JTextArea();
        JScrollPaneComments.setViewportView(jTextAreaComments);
        JLabel jLabelCont = new JLabel("Content");
        jTextFieldCont = new JTextField();
        jButtonCont = new JButton("...");
        jButtonCont.addActionListener(actDir);
        jCheckBoxArhCont = new JCheckBox("To archived content");
        JLabel jLabelArhCont = new JLabel("The archive content");
        jTextFieldArhCont = new JTextField();
        jButtonArhCont = new JButton("...");
        jButtonArhCont.addActionListener(actDir);
        jCheckBoxArhSite = new JCheckBox("To archived sites");
        JLabel jLabelArhSite = new JLabel("The archived sites");
        jTextFieldArhSite = new JTextField();
        jButtonSite = new JButton("...");
        jButtonSite.addActionListener(actDir);
        
        GroupLayout groupLayout = new GroupLayout(jPanelGeneral);
        jPanelGeneral.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addGroup(groupLayout.createParallelGroup()
                .addGap(400)
                .addGroup(
                    groupLayout.createSequentialGroup()
                    .addGroup(
                        groupLayout.createParallelGroup()
                        .addComponent(jLabelGroupId)
                        .addComponent(jLabelComments)
                        .addComponent(jLabelCont)
                        .addComponent(jLabelArhCont)
                        .addComponent(jLabelArhSite)
                    )
                    .addGap(5, 5, 5)
                    .addGroup(
                        groupLayout.createParallelGroup()
                        .addComponent(jTextFieldGroupId)
                        .addComponent(JScrollPaneComments)
                        .addComponent(jTextFieldCont)
                        .addComponent(jTextFieldArhCont)
                        .addComponent(jTextFieldArhSite)
                    )
                    .addGap(5, 5, 5)
                    .addGroup(
                        groupLayout.createParallelGroup()
                        .addComponent(jButtonCont)
                        .addComponent(jButtonArhCont)
                        .addComponent(jButtonSite)
                    )

                )
                .addComponent(jCheckBoxArhCont)
                .addComponent(jCheckBoxArhSite)
            )
            .addGap(5, 5, 5)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jLabelGroupId)
                .addComponent(jTextFieldGroupId, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            )
            .addGap(5, 5, 5)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jLabelComments)
                .addComponent(JScrollPaneComments, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
            )
            .addGap(30, 30, 30)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jLabelCont)
                .addComponent(jTextFieldCont, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonCont)
            )
            .addGap(5, 5, 5)
            .addComponent(jCheckBoxArhCont)
            .addGap(8, 8, 8)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jLabelArhCont)
                .addComponent(jTextFieldArhCont, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonArhCont)
            )
            .addGap(5, 5, 5)
            .addComponent(jCheckBoxArhSite)
            .addGap(8, 8, 8)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jLabelArhSite)
                .addComponent(jTextFieldArhSite, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonSite)
            )
            .addGap(10, 10, 10)
        );
        return jPanelGeneral;
    }
    
    JPanel initJPanelTimer(JPanel jPanelTimer) {
        JLabel jLabelPeriod = new JLabel("Period");
        jSliderPeriod = new JSlider();
        jSliderPeriod.setMajorTickSpacing(10);
        jSliderPeriod.setMaximum(120);
        jSliderPeriod.setPaintLabels(true);
        jSliderPeriod.setPaintTicks(true);
        jSliderPeriod.setSnapToTicks(true);
        JLabel jLabelErrorPeriod = new JLabel("Time error");
        jSliderTimeError = new JSlider();
        jSliderTimeError.setMajorTickSpacing(1);
        jSliderTimeError.setMaximum(20);
        jSliderTimeError.setPaintLabels(true);
        jSliderTimeError.setPaintTicks(true);
        jSliderTimeError.setSnapToTicks(true);
        
        GroupLayout groupLayout = new GroupLayout(jPanelTimer);
        jPanelTimer.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(jLabelPeriod)
                .addComponent(jLabelErrorPeriod)
                .addComponent(jSliderPeriod)
                .addComponent(jSliderTimeError)
            )
            .addGap(5, 5, 5)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(jLabelPeriod)
            .addComponent(jSliderPeriod)
            .addGap(10, 10, 10)
            .addComponent(jLabelErrorPeriod)
            .addComponent(jSliderTimeError)
            .addGap(10, 10, 10)
        );
        return jPanelTimer;
    }
    
    JPanel initJPanelSites(JPanel jPanelSites) {
        ActionListener actionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeSitesAction(evt);
            }
        };
        KeyAdapter keyAdepter = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                treeSitesKeyAdepter(evt);
            }
        };
        JScrollPane jScrollPaneSites = new JScrollPane();
        jTextFieldSiteAdd = new JTextField();
        jTextFieldSiteAdd.addKeyListener(keyAdepter);
        jPanelSites.add(jTextFieldSiteAdd);
        jTreeSites = new JTree();
        jTreeSites.setRootVisible(false);
        jTreeSites.setCellRenderer(new SitesTreeCellRenderer());
        jTreeSites.addKeyListener(keyAdepter);
        jScrollPaneSites.setViewportView(jTreeSites);
        jButtonSiteAdd = new JButton("Add");
        jButtonSiteAdd.addActionListener(actionListener);
        jButtonSiteDel = new JButton("Delete");
        jButtonSiteDel.addActionListener(actionListener);
        jButtonSiteDelAll = new JButton("Delete all");
        jButtonSiteDelAll.addActionListener(actionListener);
        JLabel jLabelDynLynk = new JLabel("Masks for dynamic links");
        jTextFieldMaskAdd = new JTextField();
        jTextFieldMaskAdd.addKeyListener(keyAdepter);
        JScrollPane jScrollPaneSitesMasks = new JScrollPane();
        jTreeSitesMasks = new JTree();
        jTreeSitesMasks.setModel(null);
        jTreeSitesMasks.setRootVisible(false);
        jTreeSitesMasks.addKeyListener(keyAdepter);
        jScrollPaneSitesMasks.setViewportView(jTreeSitesMasks);
        jButtonAddSiteMask = new JButton("Add");
        jButtonAddSiteMask.addActionListener(actionListener);
        jButtonSiteDelMask = new JButton("Delete");
        jButtonSiteDelMask.addActionListener(actionListener);
        
        GroupLayout groupLayout = new GroupLayout(jPanelSites);
        jPanelSites.setLayout(groupLayout);
        groupLayout.linkSize(jButtonSiteAdd, jButtonSiteDel, jButtonSiteDelAll,
            jButtonAddSiteMask, jButtonSiteDelMask);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jTextFieldSiteAdd)
                .addComponent(jScrollPaneSites)
                .addComponent(jLabelDynLynk)
                .addComponent(jTextFieldMaskAdd)
                .addComponent(jScrollPaneSitesMasks)
            )
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jButtonSiteAdd)
                .addComponent(jButtonSiteDel)
                .addComponent(jButtonSiteDelAll)
                .addComponent(jButtonAddSiteMask)
                .addComponent(jButtonSiteDelMask)
            )
            .addGap(5, 5, 5)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addGroup(
                groupLayout.createParallelGroup()
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(jTextFieldSiteAdd)
                    .addComponent(jScrollPaneSites, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                )
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(jButtonSiteAdd)
                    .addGap(5, 5, 5)
                    .addComponent(jButtonSiteDel)
                    .addGap(5, 5, 5)
                    .addComponent(jButtonSiteDelAll)
                )
            )
            .addGap(5, 5, 5)
            .addComponent(jLabelDynLynk)
            .addGroup(
                groupLayout.createParallelGroup()
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(jTextFieldMaskAdd)
                    .addComponent(jScrollPaneSitesMasks, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                )
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(jButtonAddSiteMask)
                    .addGap(5, 5, 5)
                    .addComponent(jButtonSiteDelMask)
                )
            )
            .addGap(10, 10, 10)
        );
        return jPanelSites;
    }
    
    JPanel initJPanelConfim(JPanel jPanelConfim) {
        ActionListener actListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource() == jButtonConfirm) {
                    saveSettings();
                }
                setVisible(false);
            }
        };
        FlowLayout flowLayout = new FlowLayout();
        jPanelConfim.setLayout(flowLayout);
        jButtonConfirm = new JButton("Confirm");
        jButtonConfirm.setPreferredSize(new Dimension(100,30));
        jButtonConfirm.addActionListener(actListener);
        jPanelConfim.add(jButtonConfirm);
        jButtonCancel = new JButton("Cancel");
        jButtonCancel.setPreferredSize(new Dimension(100,30));
        jButtonCancel.addActionListener(actListener);
        jPanelConfim.add(jButtonCancel);
        return jPanelConfim;
    }
    
    private void initComponents() {
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        jPanelSettings = new JPanel();
        add(jPanelSettings, BorderLayout.CENTER);
        JTabbedPane jTabbedPanel = new JTabbedPane();
        jPanelSettings.add(jTabbedPanel);
        jTabbedPanel.addTab("Common",initJPanelGeneral(new JPanel()));
        jTabbedPanel.addTab("Timer",initJPanelTimer(new JPanel()));
        jTabbedPanel.addTab("Sites",initJPanelSites(new JPanel()));
        add(initJPanelConfim(new JPanel()), BorderLayout.SOUTH);
    }
    
    void initCommon() throws IOException {
        try {
            jTextFieldGroupId.setText(mainForm.getApp().getSettingsHelper().getCommon().getGroupId());
            jTextAreaComments.setText(mainForm.getApp().getSettingsHelper().getCommon().getComments());
            jTextFieldCont.setText(mainForm.getApp().getSettingsHelper().getCommon().getContentDir().getAbsolutePath());
            jTextFieldArhCont.setText(mainForm.getApp().getSettingsHelper().getCommon().getArhContentDir().getAbsolutePath());
            jTextFieldArhSite.setText(mainForm.getApp().getSettingsHelper().getCommon().getArhPagesDir().getAbsolutePath());
            jCheckBoxArhCont.setSelected(mainForm.getApp().getSettingsHelper().getCommon().getIsArhContent());
            jCheckBoxArhSite.setSelected(mainForm.getApp().getSettingsHelper().getCommon().getIsArhPages());
        } catch(IOException e) {
            jTextFieldCont.setText(mainForm.getApp().getSettingsHelper().getCommon().DEF_CONTENT_DIR.getAbsolutePath());
            jTextFieldArhCont.setText(mainForm.getApp().getSettingsHelper().getCommon().DEF_ARH_CONTENT_DIR.getAbsolutePath());
            jTextFieldArhSite.setText(mainForm.getApp().getSettingsHelper().getCommon().DEF_ARH_PAGES_DIR.getAbsolutePath());
            jCheckBoxArhCont.setSelected(false);
            jCheckBoxArhSite.setSelected(false);
            throw e;
        }
    }
    
    void saveCommon() {
        mainForm.getApp().getSettingsHelper().getCommon().setGroupId(jTextFieldGroupId.getText());
        mainForm.getApp().getSettingsHelper().getCommon().setComments(jTextAreaComments.getText());
        mainForm.getApp().getSettingsHelper().getCommon().setContentDir(jTextFieldCont.getText());
        mainForm.getApp().getSettingsHelper().getCommon().setArhContentDir(jTextFieldArhCont.getText());
        mainForm.getApp().getSettingsHelper().getCommon().setArhPagesDir(jTextFieldArhSite.getText());
        mainForm.getApp().getSettingsHelper().getCommon().setArhContent(jCheckBoxArhCont.isSelected());
        mainForm.getApp().getSettingsHelper().getCommon().setArhPages(jCheckBoxArhSite.isSelected());
    }
    
    void initTimer() throws IOException {
        try {
            jSliderPeriod.setValue(mainForm.getApp().getSettingsHelper().getTimer().getPeriod()/60);
            jSliderTimeError.setValue(mainForm.getApp().getSettingsHelper().getTimer().getTimeError()/60);
        } catch(NumberFormatException e) {
            jSliderPeriod.setValue(mainForm.getApp().getSettingsHelper().getTimer().DEF_PERIOD_SECONDS);
            jSliderTimeError.setValue(mainForm.getApp().getSettingsHelper().getTimer().DEF_TIME_ERROR_SECONDS);
            throw new IOException("Timer is uncorrect.");
        }
    }
    
    void saveTimer() {
        mainForm.getApp().getSettingsHelper().getTimer().setPeriod(jSliderPeriod.getValue() * 60);
        mainForm.getApp().getSettingsHelper().getTimer().setTimeError(jSliderTimeError.getValue() * 60);
    }
    
    void initSites() {
        siteList = new SitesArrayList();
        siteList.addAll(mainForm.getApp().getSettingsHelper().getSitesList());
        mainForm.initTree(jTreeSites, "Root", siteList);
        mainForm.initTree(jTreeSitesMasks, "Root", new ArrayList());
    }
    
    void saveSites() {
        mainForm.getApp().getSettingsHelper().getSitesList().clear();
        mainForm.getApp().getSettingsHelper().getSitesList().addAll(siteList);
    }
    
    void initMasks(SiteObj siteObj) {
        mainForm.initTree(jTreeSitesMasks, "Root", siteObj.getMasks());            
    }
    
    void initSettings() {
        initSites();
        try {
            initCommon();
            initTimer();
        } catch(IOException e) {
            LOGGER.log(Level.WARNING,null,e);
            JOptionPane.showMessageDialog(this, 
                e.getMessage().concat(" Set default settings."), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        try {
            initTimer();
        } catch(IOException e) {
            LOGGER.log(Level.WARNING,null,e);
            JOptionPane.showMessageDialog(this, 
                e.getMessage().concat(" Set default settings."), "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    void saveSettings() {
        saveCommon();
        saveTimer();
        saveSites();
        try {
            mainForm.getApp().getSettingsHelper().writeSetting();
        } catch(Exception e) {
            LOGGER.log(Level.WARNING,null,e);
            JOptionPane.showMessageDialog(this, 
                e.getMessage().concat("Settings were not saved."), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        mainForm.initSettings();
    }
    
    void SettingFormShown() {
        initSettings();
        pack();
    }
    
    void addMask(JTree jTree, JTextField jTextField) {
        String text = jTextField.getText();
        if (text.trim().isEmpty()) return;
        DefaultMutableTreeNode selNode = 
            (DefaultMutableTreeNode) jTreeSites.getLastSelectedPathComponent();
        if (selNode != null){
            SiteObj siteObj = (SiteObj) selNode.getUserObject();
            siteObj.getMasks().add(text);
            jTree.setSelectionPath(new TreePath(mainForm.addTreeNode(jTree, text).getPath()));
            jTextField.setText(null);
            jTextField.requestFocus();
        }  
    }
    
    void delMask(JTree jTreeSites, JTree jTreeMasks) {
        DefaultMutableTreeNode selSite = 
            (DefaultMutableTreeNode) jTreeSites.getLastSelectedPathComponent();
        if (selSite != null) {
            SiteObj siteObj = (SiteObj) selSite.getUserObject();
            DefaultMutableTreeNode node = mainForm.removeTreeNode(jTreeMasks);
            if (node != null) {
                siteObj.getMasks().remove(node.getUserObject());
            }
        }  
    }
    
    void treeSitesAction(java.awt.event.ActionEvent evt) {
        if(evt.getSource() == jButtonSiteAdd) {
            mainForm.addSite(jTreeSites, jTextFieldSiteAdd, siteList);
        } else if(evt.getSource() == jButtonSiteDel) {
            mainForm.delSite(jTreeSites, siteList);
            mainForm.removeAllTreeNode(jTreeSitesMasks);
        } else if(evt.getSource() == jButtonSiteDelAll) {
            mainForm.removeAllTreeNode(jTreeSites);
            siteList.clear();
            mainForm.removeAllTreeNode(jTreeSitesMasks);
        } else if(evt.getSource() == jButtonAddSiteMask) {
            addMask(jTreeSitesMasks, jTextFieldMaskAdd);
        } else if(evt.getSource() == jButtonSiteDelMask) {
            delMask(jTreeSites, jTreeSitesMasks);
        }
    }
    
    void treeSitesKeyAdepter(java.awt.event.KeyEvent evt) {
        if (evt.getSource() == jTextFieldSiteAdd) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                 mainForm.addSite(jTreeSites, jTextFieldSiteAdd, siteList);
            }
        } else if (evt.getSource() == jTreeSites) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                mainForm.delSite(jTreeSites, siteList);
            }
        } else if (evt.getSource() == jTextFieldMaskAdd) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                 addMask(jTreeSitesMasks, jTextFieldMaskAdd);
            }
        } else if (evt.getSource() == jTreeSitesMasks) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                delMask(jTreeSites, jTreeSitesMasks);
            }
        }
    }
    
    JTextField jTextFieldGroupId, jTextFieldCont, jTextFieldArhCont, jTextFieldArhSite,
            jTextFieldSiteAdd, jTextFieldMaskAdd;
    JButton jButtonConfirm, jButtonCancel, jButtonCont, jButtonArhCont, jButtonSite,
            jButtonSiteAdd, jButtonSiteDel, jButtonSiteDelAll, jButtonAddSiteMask,
            jButtonSiteDelMask;
    JCheckBox jCheckBoxArhCont, jCheckBoxArhSite;
    JPanel jPanelSettings;
    JSlider jSliderPeriod, jSliderTimeError;
    JTree jTreeSites, jTreeSitesMasks;
    JTextArea jTextAreaComments;
}
