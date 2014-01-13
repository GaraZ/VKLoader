package com.github.garaz.vkloader;

import java.util.Random;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
class Timer implements Runnable{
    private String period;
    private String timeError;
    private Thread thrTimer;
    private MainForm mainForm;
    final int DEF_PERIOD_SECONDS = 600;
    final int DEF_TIME_ERROR_SECONDS = 0;
    
    void setPeriod(int seconds) {
        period = String.valueOf(seconds);
    }
    
    void setTimeError(int seconds) {
        timeError = String.valueOf(seconds);
    }
    
    int getPeriod() throws NumberFormatException {
        return Integer.parseInt(period);
    }
    
    int getTimeError() throws NumberFormatException {
        return Integer.parseInt(timeError);
    }
        
    void initDefault() {
        period = String.valueOf(DEF_PERIOD_SECONDS);
        timeError = String.valueOf(DEF_TIME_ERROR_SECONDS);
    }
    
    Timer readFromXML(Element locRoot) {
        period = locRoot.selectSingleNode("Timer/Period").valueOf("text()");
        timeError = locRoot.selectSingleNode("Timer/TimeError").valueOf("text()");
        return this;
    }
        
    void writeToXML(Element locRoot) {
        Element element = locRoot.addElement("Timer");
        element.addElement("Period").setText(period);
        element.addElement("TimeError").setText(timeError);
    }
    
    void setForm(MainForm mainForm) {
        this.mainForm = mainForm;
    }

    @Override
    public void run() {
        int i = 0;
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
            do {
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
            } while(true);           
        } catch (InterruptedException e) {
            mainForm.getTimeLabel().setText("0");
            Thread.currentThread().isInterrupted();
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
