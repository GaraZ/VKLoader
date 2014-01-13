package com.github.garaz.vkloader;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 *
 * @author GaraZ
 */
public class App {
    private final static Logger LOGGER = Logger.getLogger(App.class.getName());
    private final SettingsHelper settingsHelper;
    private final FileHelper fileHelper;
    private final SiteHelper siteHelper;
    private final VkHelper vkHelper;
    
    public App(String confDir) throws IOException {
        FileHandler handler = new FileHandler(
                confDir.concat(File.separator).concat("vkl.log"), 100000, 1, true);
        LOGGER.addHandler(handler);
        settingsHelper = new SettingsHelper(
                new File(".".concat(File.separator).concat("Config.xml")));
        fileHelper = new FileHelper();
        siteHelper = new SiteHelper(this);
        vkHelper = new VkHelper();
    }
    
    CustomList getSitesList() {
        return settingsHelper.getSitesList();
    }
    
    CustomList getProfilesList() {
        return settingsHelper.getProfilesList();
    }
    
    FileHelper getFileHelper() {
        return fileHelper;
    }
    
    SettingsHelper getSettingsHelper() {
        return settingsHelper;
    }
    
    SiteHelper getSiteHelper() {
        return siteHelper;
    }
    
    VkHelper getVkHelper() {
        return vkHelper;
    }

    public static void main(String[] args) throws IOException {
        App app = new App(".");
        MainForm mainForm = new MainForm(app);
        mainForm.setVisible(true);  
        mainForm.initSettings();
    }
}
