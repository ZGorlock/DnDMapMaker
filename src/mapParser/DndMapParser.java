/*
 * File:    DndMapParser.java
 * Package: mapParser
 * Author:  Zachary Gill
 */

package mapParser;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Parses a DnD Map to playable size.
 */
public class DndMapParser {
    
    //Constants
    
    /**
     * The size of the tiles.
     */
    private static final int TILE_SIZE = 50;
    
    /**
     * The width in tiles per page.
     */
    private static final int WIDTH_PER_PAGE = 8;
    
    /**
     * The height in tiles per page.
     */
    private static final int HEIGHT_PER_PAGE = 10;
    
    
    //Static Fields
    
    /**
     * The map file to work with.
     */
    private static File file = null;
    
    /**
     * The file type of the map file.
     */
    private static String fileType = "";
    
    /**
     * A flag indicating whether or not the filter the map tiles.
     */
    private static boolean filter = true;
    
    /**
     * The map image.
     */
    private static BufferedImage image = null;
    
    /**
     * An array of map tiles derived from the map image.
     */
    private static List<List<BufferedImage>> tiles = new ArrayList<>();
    
    /**
     * A flag indicating whether or not the program is working on the DM map.
     */
    private static boolean dmFlag = false;
    
    /**
     * The DM map file to work with.
     */
    private static File dmFile = null;
    
    /**
     * The DM map image.
     */
    private static BufferedImage dmImage = null;
    
    
    //Main Method
    
    /**
     * The main method of the DnD Map Parser.
     *
     * @param args The arguments to the main mehotd.
     */
    public static void main(String[] args) {
        String filePath;
        String dmFilePath;
        if (args.length > 0 && args[0].length() > 1) {
            filePath = args[0];
            if (!"jpg".equals(fileType) && !"png".equals(fileType) && !"gif".equals(fileType)) {
                System.out.println("File type must be .jpg, .png, or .gif!");
                return;
            }
        } else {
            filePath = getImage();
            if (filePath.isEmpty()) {
                return;
            }
        }
        fileType = filePath.substring(filePath.length() - 3);
        
        if (args.length > 1) {
            if ("0".equals(args[1])) {
                filter = false;
            } else if ("1".equals(args[1])) {
                filter = true;
            }
        }
        
        //parse player map
        readMap(filePath);
        parseMap();
        writeMap();
    }
    
    
    //Static Methods
    
