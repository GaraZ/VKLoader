package com.github.garaz.vkloader;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author GaraZ
 */
public class ExceptionLoggerForm extends javax.swing.JDialog {
    
    public ExceptionLoggerForm() {
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        JPanel jPanelMain = new JPanel();
        GroupLayout groupLayout = new GroupLayout(jPanelMain);
        jPanelMain.setLayout(groupLayout);
        jTextArea = new JTextArea();
        JScrollPane jScrollLog = new JScrollPane();
        jScrollLog.setViewportView(jTextArea);
        jPanelMain.add(jScrollLog);
        add(jPanelMain);
        
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup()
            .addComponent(jScrollLog, GroupLayout.DEFAULT_SIZE,
                    300, Short.MAX_VALUE)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup()
            .addComponent(jScrollLog, GroupLayout.DEFAULT_SIZE, 
                    400, Short.MAX_VALUE)
        );
    }
    
    void clean() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jTextArea.setText(null);
            }
        });
    }
    
    void put(final String string) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jTextArea.append(string);
                jTextArea.append(System.getProperty("line.separator"));
            }
        });
    }
    
    JTextArea jTextArea;
}
