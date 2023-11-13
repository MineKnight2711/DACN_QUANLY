/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

/**
 *
 * @author WitherDragon
 */
public class ImageLoader extends SwingWorker<ImageIcon, Void> {
    private final String imageUrl;
    private final int width;
    private int height;
private int progress;
    public ImageLoader(String imageUrl, int width, int height) {
        progress = 0; 
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
    }
    @Override
    protected void process(List<Void> chunks) {

      if (progress < 100) {
        progress++; 
        setProgress(progress);
      }

    }

    @Override
        protected ImageIcon doInBackground() {

          try {
            URL url = new URL(imageUrl);
            InputStream in = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
              baos.write(buffer, 0, read);
              process(null); 
            }

            byte[] imageData = baos.toByteArray();
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
            // Scale image first 
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // Draw scaled image onto new BufferedImage
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = result.createGraphics();

            // Render mode
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // Draw scaled image 
            g2.drawImage(scaled, 0, 0, null); 
            g2.dispose();
            return new ImageIcon(scaled);

          } catch (IOException e) {
            e.printStackTrace();
            return null;
          }

        }
}