    /**
     * Reads the map.
     *
     * @param filePath The path to the map file.
     */
    private static void readMap(String filePath) {
        try {
            file = new File(filePath);
            image = ImageIO.read(file);
            
            String dmFilePath = getDMImage(filePath);
            if (!dmFilePath.isEmpty()) {
                dmFlag = true;
                dmFile = new File(dmFilePath);
                dmImage = ImageIO.read(dmFile);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Parses the map.
     */
    private static void parseMap() {
        int column = 0;
        
        System.out.println(image.getWidth() + "x" + image.getHeight() + "\n");
        
        for (int i = 0; i < image.getWidth(); i += TILE_SIZE * WIDTH_PER_PAGE) {
            tiles.add(new ArrayList<>());
            for (int j = 0; j < image.getHeight(); j += TILE_SIZE * HEIGHT_PER_PAGE) {
                int stretchX = ((i + TILE_SIZE * WIDTH_PER_PAGE) > image.getWidth()) ? (image.getWidth() - i) : TILE_SIZE * WIDTH_PER_PAGE;
                int stretchY = ((j + TILE_SIZE * HEIGHT_PER_PAGE) > image.getHeight()) ? (image.getHeight() - j) : TILE_SIZE * HEIGHT_PER_PAGE;
                
                System.out.printf("%-12s %-12s %-10s\n", "(" + i + ", " + j + ")", "(" + (i + stretchX) + ", " + (j + stretchY) + ")", "- " + stretchX + "x" + stretchY);
                
                BufferedImage newTile = new BufferedImage(TILE_SIZE * WIDTH_PER_PAGE, TILE_SIZE * HEIGHT_PER_PAGE, BufferedImage.TYPE_INT_RGB);
                BufferedImage tileMap = image.getSubimage(i, j, stretchX + (i + stretchX == image.getWidth() ? 0 : 1), stretchY + (j + stretchY == image.getHeight() ? 0 : 1));
                
                
                Graphics2D g2d = newTile.createGraphics();
                g2d.setColor(Color.WHITE);
                if (tileMap.getWidth() > tileMap.getHeight()) {
                    for (int yf = 0; yf < newTile.getHeight(); yf += TILE_SIZE) {
                        g2d.fillRect(0, yf, newTile.getWidth(), (3 * TILE_SIZE / 4));
                    }
                } else if (tileMap.getHeight() > tileMap.getWidth()) {
                    for (int xf = 0; xf < newTile.getWidth(); xf += TILE_SIZE) {
                        g2d.fillRect(xf, 0, (3 * TILE_SIZE / 4), newTile.getHeight());
                    }
                }
                g2d.drawImage(tileMap, 0, 0, null);
                g2d.dispose();
                
                tiles.get(column).add(newTile);
            }
            column++;
        }
        
        if (dmFlag) {
            double ratX = (double) dmImage.getWidth() / image.getWidth();
            double ratY = (double) dmImage.getHeight() / image.getHeight();
            int gridColor = new Color(0, 0, 0).getRGB();
            
            for (int i = 0; i < dmImage.getWidth(); i += TILE_SIZE * WIDTH_PER_PAGE * ratX) {
                for (int w = -1; w <= 1; w++) {
                    for (int j = 0; j < dmImage.getHeight(); j++) {
                        if (i + w >= 0 && i + w < dmImage.getWidth()) {
                            dmImage.setRGB(i + w, j, gridColor);
                        }
                    }
                }
                
            }
            for (int j = 0; j < dmImage.getHeight(); j += TILE_SIZE * HEIGHT_PER_PAGE * ratY) {
                for (int w = -1; w <= 1; w++) {
                    for (int i = 0; i < dmImage.getWidth(); i++) {
                        if (j + w >= 0 && j + w < dmImage.getHeight()) {
                            dmImage.setRGB(i, j + w, gridColor);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Produces the map pieces from the parsed map.
     */
    private static void writeMap() {
        try {
            //Output Directory
            File outputDir = new File("OUTPUT");
            if (!outputDir.exists()) {
                if (!outputDir.mkdir()) {
                    System.out.println("Output directory could not be created!");
                    return;
                }
            }
            
            //Map Output Directory
            String title = file.getName().substring(0, file.getName().length() - 4).replaceAll("\\s(\\d*\\s)?\\(player\\)", "");
            File mapOutputDir = new File(outputDir, title);
            if (!mapOutputDir.exists()) {
                if (!mapOutputDir.mkdir()) {
                    System.out.println("Map output directory could not be created!");
                    return;
                }
            }
            
            //Player Map
            File playerDir = new File(mapOutputDir, "map-Player");
            if (!playerDir.exists()) {
                if (!playerDir.mkdir()) {
                    System.out.println("Player map output directory could not be created!");
                    return;
                }
            }
            
            for (int i = 0; i < tiles.size(); i++) {
                for (int j = 0; j < tiles.get(i).size(); j++) {
                    if (filterTile(tiles.get(i).get(j))) {
                        String tileFile = file.getName().substring(0, file.getName().length() - 4) + " (" + i + ", " + j + ")." + fileType;
                        ImageIO.write(tiles.get(i).get(j), fileType, new File(playerDir, tileFile.replaceAll("\\s(\\d*\\s)?\\(player\\)", "")));
                        
                    }
                }
            }
            
            //DM Map
            if (dmFlag) {
                File dmDir = new File(mapOutputDir, "map-DM");
                if (!dmDir.exists()) {
                    if (!dmDir.mkdir()) {
                        System.out.println("DM map output directory could not be created!");
                        return;
                    }
                }
                
                String masterFile = dmFile.getName().substring(0, dmFile.getName().length() - 4) + "." + fileType;
                ImageIO.write(dmImage, fileType, new File(dmDir, masterFile.replaceAll("\\s(\\d*\\s)?\\(print\\)", "")));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Gets the map file to work with from the working directory.
     *
     * @return The path to the map file, or an empty string if no map file could be found.
     */
    private static String getImage() {
        File classpath = new File(".");
        File[] files = classpath.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No original map image found in directory!");
            return "";
        }
        
        for (File f : files) {
            String type = f.getName().substring(f.getName().length() - 3);
            if (("jpg".equals(type) || "png".equals(type) || "gif".equals(type)) && f.getName().contains("(player)")) { //must get player map
                return f.getName();
            }
        }
        
        return "";
    }
    
    /**
     * Gets the DM map file to work with from the working directory.
     *
     * @param mapImage The path to the map file.
     * @return The path the the DM map file, or an empty string if no DM map file could be found.
     */
    private static String getDMImage(String mapImage) {
        String dmImage = mapImage.replaceAll("\\(player\\)", "(print)");
        if (new File(dmImage).exists()) {
            return dmImage;
        }
        return "";
    }
    
    /**
     * Filters the tile for empty space.
     *
     * @param tile The tile/
     * @return Whether or not the file contains meaningful information.
     */
    private static boolean filterTile(BufferedImage tile) {
        if (!filter) {
            return true;
        }
        
        for (int i = 0; i < tile.getWidth(); i++) {
            for (int j = 0; j < tile.getHeight(); j++) {
                
                int color = tile.getRGB(i, j);
                
                int alpha = (color & 0xff000000) >> 24;
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = (color & 0x000000ff);
                
                if (red < 128 && green < 128 && blue < 128) {
                    return true;
                }
                
            }
        }
        
        return false;
    }
    
}
