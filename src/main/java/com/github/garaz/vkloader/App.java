package com.github.garaz.vkloader;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author GaraZ
 */
public class App {
    
    void initDependence() {
        FileManager fileManager = new FileManager();
        SiteManager siteManager = new SiteManager();
        VkManager vkManager = new VkManager();
        MainForm mainForm = new MainForm(fileManager, siteManager, vkManager);
        mainForm.initSettings();
        mainForm.setVisible(true);
    }

    public static void main(String[] args) {
        App app = new App();
        app.initDependence();
    }
}
