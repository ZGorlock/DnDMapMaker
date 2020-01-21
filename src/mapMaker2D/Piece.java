/*
 * File:    Piece.java
 * Package: mapMaker2D
 * Author:  Zachary Gill
 */

package mapMaker2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Piece {
    
    public File file;
    public File iconFile;
    public BufferedImage icon;
    public String name;
    public Piece replaceForPlayer;
    
    public Piece(File file, File iconFile, String name, Piece replaceForPlayer) {
        this.file = file;
        this.iconFile = iconFile;
        try {
            this.icon = ImageIO.read(iconFile);
        } catch (Exception ignored) {
        }
        this.name = name;
        this.replaceForPlayer = replaceForPlayer;
    }
    
    public Piece(File file, File iconFile, String name) {
        this(file, iconFile, name, null);
    }
    
    public Piece(File file, String name, Piece replaceForPlayer) {
        this(file, file, name);
    }
    
    public Piece(File file, String name) {
        this(file, name, null);
    }
    
}
