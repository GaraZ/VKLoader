package com.github.garaz.vkloader;

import java.util.Random;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
class TimerManager implements Runnable{
    private String period;
    private String timeError;
    private Thread thrTimer;
    private MainForm mainForm;
    final int DEF_PERIOD_SECONDS = 600;
    final int DEF_TIME_ERROR_SECONDS = 0;
    
    public void setPeriod(int seconds) {
        period = String.valueOf(seconds);
    }
    
    public void setTimeError(int seconds) {
        timeError = String.valueOf(seconds);
    }
    
    public int getPeriod() throws NumberFormatException {
        return Integer.parseInt(period);
    }
    
    public int getTimeError() throws NumberFormatException {
        return Integer.parseInt(timeError);
    }
        
    void initDefault() {
        period = String.valueOf(DEF_PERIOD_SECONDS);
        timeError = String.valueOf(DEF_TIME_ERROR_SECONDS);
    }
    
    TimerManager readFromXML(Element locRoot) {
        period = locRoot.selectSingleNode("Timer/Period").valueOf("text()");
        timeError = locRoot.selectSingleNode("Timer/TimeError").valueOf("text()");
        return this;
    }
        
    void writeToXML(Element locRoot) {
        Element element = locRoot.addElement("Timer");
        element.addElement("Period").setText(period);
        element.addElement("TimeError").setText(timeError);
    }
    
    public void setForm(MainForm mainForm) {
        this.mainForm = mainForm;
    }

    @Override
    public void run() {
        int per = Integer.parseInt(period);
        int err = Integer.parseInt(timeError);
        Random generator = new Random();
        int a;
        if (err > 0) {
             a = per + err - generator.nextInt(err * 2);
        } else {
            a = per;
        }
        try {
            for(int i = 0; ; i++) {
                if (i >= a) {
                    if (err > 0) {
                        a = per + err - generator.nextInt(err * 2);
                    } else {
                        a = per;
                    }
                    if (mainForm.getJButtonUpload().isEnabled()) {
                        mainForm.uploadContent();
                    }
                    i = 0;
                }
                String text = new StringBuilder()
                        .append(String.valueOf(i))
                        .append("/")
                        .append(String.valueOf(a))
                        .toString();
                mainForm.getTimeLabel().setText(text);
                Thread.sleep(1000);
                i++;
            }          
        } catch (InterruptedException e) {
            mainForm.getTimeLabel().setText("0");
            Thread.currentThread().interrupt();
        }
    }
    
    void start(MainForm mainForm) {
        if (this.mainForm == null) {
            this.mainForm = mainForm;
        }
        thrTimer = new Thread(this);
        thrTimer.setDaemon(true);
        thrTimer.start();
    }
    
    void stop() {
        thrTimer.interrupt();
    }
}

