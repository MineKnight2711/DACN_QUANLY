/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author WitherDragon
 */
public class ImagePreviewLabel extends JLabel {
    public ImagePreviewLabel() {
        super(new ImageIcon());
    }
    public void setImage(File f) {
        ImageIcon icon = new ImageIcon(f.getPath());  
        icon.setImage(icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        setIcon(icon);
    }
}
