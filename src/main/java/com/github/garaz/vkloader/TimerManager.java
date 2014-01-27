package com.github.garaz.vkloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author GaraZ
 */
public class TimerManager {
    
    private static SettingsManager settingsManager;
    private static MainForm mainForm;
    private Timer timerLabelShow, timerUpload;
    private int counter = 0;
    private int total = 0;   
    
    public TimerManager(SettingsManager settingsManager, MainForm mainForm) {
        this.settingsManager = settingsManager;
        this.mainForm = mainForm;
        init();
    }
    
    final void init() {
        ActionListener taskLabelShow = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                String text = new StringBuilder()
                        .append(String.valueOf(counter))
                        .append("/")
                        .append(String.valueOf(total))
                        .toString();
                mainForm.getTimeLabel().setText(text);
            }
        };
        
        ActionListener taskUpload = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upload();
            }
        };
        
        timerLabelShow = new Timer(1000,taskLabelShow);
        timerUpload = new Timer(total,taskUpload);
    }
    
    void upload() {
        findTotal();
        timerUpload.setDelay(total);
        counter = 0;
        if (mainForm.getJButtonUpload().isEnabled()) {
            mainForm.uploadContent();
        }
        timerLabelShow.restart();
        timerUpload.restart();
    } 
    
    void findTotal() {
        int per = settingsManager.getPeriod();
        int err = settingsManager.getTimeError();
        Random generator = new Random();
        if (err > 0) {
            total = per + err - generator.nextInt(err * 2) * 1000;
        } else {
            total = per * 1000;
        }
    }
    
    void start() {
        counter = 0;
        findTotal();
        timerUpload.setDelay(total);
        timerLabelShow.start();
        timerUpload.start();
    }
    
    void stop() {
        counter = 0;
        timerLabelShow.stop();
        timerUpload.stop();
    }
}
