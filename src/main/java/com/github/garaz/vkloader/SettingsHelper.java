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
public class SettingsHelper {
    static final String CHARSET = "UTF8";
    private final File settinsFile;
    private final Common common;
    private final Timer timer;
    private final CustomList<SiteObj> sites;
    private final CustomList<ProfileObj> profiles;
    
    public SettingsHelper(File file) {
        settinsFile = file;       
        common = new Common();
        timer = new Timer();
        sites = new SitesArrayList();
        profiles = new ProfilesArrayList();
    }
    
    Common getCommon() {
        return common;
    }
    
    Timer getTimer() {
        return timer;
    }
    
    CustomList getSitesList() { 
        return  sites;
    }
    
    CustomList getProfilesList() { 
        return  profiles;
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
    
    void readFile() throws IOException, Exception {
        try {
            if (settinsFile.exists()) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(new InputStreamReader(
                new FileInputStream(settinsFile), CHARSET));
                Element root = document.getRootElement();
                common.readFromXML(root);
                timer.readFromXML(root);
                sites.readFromXML(root);
                profiles.readFromXML(root);
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
