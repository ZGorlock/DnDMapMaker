/*
 * File:    BlackSpaceReducer.java
 * Package: mapParser
 * Author:  Zachary Gill
 */

package mapParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlackSpaceReducer {
    
    private static final int blockSize = 20;
    
    private static final int borderMinWidth = 10;
    
    
    public static void main(String[] args) {
        parseImages(getImages());
    }
    
    private static List<String> getImages() {
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
            
            List<Point> border = new ArrayList<>();
            for (int x = 0; x < data.getWidth(); x++) {
                for (int y = 0; y < data.getHeight(); y++) {
                    int xTouch = 0;
                    int yTouch = 0;
                    for (int xp = x + 1; xp < Integer.MAX_VALUE; xp++) {
                        if (inImage(data, xp, y) && isBlack(data.getRGB(xp, y))) {
                            xTouch++;
                            if (xTouch == borderMinWidth) {
                                break;
                            }
                        } else {
                            if (inImage(data, x - 1, y)) {
                                for (int xm = x - 1; xm > Integer.MIN_VALUE; xm--) {
                                    if (inImage(data, xm, y) && isBlack(data.getRGB(xm, y))) {
                                        xTouch++;
                                        if (xTouch == borderMinWidth) {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    if (xTouch == borderMinWidth) {
                        for (int yp = y + 1; yp < Integer.MAX_VALUE; yp++) {
                            if (inImage(data, x, yp) && isBlack(data.getRGB(x, yp))) {
                                yTouch++;
                                if (yTouch == borderMinWidth) {
                                    break;
                                }
                            } else {
                                if (inImage(data, x, y - 1)) {
                                    for (int ym = y - 1; ym > Integer.MIN_VALUE; ym--) {
                                        if (inImage(data, x, ym) && isBlack(data.getRGB(x, ym))) {
                                            yTouch++;
                                            if (yTouch == borderMinWidth) {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        if (yTouch == borderMinWidth) {
                            border.add(new Point(x, y));
                        }
                    }
                }
            }
            
            for (Point borderPoint : border) {
                if (borderPoint.y % blockSize < (blockSize / 2)) {
                    if (borderPoint.x % blockSize < (blockSize / 2)) {
                        data.setRGB(borderPoint.x, borderPoint.y, Color.WHITE.getRGB());
                    }
                } else {
                    if (borderPoint.x % blockSize >= (blockSize / 2)) {
                        data.setRGB(borderPoint.x, borderPoint.y, Color.WHITE.getRGB());
                    }
                }
            }
            
            saveImage(image, data);
        }
    }
    
    private static BufferedImage readImage(String filePath) {
        try {
            File file = new File(filePath);
            return ImageIO.read(file);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static void saveImage(String filePath, BufferedImage data) {
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
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);
        
        return red < 64 && green < 64 && blue < 64;
    }
    
    private static boolean inImage(BufferedImage data, int x, int y) {
        return (x >= 0) && x < data.getWidth() && (y >= 0) && y < data.getHeight();
    }
    
}
