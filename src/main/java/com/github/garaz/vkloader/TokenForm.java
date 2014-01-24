package com.github.garaz.vkloader;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author GaraZ
 */
public final class TokenForm extends javax.swing.JDialog {
    private String token = null;
    
    static final String TOKEN_ADDRESS  = new StringBuilder()
        .append("https://oauth.vk.com/authorize?")
        .append("client_id=").append(VkManager.APP_ID)
        .append("&redirect_uri=https://oauth.vk.com/blank.html")
        .append("&display=page")
        .append("&scope=wall,photos")
        .append("&response_type=token")
        .toString();
    
    
    public TokenForm() {
        initComponents();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setModal(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTextAreaAnswer.setText(null);
            }
        });
    }
    
    class LinkMouseListener extends MouseAdapter{
        @Override
        public void mouseEntered(MouseEvent e) {
            if(!(e.getSource() instanceof JLabel)){
                return;
            }
            JLabel label = (JLabel)e.getSource();
            label.setForeground(Color.RED);
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            label.setCursor(cursor);
        }
 
        @Override
        public void mouseExited(MouseEvent e) {
            if(!(e.getSource() instanceof JLabel)){
                return;
            }
            JLabel label = (JLabel)e.getSource();
            label.setForeground(Color.BLUE);
        }
    }
    
    void initComponents() {
        JPanel panel = new JPanel();
        panel.addAncestorListener(null);
        JLabel labelStart = new JLabel("To follow the link:");
        JLabel jLabelBrowser = new JLabel("www.vk.com");
        jLabelBrowser.setForeground(Color.blue);
        jLabelBrowser.setFont(new Font("Arial", Font.BOLD, 14));
        jLabelBrowser.addMouseListener(new LinkMouseListener());
        jLabelBrowser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    launchBrowser();
                }
            }
        });
        JLabel labelEnd = new JLabel("and put answer here:");
        JScrollPane jScrollPaneSite = new JScrollPane();
        jTextAreaAnswer = new JTextArea();
        jTextAreaAnswer.setLineWrap(true);
        jTextAreaAnswer.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    parseToken();
                }
            }
        });
        jScrollPaneSite.setViewportView(jTextAreaAnswer);
        JButton jButtonOk = new JButton("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseToken();
            }
        });
        GroupLayout groupLayout = new GroupLayout(panel);
        panel.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(
                    groupLayout.createSequentialGroup()
                    .addComponent(labelStart)
                    .addGap(3, 3, 3)
                    .addComponent(jLabelBrowser)
                    .addGap(3, 3, 3)
                    .addComponent(labelEnd)
                )
            .addComponent(jScrollPaneSite, 350, 350, 350)
            .addComponent(jButtonOk)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(labelStart)
                .addComponent(jLabelBrowser)
                .addComponent(labelEnd)
            )
            .addGap(5, 5, 5)
            .addComponent(jScrollPaneSite, 100, 100, 100)
            .addGap(5, 5, 5)
            .addComponent(jButtonOk)
            .addGap(10, 10, 10)
        );
        add(panel);
    }
    
    private void launchBrowser() {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(TOKEN_ADDRESS));
                } catch (IOException | URISyntaxException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Can not launch browser. Error :".concat(e.getMessage()),
                        "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    void parseToken() {
        String answer = jTextAreaAnswer.getText();
        if (jTextAreaAnswer == null) return;
        int pos = answer.indexOf("access_token");
        token = null;
        if (pos != -1) {
            token = answer.substring(pos+13, pos+98);
        }
        System.out.println(token);
        setVisible(false);
    }
    
    String showForm() {
        setVisible(true);
        return token;
    }
    
    JTextArea jTextAreaAnswer;
}
