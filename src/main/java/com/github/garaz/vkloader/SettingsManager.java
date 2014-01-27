package com.github.garaz.vkloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author GaraZ
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="settings")
public class SettingsManager {
    static final String CONFIG =  "Config.xml";
    static final String CHARSET = "UTF8";
    final static File DEF_CONTENT_DIR = new File(System.getProperty("user.dir"), "Cont");
    final static File DEF_ARCHIVE_CONTENT_DIR = new File (System.getProperty("user.dir"), "ArhCont");
    final static File DEF_ARCHIVE_PAGES_DIR = new File(System.getProperty("user.dir"), "ArhSites");
    final static int DEF_PERIOD_SECONDS = 600;
    final static int DEF_TIME_ERROR_SECONDS = 0;
    private static File settinsFile = 
            new File(System.getProperty("user.dir"), CONFIG);
    
    @XmlElement
    private String groupId = "";
    @XmlElement
    private String comments = "";
    @XmlElement
    private File contentDir;
    @XmlElement
    private boolean isArchiveContent;
    @XmlElement
    private File archiveContentDir;
    @XmlElement
    private boolean isArchivePages;
    @XmlElement
    private File archivePagesDir;
    @XmlElement
    private String period;
    @XmlElement
    private String timeError;
    @XmlElement
    private static List<SiteObj> sites;
    @XmlElement
    private static List<ProfileObj> profiles;
    
    public SettingsManager() {
        super();
        sites = new ArrayList();
        profiles = new ArrayList();
    }
    
    public void setGroupId(String id) {
        groupId = id;
    }
    
    public void setComments(String text) {
        comments = text;
    }
    
    public void setContentDir(File file) throws IOException {
        if(file.isDirectory()) {
            contentDir = file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }        
    }
        
    public void setArchiveContent(boolean bool) {
        isArchiveContent = bool;
    }
        
    public void setArchiveContentDir(File file) throws IOException {
        if(file.isDirectory()) {
            archiveContentDir = file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }    
    }
        
    public void setArchivePages(boolean bool) {
        isArchivePages = bool;
    }
        
    public void setArchivePagesDir(File file) throws IOException {
        if(file.isDirectory()) {
            archivePagesDir = file;
        } else {
            throw new IOException("Path of archive sites directory is uncorrect.");
        }
    }
    
    public void setPeriod(int seconds) {
        period = String.valueOf(seconds);
    }

    public void setTimeError(int seconds) {
        timeError = String.valueOf(seconds);
    }
    
    public void setSitesList(List<SiteObj> sites) {
        this.sites = sites;
    }
    
    public void setProfilesList(List<ProfileObj> profiles) {
        this.profiles = profiles;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getComments() {
        return comments;
    }
        
    public File getContentDir() throws IOException {
        return contentDir;
    }
        
    public boolean isArchiveContent() {
        return isArchiveContent;
    }
        
    public File getArchiveContentDir() {
        return archiveContentDir;
    }
        
    public boolean isArchivePages() {
        return isArchivePages;
    }
        
    public File getArchivePagesDir() {
        return archivePagesDir;
    }
    
    public int getPeriod() throws NumberFormatException {
        return Integer.parseInt(period);
    }

    public int getTimeError() throws NumberFormatException {
        return Integer.parseInt(timeError);
    }
    
    public List<SiteObj> getSitesList() {
        return sites;
    }
    
    public List<ProfileObj> getProfilesList() {
        return profiles;
    }
    
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
        period = String.valueOf(DEF_PERIOD_SECONDS / 60) ;
        timeError = String.valueOf(DEF_TIME_ERROR_SECONDS / 60);
    }
    
    static SettingsManager readSetting() throws JAXBException {
        try {
        JAXBContext jaxbContext = JAXBContext.newInstance(
                SettingsManager.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        SettingsManager sm = (SettingsManager) jaxbUnmarshaller.unmarshal(settinsFile);
	return  sm;
        } catch(JAXBException e) {
            throw new JAXBException("Error reading settings", e);
        }
    }
    
    void writeSetting() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SettingsManager.class);
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(this, settinsFile);   
    }
}

