package com.github.garaz.vkloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author GaraZ
 */
public class SettingsManager {
    static final String CHARSET = "UTF8";
    final static File DEF_CONTENT_DIR = new File(System.getProperty("user.dir"), "Cont");
    final static File DEF_ARCHIVE_CONTENT_DIR = new File (System.getProperty("user.dir"), "ArhCont");
    final static File DEF_ARCHIVE_PAGES_DIR = new File(System.getProperty("user.dir"), "ArhSites");
    private static File settinsFile;
    private static Common common;
    private static Timer timer;
    private static XmlList<SiteObj> sites;
    private static XmlList<ProfileObj> profiles;
    
    private class Common {
        private String groupId = new String();
        private String comments = new String();
        private File contentDir;
        private boolean isArchiveContent;
        private File archiveContentDir;
        private boolean isArchivePages;
        private File archivePagesDir;

        void initDefault() {
            contentDir = DEF_CONTENT_DIR;
            if (!DEF_CONTENT_DIR.exists()) {
                DEF_CONTENT_DIR.mkdirs();
            }
            isArchiveContent = false;
            archiveContentDir = DEF_ARCHIVE_CONTENT_DIR;
            if (!DEF_ARCHIVE_CONTENT_DIR.exists()) {
                DEF_ARCHIVE_CONTENT_DIR.mkdirs();
            }
            isArchivePages = false;
            archivePagesDir = DEF_ARCHIVE_PAGES_DIR;
            if (!DEF_ARCHIVE_PAGES_DIR.exists()) {
                DEF_ARCHIVE_PAGES_DIR.mkdirs();
            }
        }

        void readFromXML(Element locRoot) {
            groupId = locRoot.selectSingleNode("Common/GroupId").valueOf("text()");
            comments = locRoot.selectSingleNode("Common/Comments").valueOf("text()");
            contentDir = new File(locRoot.selectSingleNode("Common/ContentDir").valueOf("text()"));
            isArchiveContent = Boolean.valueOf(locRoot.selectSingleNode("Common/IsArhContent").valueOf("text()"));
            archiveContentDir = new File (locRoot.selectSingleNode("Common/ArhContentDir").valueOf("text()"));
            isArchivePages = Boolean.valueOf(locRoot.selectSingleNode("Common/IsArhSites").valueOf("text()"));
            archivePagesDir = new File (locRoot.selectSingleNode("Common/ArhSitesDir").valueOf("text()"));
        }

        void writeToXML(Element locRoot) {
            Element element = locRoot.addElement("Common");
            element.addElement("GroupId").addText(groupId);
            element.addElement("Comments").addText(comments);
            element.addElement("ContentDir").addText(contentDir.getAbsolutePath());
            element.addElement("IsArhContent").addText(String.valueOf(isArchiveContent));
            element.addElement("ArhContentDir").addText(archiveContentDir.getAbsolutePath());
            element.addElement("IsArhSites").addText(String.valueOf(isArchivePages));
            element.addElement("ArhSitesDir").addText(archivePagesDir.getAbsolutePath());            
        }    
    }
    
    public SettingsManager(File file, Timer timer,
            SitesArrayList sites, ProfilesArrayList profiles) {
        settinsFile = file;       
        common = new Common();
        this.timer = timer;
        this.sites = sites;
        this.profiles = profiles;
    }
    
    void setGroupId(String id) {
        common.groupId = id;
    }
    
    void setComments(String text) {
        common.comments = text;
    }
    
    void setContentDir(File file) throws IOException {
        if(file.isDirectory()) {
            common.contentDir = file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }        
    }
        
    void setArchiveContent(boolean bool) {
        common.isArchiveContent = bool;
    }
        
    void setArchiveContentDir(File file) throws IOException {
        if(file.isDirectory()) {
            common.archiveContentDir = file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }    
    }
        
    void setArchivePages(boolean bool) {
        common.isArchivePages = bool;
    }
        
    void setArchivePagesDir(File file) throws IOException {
        if(file.isDirectory()) {
            common.archivePagesDir = file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }
    }
    
    String getGroupId() {
        return common.groupId;
    }
    
    String getComments() {
        return common.comments;
    }
        
    File getContentDir() throws IOException {
        return common.contentDir;
    }
        
    boolean isArchiveContent() {
        return common.isArchiveContent;
    }
        
    File getArchiveContentDir() {
        return common.archiveContentDir;
    }
        
    boolean isArchivePages() {
        return common.isArchivePages;
    }
        
    File getArchivePagesDir() {
        return common.archivePagesDir;
    }
    
    void initDefault() {
        common.initDefault();
        timer.initDefault();
    }
    
    void initSettings() throws IOException, Exception {
        try {
            readFile();
        } catch(IOException e) {
            initDefault();
            writeSetting();
            throw e;
        } catch(Exception ex) {
            writeSetting();
            throw ex;
        }
    }
    
    void readFile() throws IOException, Exception { //FileNotFoundException
        try {
            if (settinsFile.exists()) {
                SAXReader reader = new SAXReader();
                try (InputStreamReader inStream = new InputStreamReader(
                    new FileInputStream(settinsFile), CHARSET)) {
                    Document document = reader.read(inStream);
                    Element root = document.getRootElement();
                    common.readFromXML(root);
                    timer.readFromXML(root);
                    sites.readFromXML(root);
                    profiles.readFromXML(root);
                }
            } else {
                throw new IOException("Settings file not found. ");
            }
        } catch(DocumentException | IOException e) {
            throw new IOException(e.getMessage().concat(" Set default parameters."));
        }
    }
    
    void writeSetting() throws IOException, Exception {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Root");
        common.writeToXML(root);
        timer.writeToXML(root);
        sites.writeToXML(root);
        profiles.writeToXML(root);
        settinsFile.createNewFile();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(
                new FileOutputStream(settinsFile), CHARSET), format
        );
        try {    
            writer.write(document);
        } finally {
            writer.close();
        }
    }
}

