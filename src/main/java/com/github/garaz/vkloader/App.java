package com.github.garaz.vkloader;

/**
 *
 * @author GaraZ
 */
public class App {
    
    void start() {
        FileManager fileManager = new FileManager();
        VkManager vkManager = new VkManager();
        MainForm mainForm = new MainForm(fileManager, vkManager);
        mainForm.initSettings();
        mainForm.setVisible(true);
    }

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
