package com.github.garaz.vkloader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author GaraZ
 */
public class MainForm extends javax.swing.JFrame {
    private static Logger logger = LogManager.getLogger(App.class.getName());
    private static List<SiteObj> sitesList;
    private static List<ProfileObj> profilesList;
    private static SettingsManager settingsManager;
    private static FileManager fileManager;
    private static SiteManager siteManager;
    private static VkManager vkManager;
    private static SettingsForm settingsForm;
    private static TokenForm tokenForm;
    private static TimerManager timerManager;
    
    public MainForm(FileManager fileManager, 
            SiteManager siteManager, VkManager vkManager) {
        this.fileManager = fileManager;
        this.siteManager = siteManager;
        this.vkManager = vkManager;
        tokenForm = new TokenForm();
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }
    
    private class FilesTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
            File file = (File) treeNode.getUserObject();
            setText(file.getName());
            if (hasFocus && selected) {
                jLabelSuccessful.setVisible(false);
                try {
                    if (file.isFile()) {
                        jCanvas.setImage(file);       
                    }
                } catch (IOException e) {
                    try {
                        logger.error(e);
                        initTreeFiles();
                    } catch (IOException ex) {
                        logger.error(ex);
                    }
                }  
            }
            return this;
        }
    }
    
    class Download implements Runnable {
        private final List<SiteObj> sitesList;
        private Runnable runnable;
        
        public Download(List<SiteObj> sitesList) {
            this.sitesList = sitesList;
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        fileManager.saveImages(siteManager.getImages(),
                                settingsManager.getContentDir().getAbsolutePath());
                        if (settingsManager.isArchivePages()) {
                            fileManager.savePages(siteManager.getPages(),
                                settingsManager.getArchivePagesDir().getAbsolutePath());
                        }
                    } catch (IOException ex) {
                        logger.error(ex);
                        JOptionPane.showMessageDialog(MainForm.this, 
                                "Images not saving", "Error!", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            jToggleButtonDownload.setText("Download");
                            initTreeFiles();
                        } catch (IOException ex) {
                            logger.error(ex);
                            JOptionPane.showMessageDialog(MainForm.this, 
                                    ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
                        }
                        jToggleButtonDownload.setSelected(false);
                    }
                }
            };
        }
        
        @Override
        public void run() {
                
            try {
                siteManager.runSitesDownload(sitesList, runnable);
            } catch (IOException | InterruptedException e) {
                logger.error(e);
                JOptionPane.showMessageDialog(MainForm.this, 
                        "Downloading error", "Error!", JOptionPane.ERROR_MESSAGE);
            } 
        }
    }
       
    public JTree getTreeSites() {
        return jTreeSites;
    }
        
    void menuAction(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jMenuItemOptions) {
            settingsForm.setVisible(true);
        } else if (evt.getSource() == jMenuItemHelp) {
            String text = new StringBuilder()
                .append("Write me and I'll help you :-)")
                .append(System.getProperty("line.separator"))
                .append("Email: pilgrim_88@mail.ru")
                .append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"))
                .append("Hot keys:")
                .append(System.getProperty("line.separator"))
                .append("\"Space\" - select\\unselect image")
                .append(System.getProperty("line.separator"))
                .append("\"Del\" - delete image")
                .append(System.getProperty("line.separator"))
                .append("\"Enter\" - upload image to vk.com")
                .toString();
            JOptionPane.showMessageDialog(this, text, "About", 
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else if (evt.getSource() == jMenuItemAbout) {
            JOptionPane.showMessageDialog(this, "HELLO! :-)", "About", 
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    JMenuBar initMenuBar(JMenuBar jMenuBarMain) {
        ActionListener actionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAction(evt);
            }
        };
        JMenu jMenuSettings = new JMenu("Settings");
        jMenuItemOptions = new JMenuItem("Options");
        jMenuItemOptions.addActionListener(actionListener);
        jMenuSettings.add(jMenuItemOptions);
        jMenuBarMain.add(jMenuSettings);
        JMenu jMenuHelp = new JMenu();
        jMenuHelp.setText("Help");
        jMenuItemHelp = new JMenuItem();
        jMenuItemHelp.setText("Help");
        jMenuItemHelp.addActionListener(actionListener);
        jMenuHelp.add(jMenuItemHelp);
        jMenuHelp.add(new javax.swing.JPopupMenu.Separator());
        jMenuItemAbout = new JMenuItem();
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(actionListener);
        jMenuHelp.add(jMenuItemAbout);
        jMenuBarMain.add(jMenuHelp);
        return jMenuBarMain;
    }
    
    JPanel initJPanelTop(JPanel jPanelTop) {
        ActionListener actionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectAction(evt);
            }
        };
        
        JLabel jLabelLogin = new JLabel("Login");
        jComboBoxLogin = new JComboBox();
        jComboBoxLogin.setEditable(true);
        jButtonClearProfile = new JButton("Clear");
        jButtonClearProfile.addActionListener(actionListener);
        jButtonClearToken = new JButton("Clear token");
        jButtonClearToken.addActionListener(actionListener);
        jCheckBoxSaveToken = new JCheckBox("Save token");
        jCheckBoxSaveToken.setSelected(true);
        jToggleButtonConnect = new JToggleButton();
        jToggleButtonConnect.setText("Conect");
        jToggleButtonConnect.addActionListener(actionListener);
        jToggleButtonTimer = new JToggleButton() ;
        jToggleButtonTimer.setText("Timer");
        jToggleButtonTimer.addActionListener(actionListener);
        jLabelTime = new JLabel();
        jLabelTime.setText("0/0");
        jLabelSuccessful = new JLabel("Upload is successful");
        jLabelSuccessful.setForeground(Color.red);
        jLabelSuccessful.setVisible(false);
        
        GroupLayout groupLayout = new GroupLayout(jPanelTop);
        jPanelTop.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(jLabelLogin)
            .addGap(5, 5, 5)
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(jComboBoxLogin, 170, 170, 170)
                .addComponent(jCheckBoxSaveToken)
            )
            .addGap(5, 5, 5)
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(jButtonClearProfile, 100, 100, 100)
                .addComponent(jButtonClearToken, 100, 100, 100)
            )
            .addGap(15, 15, 15)
            .addComponent(jToggleButtonConnect)
            .addGap(25, 25, 25)
            .addComponent(jToggleButtonTimer)
            .addGap(5, 5, 5)
            .addComponent(jLabelTime)
            .addGap(20, 20, 20)    
            .addComponent(jLabelSuccessful)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup()
                        .addComponent(jLabelLogin)
                        .addComponent(jComboBoxLogin)
                        .addComponent(jButtonClearProfile)
                    )
                    .addGap(3, 3, 3)
                    .addGroup(groupLayout.createParallelGroup()
                        .addComponent(jCheckBoxSaveToken)
                        .addComponent(jButtonClearToken)
                    )
                )
                .addComponent(jToggleButtonConnect)
                .addComponent(jToggleButtonTimer)
                .addComponent(jLabelTime)
                .addComponent(jLabelSuccessful)
            )
            .addGap(5,5,5)
        );
        return jPanelTop;
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
        jTextFieldSiteAdd = new JTextField(10);
        jTextFieldSiteAdd.addKeyListener(keyAdepter);
        JScrollPane jScrollPaneSite = new JScrollPane();
        jTreeSites = new JTree();
        jTreeSites.setModel(null);
        jTreeSites.setRootVisible(false);
        jScrollPaneSite.setViewportView(jTreeSites);
        jTreeSites.addKeyListener(keyAdepter);
        jButtonSiteAdd = new JButton("Add");
        jButtonSiteAdd.addActionListener(actionListener);
        jButtonSiteDel = new JButton("Delete");
        jButtonSiteDel.addActionListener(actionListener);
        jButtonSiteDelAll = new JButton("Delete all");
        jButtonSiteDelAll.addActionListener(actionListener);
        jToggleButtonDownload = new JToggleButton("Download");
        jToggleButtonDownload.addActionListener(actionListener);
        
        GroupLayout groupLayout = new GroupLayout(jPanelSites);
        jPanelSites.setLayout(groupLayout);
        groupLayout.linkSize(jButtonSiteAdd, jButtonSiteDel, jButtonSiteDelAll);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGroup(
                groupLayout.createParallelGroup()
                .addGap(200)
                .addComponent(jTextFieldSiteAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPaneSite, 200, 200, 200)
                .addComponent(jToggleButtonDownload, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            )
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jButtonSiteAdd)
                .addComponent(jButtonSiteDel)
                .addComponent(jButtonSiteDelAll)
            )            
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup()
            .addGroup(
                groupLayout.createSequentialGroup()
                .addComponent(jTextFieldSiteAdd)
                .addComponent(jScrollPaneSite, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3,3,3)
                .addComponent(jToggleButtonDownload)
            )
            .addGroup(
                groupLayout.createSequentialGroup()
                .addComponent(jButtonSiteAdd)
                .addGap(5, 5, 5)
                .addComponent(jButtonSiteDel)
                .addGap(5, 5, 5)
                .addComponent(jButtonSiteDelAll)
            )
        );
        return jPanelSites;
    }
    
    JPanel initJPanelFiles(JPanel jPanelFiles) {
        ActionListener actionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeFilesAction(evt);
            }
        };
        KeyAdapter keyAdepter = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                treeFilesKeyAdepter(evt);
            }
        };
        JScrollPane jScrollPaneFile = new JScrollPane();
        jTreeFiles = new JTree();
        jTreeFiles.setModel(null);
        jTreeFiles.setCellRenderer(new FilesTreeCellRenderer());
        jTreeFiles.setRootVisible(false);
        jTreeFiles.addKeyListener(keyAdepter);
        jScrollPaneFile.setViewportView(jTreeFiles);
        JScrollPane jScrollPaneSelFile = new JScrollPane();
        jTreeSelFiles = new JTree();
        jTreeSelFiles.setModel(null);
        jTreeSelFiles.setCellRenderer(new FilesTreeCellRenderer());
        jTreeSelFiles.setRootVisible(false);
        jTreeSelFiles.addKeyListener(keyAdepter);
        jScrollPaneSelFile.setViewportView(jTreeSelFiles);
        jButtonFileDel = new JButton("Delete");
        jButtonFileDel.addActionListener(actionListener);
        jButtonFileDelAll = new JButton("Delete all");
        jButtonFileDelAll.addActionListener(actionListener);
        jButtonUnselect = new JButton("▲");
        jButtonUnselect.addActionListener(actionListener);
        jButtonSelect = new JButton("▼");
        jButtonSelect.addActionListener(actionListener);
        jButtonSelUp = new JButton("▲");
        jButtonSelUp.addActionListener(actionListener);
        jButtonSelDown = new JButton("▼");
        jButtonSelDown.addActionListener(actionListener);
        jButtonSelClearAll = new JButton("Clear all");
        jButtonSelClearAll.addActionListener(actionListener);
        
        GroupLayout groupLayout = new GroupLayout(jPanelFiles);
        jPanelFiles.setLayout(groupLayout);
        groupLayout.linkSize(jButtonFileDel, jButtonFileDelAll, jButtonSelUp,
                jButtonSelDown, jButtonSelClearAll);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jScrollPaneFile, 200, 200, 200)
                .addComponent(jButtonUnselect, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)               
                .addComponent(jButtonSelect, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPaneSelFile, 200, 200, 200)
            )
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jButtonFileDel)
                .addComponent(jButtonFileDelAll)
                .addComponent(jButtonSelUp)
                .addComponent(jButtonSelDown)
                .addComponent(jButtonSelClearAll)
            )            
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jScrollPaneFile, GroupLayout.DEFAULT_SIZE, Short.SIZE, Short.MAX_VALUE)
                .addGroup(
                    groupLayout.createSequentialGroup()
                    .addComponent(jButtonFileDel)
                    .addGap(5,5,5)
                    .addComponent(jButtonFileDelAll)
                )
            )
            .addComponent(jButtonUnselect, 15, 15, 15)     
            .addComponent(jButtonSelect, 15, 15, 15)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(jScrollPaneSelFile, GroupLayout.DEFAULT_SIZE, Short.SIZE, Short.MAX_VALUE)
                .addGroup(
                    groupLayout.createSequentialGroup()
                    .addComponent(jButtonSelUp)
                    .addComponent(jButtonSelDown)
                    .addGap(5,5,5)
                    .addComponent(jButtonSelClearAll)
                )   
            )
        );
        return jPanelFiles;
    }
    
    
    JPanel initJPanelRigth(JPanel jPanelRigth) {
        JTabbedPane jTabbedPanelTree = new JTabbedPane();
        jPanelRigth.add(jTabbedPanelTree);
        jTabbedPanelTree.addTab("Sites",initJPanelSites(new JPanel()));
        jTabbedPanelTree.addTab("Files",initJPanelFiles(new JPanel()));
        return jPanelRigth;
    }
    
    JPanel initJPanelCenter(JPanel jPanelCenter) {
        ActionListener actionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadContent();
            }
        };
        jCanvas = new JCanvas();
        jButtonUpload = new JButton("Upload");
        jButtonUpload.setEnabled(false);
        jButtonUpload.addActionListener(actionListener);     
        
        GroupLayout groupLayout = new GroupLayout(jPanelCenter);
        jPanelCenter.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(jCanvas, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addComponent(jButtonUpload, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            )
            .addGap(5, 5, 5)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(jCanvas, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addGap(5, 5, 5)
            .addComponent(jButtonUpload)
            .addGap(5, 5, 5)
        );
        return jPanelCenter;
    }
                         
    private void initComponents() {
        setJMenuBar(initMenuBar(new JMenuBar()));
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        add(initJPanelCenter(new JPanel()), BorderLayout.CENTER);
        add(initJPanelTop(new JPanel()), BorderLayout.NORTH);
        add(initJPanelRigth(new JPanel()), BorderLayout.EAST);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }
    
    void initProfiles(List list) {
        DefaultComboBoxModel model = new DefaultComboBoxModel(list.toArray());
        jComboBoxLogin.setModel(model);
    }
    
    public JLabel getTimeLabel() {
        return jLabelTime;
    }
    
    public JButton getJButtonUpload() {
        return jButtonUpload;
    }
    
    private <T>DefaultMutableTreeNode buildTree(DefaultMutableTreeNode root, List<T> list) {
        if (!list.isEmpty()) {
            try{     
                DefaultMutableTreeNode treeNodeF;
                for(T t: list){
                    treeNodeF = new DefaultMutableTreeNode(t);
                    root.add(treeNodeF);
                }
            } catch(Exception e) {
                logger.error(e);
                root.add(new DefaultMutableTreeNode("The error building a tree."));
            }
        }
        return root;
    }
    
    <T> void initTree(JTree jTree, T rootVal, List list) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootVal);
        DefaultTreeModel treeModel = new DefaultTreeModel(buildTree(root, list));
        jTree.setModel(treeModel);            
    }

    void initTreeFiles() throws IOException {
        File file = null;
        try {
            try {
                file = settingsManager.getContentDir();
            } catch(IOException e) {
                file = settingsManager.DEF_CONTENT_DIR;
                throw new IOException("Path of content directory is incorrect. Set default value.");
            }
        } finally {
            fileManager.initContent(file);
            initTree(jTreeSelFiles, file, fileManager.getHarmonizeSelFiles());
            initTree(jTreeFiles, file, fileManager.getContList());
        }      
    }    

    void initSettings() {
        try {
            settingsManager = SettingsManager.readSetting();
        } catch(JAXBException ex) {
                settingsManager = new SettingsManager();
                settingsManager.initDefault();
                logger.error(ex);
                JOptionPane.showMessageDialog(this, 
                        ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            try {
                settingsManager.writeSetting();
            } catch(JAXBException e) {
                logger.error(e);
                JOptionPane.showMessageDialog(this, 
                        e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
        sitesList = settingsManager.getSitesList();
        profilesList = settingsManager.getProfilesList();
        settingsForm = new SettingsForm(sitesList, 
                settingsManager, this);
        timerManager = new TimerManager(settingsManager, this);
        initProfiles(profilesList);
        try {
            initTree(jTreeSites, "Root", sitesList); 
            initTreeFiles();
        } catch(IOException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, 
                    e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    <T> DefaultMutableTreeNode addTreeNode(JTree jTree, T t) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(t);
        DefaultTreeModel model = (DefaultTreeModel)jTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        root.add(treeNode);
        model.reload(root);
        jTree.setModel(model);
        return treeNode;
    }
    
    void addSite(JTree jTreeSites, JTextField jTextField, List<SiteObj> sitesList) {
        String text = jTextField.getText().trim();
        if (text.isEmpty()) return;
        try {    
            URI uri = SiteManager.verifyUrl(text);
            SiteObj siteObj = new SiteObj(uri, new ArrayList());
            sitesList.add(siteObj);
            jTreeSites.setSelectionPath(new TreePath(addTreeNode(jTreeSites, siteObj).getPath()));
            jTextField.setText(null);
            jTextField.requestFocus();
        } catch (URISyntaxException e) {
            jTextField.setText(null);
            logger.error(e);
            JOptionPane.showMessageDialog(null, 
                    "URL is incorrect.", "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    DefaultMutableTreeNode getFocusDown(JTree jTRee, DefaultMutableTreeNode selNode) {
        if (selNode != null) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) selNode.getParent();
            int index = root.getIndex(selNode);
            if (index == root.getChildCount()-1) {
                index--;
            } else {
                index++;
            }
            if (index >= 0) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(index);
                jTRee.setSelectionPath(new TreePath(node.getPath()));
                return node;
            }            
        }
        return null;
    }
       
    DefaultMutableTreeNode removeTreeNode(JTree jTree) {
        DefaultMutableTreeNode selNode = 
            (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        if (selNode == null) {        
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
            if (root.getChildCount() > 0) {
                selNode = (DefaultMutableTreeNode) root.getChildAt(0);
            } else return null;
        }
        getFocusDown(jTree, selNode);
        model.removeNodeFromParent(selNode);
        return selNode;
    }
    
    List removeAllTreeNode(JTree jTRee) {
        DefaultTreeModel model = (DefaultTreeModel) jTRee.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        List list = new ArrayList();
        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            list.add(((DefaultMutableTreeNode) rootNode.getChildAt(i)).getUserObject());
        }
        rootNode.removeAllChildren();
        model.reload();
        return list;
    }
    
    void delSite(JTree jTree, List<SiteObj> sitesList) {
        DefaultMutableTreeNode node = removeTreeNode(jTree);
        if (node != null) {
            sitesList.remove(node.getUserObject());
        }
    }
    
    void download(List<SiteObj> sitesList, JToggleButton jToggleButton) {
        siteManager.clean();
        jToggleButtonDownload.setSelected(true);
        SwingUtilities.invokeLater (new Download(sitesList));
        
    }
    
    void downloadSites() {
        if (jToggleButtonDownload.isSelected()) {
            download(sitesList, jToggleButtonDownload);
            try {
                initTreeFiles();
            } catch (IOException e) {
                logger.error(e);
                JOptionPane.showMessageDialog(this, 
                        "Downloading error", "Error!", JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            jToggleButtonDownload.setText("Stop...");
            siteManager.stop();
            jToggleButtonDownload.setSelected(true);
        }
    }
    
    void treeSitesAction(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jButtonSiteAdd) {
            addSite(jTreeSites, jTextFieldSiteAdd, sitesList);
        } else if (evt.getSource() == jButtonSiteDel) {
            delSite(jTreeSites, sitesList);
        } else if (evt.getSource() == jButtonSiteDelAll) {
            removeAllTreeNode(jTreeSites);
            sitesList.clear();
        } else if (evt.getSource() == jToggleButtonDownload) {
            downloadSites();
        }
        try {
            settingsManager.writeSetting();
        } catch(JAXBException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, 
                    "Settings were not saved. ".concat(e.getMessage()), 
                    "Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    void treeSitesKeyAdepter(java.awt.event.KeyEvent evt) {
        if (evt.getSource() == jTextFieldSiteAdd) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                addSite(jTreeSites, jTextFieldSiteAdd, sitesList);
            }
        } else if (evt.getSource() == jTreeSites) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                delSite(jTreeSites, sitesList);
            }
        }
        try {
            settingsManager.writeSetting();
        } catch(JAXBException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, 
                    "Settings were not saved.", "Error!", 
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    File selectFile(JTree jTreeFrom, JTree jTreeTo) {
        DefaultMutableTreeNode selNode = removeTreeNode(jTreeFrom);
        if (selNode != null) {
            jTreeTo.setSelectionPath(new TreePath(
                    addTreeNode(jTreeTo, selNode.getUserObject()).getPath()));
            return (File) selNode.getUserObject();
        }
        jTreeTo.requestFocus();
        return null;
    }
    
    boolean changeNodePos(javax.swing.JTree jTree,int lvl) {
        DefaultMutableTreeNode selNode = 
            (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent(); 
        if (selNode == null) return false;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
        if (parent == null) return false;
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        int actLvlNode = model.getIndexOfChild(parent, selNode);
        if((actLvlNode+lvl >= 0) && (actLvlNode+lvl <= parent.getChildCount()-1)){
            model.removeNodeFromParent(selNode);
            model.insertNodeInto(selNode, parent, actLvlNode+lvl);
            jTree.setSelectionPath(new TreePath(selNode.getPath()));
            return true;
        }
        return false;
    }
    
    void chabgeSelFilePos(int vector) {
        DefaultMutableTreeNode selNode = 
                (DefaultMutableTreeNode) jTreeSelFiles.getLastSelectedPathComponent();
        if (selNode == null) return;
        if (fileManager.changeSelListPos((File) selNode.getUserObject(), vector)){
            if(!changeNodePos(jTreeSelFiles, vector)) {
                String text = "Рассинхронизация списка загрузок.";
                logger.error(text);
                JOptionPane.showMessageDialog(this, 
                    text, "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    void clearSelFiles() {
        List list;
        list = removeAllTreeNode(jTreeSelFiles);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            fileManager.unselectFile((File) list.get(i));
            addTreeNode(jTreeFiles, list.get(i));
        }
    }
    
    void deleteFile(File file) {
        try {
            fileManager.removeFile(file);
        } catch(IOException ex) {
            logger.error(ex);
            JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), "Error!", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    void deleteAllFile() {
        try {
            removeAllTreeNode(jTreeFiles);            
            fileManager.removeAllFiles();
        } catch(IOException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, 
                    e.getMessage(), "Error!", 
                    JOptionPane.ERROR_MESSAGE);
            try {
                initTreeFiles();
            } catch(IOException ex) {
                logger.error(ex);
                JOptionPane.showMessageDialog(this, 
                        ex.getMessage(), "Error!", 
                        JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
    void treeFilesAction(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jButtonFileDel) {
            deleteFile(
                    (File) removeTreeNode(jTreeFiles).getUserObject()
            );
        } else if (evt.getSource() == jButtonFileDelAll) {
            deleteAllFile();
        } else if (evt.getSource() == jButtonUnselect) {
            fileManager.unselectFile(
                    selectFile(jTreeSelFiles, jTreeFiles)
            );
        } else if (evt.getSource() == jButtonSelect) {
            fileManager.selectFile(
                    selectFile(jTreeFiles, jTreeSelFiles)
            );
        } else if (evt.getSource() == jButtonSelUp) {
            chabgeSelFilePos(-1);
        } else if (evt.getSource() == jButtonSelDown) {
            chabgeSelFilePos(1);
        } else if (evt.getSource() == jButtonSelClearAll) {
            clearSelFiles();
        }     
    }
    
    void treeFilesKeyAdepter(java.awt.event.KeyEvent evt) {
        if (evt.getSource() == jTreeFiles) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                fileManager.selectFile(
                    selectFile(jTreeFiles, jTreeSelFiles));
            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                deleteFile(
                    (File) removeTreeNode(jTreeFiles).getUserObject()
                );
            }
        } else if (evt.getSource() == jTreeSelFiles) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                fileManager.unselectFile(
                    selectFile(jTreeSelFiles, jTreeFiles));
            } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if (jButtonUpload.isEnabled()) {
                    uploadContent();
                }
            }
        }
    }
    
    void uploadContent() {
        jLabelSuccessful.setVisible(false);
        DefaultMutableTreeNode treeNode = 
            (DefaultMutableTreeNode) jTreeSelFiles.getLastSelectedPathComponent();
        if (treeNode == null) return;
            try {
                File file = (File) treeNode.getUserObject();
                String groupId = settingsManager.getGroupId();
                String message = settingsManager.getComments();
                if (vkManager.upload(file, groupId, message) != null) {
                    jLabelSuccessful.setVisible(true);
                    if (settingsManager.isArchiveContent()) {
                        fileManager.moveToArchive(
                                settingsManager.getArchiveContentDir(),
                                file
                        );
                    } else {
                        deleteFile(file);
                    }
                    removeTreeNode(jTreeSelFiles);
                }
            } catch(VkAPIException ex) {
                logger.error(ex);
                jButtonUpload.setEnabled(false);
                String text = new StringBuilder()
                        .append(ex.getMessage())
                        .append(System.getProperty("line.separator"))
                        .append("Try clear token.")
                        .append(System.getProperty("line.separator"))
                        .append("Maybe group id is incorrect.")
                        .append(System.getProperty("line.separator"))
                        .append("Try connect to http://vk.com/ again.")
                        .toString();
                JOptionPane.showMessageDialog(this, text, "Error!",
                    JOptionPane.WARNING_MESSAGE);
            } catch(IOException | URISyntaxException e) {
                logger.error(e);
                jButtonUpload.setEnabled(false);
                jToggleButtonConnect.setSelected(false);
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            }
    }  
    
    void addProfile(ProfileObj profile) {
        if (!profilesList.contains(profile)) {
            jComboBoxLogin.addItem(profile);
            profilesList.add(profile);
        }
        jComboBoxLogin.setSelectedItem(profile);
    }
    
    ProfileObj currentProfile() {
        Object obj = jComboBoxLogin.getSelectedItem();
        if (obj == null) {
            return null;
        }
        ProfileObj profile;
        if (obj.getClass() == ProfileObj.class) {
            profile = (ProfileObj) obj;
        } else {
            profile = new ProfileObj(String.valueOf(obj));
        }
        return profile;
    }
    
    void connectToVk(ProfileObj profile) {
        try {
            if (jToggleButtonConnect.isSelected()) {
                jComboBoxLogin.setEnabled(false);
                if (profile == null) 
                    throw new IOException("Please enter the login");
                if (profile.getToken() == null) {
                    String token = tokenForm.showForm();
                    profile.setToken(token, jCheckBoxSaveToken.isSelected());
                    if (token == null) {
                        throw new IOException("Token is incorrect");
                    }
                    addProfile(profile);
                }
                profile.setSaveToken(jCheckBoxSaveToken.isSelected());
                vkManager.setToken(profile.getToken());  
                jButtonUpload.setEnabled(true);
            } else {
                jComboBoxLogin.setEnabled(true);
                jButtonUpload.setEnabled(false);
            }
        } catch (IOException | IllegalStateException e) {
            jComboBoxLogin.setEnabled(true);
            jToggleButtonConnect.setSelected(false);
            jButtonUpload.setEnabled(false);
            logger.error(e);
            JOptionPane.showMessageDialog(this, 
                   "Connection error. ".concat(e.getMessage()), "Error!", 
                   JOptionPane.ERROR_MESSAGE
            );
        }  
    }
    
    void removeProfile() {
        Object obj = jComboBoxLogin.getSelectedItem();
        if (obj != null) {
            jComboBoxLogin.removeItem(obj);
            profilesList.remove(obj);
        }
    }
    
    void connectAction(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jToggleButtonConnect) {
            connectToVk(currentProfile());
        } else if (evt.getSource() == jButtonClearProfile) {
            removeProfile();
        } else if (evt.getSource() == jButtonClearToken) {
            currentProfile().setToken(null, true);
            jButtonUpload.setEnabled(false);
            jToggleButtonConnect.setSelected(false);
        } else if (evt.getSource() == jToggleButtonTimer) {
            runTimer();
        }
        try {
            settingsManager.writeSetting();
        } catch(JAXBException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, 
                   "Profiles were not saved. ".concat(e.getMessage()), "Error!",
                   JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    void runTimer() {
        if (jToggleButtonTimer.isSelected()) {
            jLabelTime.setText("0/0");
            timerManager.start();
        } else {
            timerManager.stop();
        }
    }
    
    private JTree jTreeSites, jTreeFiles, jTreeSelFiles;
    private JTextField jTextFieldSiteAdd;
    private JButton jButtonSiteAdd, jButtonSiteDel, jButtonSiteDelAll,
            jButtonFileDel, jButtonFileDelAll, jButtonUnselect,
            jButtonSelect, jButtonSelUp, jButtonSelDown, jButtonSelClearAll,
            jButtonUpload, jButtonClearProfile, jButtonClearToken;
    private JCanvas jCanvas;
    private JToggleButton jToggleButtonConnect, jToggleButtonDownload,
            jToggleButtonTimer;
    private JComboBox jComboBoxLogin;
    private JCheckBox jCheckBoxSaveToken;
    private JLabel jLabelTime, jLabelSuccessful;
    private JMenuItem jMenuItemOptions, jMenuItemHelp, jMenuItemAbout;
}