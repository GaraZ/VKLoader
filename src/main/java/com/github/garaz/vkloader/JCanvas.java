package com.github.garaz.vkloader;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author GaraZ
 */
public class JCanvas extends javax.swing.JPanel {
    private Image image;
    
    Image getImage() {
        return image;
    }

    public void setImage(Image aImage) {
        image = aImage;
        repaint();
    }
    
    public void setImage(File file) throws IOException {
        image = ImageIO.read(file);
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);            
        if(image != null){
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

