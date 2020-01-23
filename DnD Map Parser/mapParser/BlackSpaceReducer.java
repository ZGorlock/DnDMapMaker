/*
 * File:    BlackSpaceReducer.java
 * Package: mapParser
 * Author:  Zachary Gill
 */

package mapParser;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Reduces black space in map images.
 */
public class BlackSpaceReducer {
    
    //Constants
    
    /**
     * The size of the block to do black space replacements with.
     */
    private static final int BLOCK_SIZE = 20;
    
    /**
     * The minimum number of adjacent black pixels to register as a border.
     */
    private static final int BORDER_MIN_WIDTH = 10;
    
    
    //Main method
    
    /**
     * The main method of the Black Space Reducer.
     *
     * @param args The aguments to the main method.
     */
    public static void main(String[] args) {
        parseImages(getImages());
    }
    
    
    //Static Methods
    
    /**
     * Gets the images in the working directory.
     *
     * @return The list of images in the working directory.
     */
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
    
    /**
     * Parses the images and reduces black space.
     *
     * @param images The list of images to parse.
     */
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
                            if (xTouch == BORDER_MIN_WIDTH) {
                                break;
                            }
                        } else {
                            if (inImage(data, x - 1, y)) {
                                for (int xm = x - 1; xm > Integer.MIN_VALUE; xm--) {
                                    if (inImage(data, xm, y) && isBlack(data.getRGB(xm, y))) {
                                        xTouch++;
                                        if (xTouch == BORDER_MIN_WIDTH) {
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
                    if (xTouch == BORDER_MIN_WIDTH) {
                        for (int yp = y + 1; yp < Integer.MAX_VALUE; yp++) {
                            if (inImage(data, x, yp) && isBlack(data.getRGB(x, yp))) {
                                yTouch++;
                                if (yTouch == BORDER_MIN_WIDTH) {
                                    break;
                                }
                            } else {
                                if (inImage(data, x, y - 1)) {
                                    for (int ym = y - 1; ym > Integer.MIN_VALUE; ym--) {
                                        if (inImage(data, x, ym) && isBlack(data.getRGB(x, ym))) {
                                            yTouch++;
                                            if (yTouch == BORDER_MIN_WIDTH) {
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
                        if (yTouch == BORDER_MIN_WIDTH) {
                            border.add(new Point(x, y));
                        }
                    }
                }
            }
            
            for (Point borderPoint : border) {
                if (borderPoint.y % BLOCK_SIZE < (BLOCK_SIZE / 2)) {
                    if (borderPoint.x % BLOCK_SIZE < (BLOCK_SIZE / 2)) {
                        data.setRGB(borderPoint.x, borderPoint.y, Color.WHITE.getRGB());
                    }
                } else {
                    if (borderPoint.x % BLOCK_SIZE >= (BLOCK_SIZE / 2)) {
                        data.setRGB(borderPoint.x, borderPoint.y, Color.WHITE.getRGB());
                    }
                }
            }
            
            saveImage(image, data);
        }
    }
    
    /**
     * Reads an image file to an image.
     *
     * @param filePath The path to an image file.
     * @return The image read from the specified file.
     */
    private static BufferedImage readImage(String filePath) {
        try {
            File file = new File(filePath);
            return ImageIO.read(file);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Saves an image to a file.
     *
     * @param filePath The path to the file to save.
     * @param data     The image to write the the specified file.
     */
    private static void saveImage(String filePath, BufferedImage data) {
        try {
            File file = new File(filePath);
            String fileType = filePath.substring(filePath.length() - 3);
            ImageIO.write(data, fileType, file);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Determines if a color is black or not.
     *
     * @param color The color.
     * @return Whether the color is black or not.
     */
    private static boolean isBlack(int color) {
        int alpha = (color & 0xff000000) >> 24;
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);
        
        return red < 64 && green < 64 && blue < 64;
    }
    
    /**
     * Determines if a position is within an image.
     *
     * @param data The image.
     * @param x    The x coordinate of the position to check.
     * @param y    The y coordinate of the position to check.
     * @return Whether or not the position is within the image.
     */
    private static boolean inImage(BufferedImage data, int x, int y) {
        return (x >= 0) && x < data.getWidth() && (y >= 0) && y < data.getHeight();
    }
    
}
