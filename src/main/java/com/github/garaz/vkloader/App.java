package com.github.garaz.vkloader;

import java.io.File;
import java.io.IOException;


/**
 *
 * @author GaraZ
 */
public class App {
    
    void initDependence() {
        File file =  new File(System.getProperty("user.dir"), "Config.xml");
        Timer timer = new Timer();
        SitesArrayList sites = new SitesArrayList();
        ProfilesArrayList profiles = new ProfilesArrayList();
        SettingsManager settingsManager = 
                new SettingsManager(file, timer, sites, profiles);
        FileManager fileManager = new FileManager();
        SiteManager siteManager = new SiteManager();
        VkManager vkManager = new VkManager();
        MainForm mainForm = new MainForm(sites, profiles, timer, 
                settingsManager, fileManager, siteManager, vkManager);
        mainForm.initSettings();
        mainForm.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        App app = new App();
        app.initDependence();
    }
}
