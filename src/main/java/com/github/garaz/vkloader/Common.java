package com.github.garaz.vkloader;

import java.io.File;
import java.io.IOException;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
public class Common {
    private volatile String groupId = new String();
    private volatile String comments = new String();
    private volatile String contentDir;
    private volatile boolean isArhContent;
    private volatile String arhContentDir;
    private volatile boolean isArhPages;
    private volatile String arhPagesDir;
    final File DEF_CONTENT_DIR = new File(System.getProperty("user.dir")
            .concat(File.separator)
            .concat("Cont"));
    final File DEF_ARH_CONTENT_DIR = new File (System.getProperty("user.dir")
            .concat(File.separator)
            .concat("ArhCont"));
    final File DEF_ARH_PAGES_DIR = new File(System.getProperty("user.dir")
            .concat(File.separator)
            .concat("ArhSites"));
    
    void setGroupId(String id) {
        groupId = id;
    }
    
    void setComments(String text) {
        comments = text;
    }
    
    void setContentDir(String dir) {
        contentDir = dir;
    }
        
    void setArhContent(boolean bool) {
        isArhContent = bool;
    }
        
    void setArhContentDir(String dir) {
        arhContentDir = dir;
    }
        
    void setArhPages(boolean bool) {
        isArhPages = bool;
    }
        
    void setArhPagesDir(String dir) {
        arhPagesDir = dir;
    }
    
    String getGroupId() {
        return groupId;
    }
    
    String getComments() {
        return comments;
    }
        
    File getContentDir() throws IOException {
        File file = new File(contentDir);
        if(file.isDirectory()) {
            return file;
        } else {
            throw new IOException("Path of content directory is uncorrect.");
        }
    }
        
    boolean getIsArhContent() {
        return isArhContent;
    }
        
    File getArhContentDir() throws IOException {
        File file = new File(arhContentDir);
        if(file.isDirectory()) {
            return file;
        } else {
            throw new IOException("Path of archive content directory is uncorrect.");
        }
    }
        
    boolean getIsArhPages() {
        return isArhPages;
    }
        
    File getArhPagesDir() throws IOException {
        File file = new File(arhPagesDir);
        if(file.isDirectory()) {
            return file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }
    }
        
    void initDefault() {
        contentDir = DEF_CONTENT_DIR.getAbsolutePath();
        if (!DEF_CONTENT_DIR.exists()) {
            DEF_CONTENT_DIR.mkdir();
        }
        isArhContent = false;
        arhContentDir = DEF_ARH_CONTENT_DIR.getAbsolutePath();
        if (!DEF_ARH_CONTENT_DIR.exists()) {
            DEF_ARH_CONTENT_DIR.mkdir();
        }
        isArhPages = false;
        arhPagesDir = DEF_ARH_PAGES_DIR.getAbsolutePath();
        if (!DEF_ARH_PAGES_DIR.exists()) {
            DEF_ARH_PAGES_DIR.mkdir();
        }
    }
        
    Common readFromXML(Element locRoot) {
        groupId = locRoot.selectSingleNode("Common/GroupId").valueOf("text()");
        comments = locRoot.selectSingleNode("Common/Comments").valueOf("text()");
        contentDir = locRoot.selectSingleNode("Common/ContentDir").valueOf("text()");
        isArhContent = Boolean.valueOf(locRoot.selectSingleNode("Common/IsArhContent").valueOf("text()"));
        arhContentDir = locRoot.selectSingleNode("Common/ArhContentDir").valueOf("text()");
        isArhPages = Boolean.valueOf(locRoot.selectSingleNode("Common/IsArhSites").valueOf("text()"));
        arhPagesDir = locRoot.selectSingleNode("Common/ArhSitesDir").valueOf("text()");
        return this;
    }
     
    void writeToXML(Element locRoot) {
        Element element = locRoot.addElement("Common");
        element.addElement("GroupId").addText(groupId);
        element.addElement("Comments").addText(comments);
        element.addElement("ContentDir").addText(contentDir);
        element.addElement("IsArhContent").addText(String.valueOf(isArhContent));
        element.addElement("ArhContentDir").addText(arhContentDir);
        element.addElement("IsArhSites").addText(String.valueOf(isArhPages));
        element.addElement("ArhSitesDir").addText(arhPagesDir);            
    }    
}