/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;


import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

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
            ImageInputStream iis = ImageIO.createImageInputStream(url.openStream());
            BufferedImage img = ImageIO.read(iis);

            int newWidth = width; // Your desired width
            int newHeight = height; // Your desired height

            // Create a new BufferedImage for the scaled image
            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

            // Perform the scaling using AffineTransformOp with bicubic interpolation
            AffineTransform at = AffineTransform.getScaleInstance((double)newWidth / img.getWidth(),
                                                                 (double)newHeight / img.getHeight());
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
            scaledImage = scaleOp.filter(img, scaledImage);

            return new ImageIcon(scaledImage );

          } catch (IOException e) {
            e.printStackTrace();
            return null;
          }

        }
}