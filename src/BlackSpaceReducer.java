/*
 * File:    BlackSpaceReducer.java
 * Package: PACKAGE_NAME
 * Author:  Zachary Gill
 */

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlackSpaceReducer {
        
    private static final int blockSize = 40;
    
    
    public static void main(String[] args)
    {
        parseImages(getImages());
    }
    
    private static List<String> getImages()
    {
        List<String> images = new ArrayList<>();
        
        File classpath = new File(".");
        File[] files = classpath.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No original map image found in directory!");
            return images;
        }
        
        for (File f : files) {
            String type = f.getName().substring(f.getName().length() - 3);
            if ("jpg".equals(type) || "png".equals(type) || "gif".equals(type)) {
                images.add(f.getName());
            }
        }
        
        return images;
    }
    
    private static void parseImages(List<String> images) {
        for (String image : images) {
            BufferedImage data = readImage(image);
            if (data == null) {
                continue;
            }
            
            boolean hit = true;
            while (hit) {
                hit = false;
                
                int ix;
                int jx;
                for (ix = 0; ix < data.getWidth(); ix++) {
                    for (jx = 0; jx < data.getHeight(); jx++) {
                        
                        if (isBlack(data.getRGB(ix, jx)) && ix + blockSize < data.getWidth() && jx + blockSize < data.getHeight()) {
                            
                            boolean nonBlock = false;
                            for (int i = ix; i < ix + blockSize; i++) {
                                for (int j = jx; j < jx + blockSize; j++) {
                                    if (!isBlack(data.getRGB(i, j))) {
                                        nonBlock = true;
                                        break;
                                    }
                                }
                                if (nonBlock) {
                                    break;
                                }
                            }
                            
                            if (!nonBlock) {
                                for (int i = ix + blockSize / 2; i < ix + blockSize; i++) {
                                    for (int j = jx; j < jx + blockSize / 2; j++) {
                                        data.setRGB(i, j, new Color(255, 255, 255).getRGB());
                                    }
                                }
                                for (int i = ix; i < ix + blockSize / 2 ; i++) {
                                    for (int j = jx + blockSize / 2; j < jx + blockSize; j++) {
                                        data.setRGB(i, j, new Color(255, 255, 255).getRGB());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            saveImage(image, data);
        }
    }
    
    private static BufferedImage readImage(String filePath)
    {
        try {
            File file = new File(filePath);
            return ImageIO.read(file);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static void saveImage(String filePath, BufferedImage data)
    {
        try {
            File file = new File(filePath);
            String fileType = filePath.substring(filePath.length() - 3);
            ImageIO.write(data, fileType, file);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static boolean isBlack(int color) {
        int alpha = (color & 0xff000000) >> 24;
        int red   = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue  = (color & 0x000000ff);
        
        return red < 64 && green < 64 && blue < 64;
    }
    
}
