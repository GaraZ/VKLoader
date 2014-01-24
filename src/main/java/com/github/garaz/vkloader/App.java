package com.github.garaz.vkloader;

import java.io.File;


/**
 *
 * @author GaraZ
 */
public class App {
    
    void initDependence() {
        File file =  new File(System.getProperty("user.dir"), "Config.xml");
        TimerManager timerManager = new TimerManager();
        SitesArrayList sites = new SitesArrayList();
        ProfilesArrayList profiles = new ProfilesArrayList();
        SettingsManager settingsManager = 
                new SettingsManager(file, timerManager, sites, profiles);
        FileManager fileManager = new FileManager();
        SiteManager siteManager = new SiteManager();
        VkManager vkManager = new VkManager();
        MainForm mainForm = new MainForm(sites, profiles, timerManager, 
                settingsManager, fileManager, siteManager, vkManager);
        mainForm.initSettings();
        mainForm.setVisible(true);
    }

    public static void main(String[] args) {
        App app = new App();
        app.initDependence();
    }
}
