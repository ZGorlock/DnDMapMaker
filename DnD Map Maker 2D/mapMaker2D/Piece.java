/*
 * File:    Piece.java
 * Package: mapMaker2D
 * Author:  Zachary Gill
 */

package mapMaker2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Defines a Piece.
 */
public class Piece {
    
    //Constants
    
    /**
     * The size of a Piece.
     */
    public static final int PIECE_SIZE = 50;
    
    
    //Fields
    
    /**
     * The image file of the icon of the Piece.
     */
    public File iconFile;
    
    /**
     * The icon of the Piece.
     */
    public BufferedImage icon;
    
    /**
     * The highlighted icon of the Piece.
     */
    public BufferedImage highlightedIcon;
    
    /**
     * The name of the Piece.
     */
    public String name;
    
    /**
     * The x dimension of the Piece, in map squares.
     */
    public int sizeX;
    
    /**
     * The y dimension of the Piece, in map squares.
     */
    public int sizeY;
    
    /**
     * A Piece to replace this Piece with when generating the Player Map, null if no replacement.
     */
    public Piece replaceForPlayer;
    
    /**
     * The sub Pieces composing this Piece.
     */
    public Piece[][] subPieces = new Piece[1][1];
    
    /**
     * The parent Piece of this Piece.
     */
    public Piece parentPiece;
    
    
    //Constructors
    
    /**
     * Constructs a Piece.
     *
     * @param iconFile         The image file of the icon of the Piece.
     * @param name             The name of the Piece.
     * @param replaceForPlayer The Piece to replace this Piece with when generating the Player Map, null if no replacement.
     */
    public Piece(File iconFile, String name, Piece replaceForPlayer) {
        this.iconFile = iconFile;
        try {
            this.icon = ImageIO.read(iconFile);
        } catch (Exception e) {
            return;
        }
        generateHighlightedIcon();
        this.name = name;
        this.sizeX = icon.getWidth() / PIECE_SIZE;
        this.sizeY = icon.getHeight() / PIECE_SIZE;
        this.replaceForPlayer = replaceForPlayer;
        subPieces = new Piece[sizeX][sizeY];
        generateSubPieces();
    }
    
    /**
     * Constructs a Piece.
     *
     * @param iconFile The image file of the icon of the Piece.
     * @param name     The name of the Piece.
     */
    public Piece(File iconFile, String name) {
        this(iconFile, name, null);
    }
    
    /**
     * Private no argument constructor for a Piece.
     */
    private Piece() {
    }
    
    
    //Methods
    
    /**
     * Generates the highlighted icon for the Piece.
     */
    private void generateHighlightedIcon() {
        this.highlightedIcon = new BufferedImage(icon.getWidth(), icon.getHeight(), icon.getType());
        Graphics iconGraphics = highlightedIcon.getGraphics();
        iconGraphics.drawImage(icon, 0, 0, null);
        iconGraphics.dispose();
        for (int x = 0; x < highlightedIcon.getWidth(); x++) {
            for (int y = 0; y < highlightedIcon.getHeight(); y++) {
                if (new Color(highlightedIcon.getRGB(x, y)).equals(Color.WHITE)) {
                    highlightedIcon.setRGB(x, y, Color.GREEN.getRGB());
                }
            }
        }
    }
    
    /**
     * Generates the sub Pieces for this Piece.
     */
    private void generateSubPieces() {
        if (sizeX == 1 && sizeY == 1) {
            subPieces[0][0] = this;
            return;
        }
        
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Piece subPiece = new Piece();
                subPiece.icon = this.icon.getSubimage(x * PIECE_SIZE, y * PIECE_SIZE, PIECE_SIZE, PIECE_SIZE);
                subPiece.generateHighlightedIcon();
                subPiece.name = this.name + ((x > 0 || y > 0) ? (":" + x + ":" + y) : "");
                subPiece.sizeX = 1;
                subPiece.sizeY = 1;
                if (this.replaceForPlayer != null) {
                    subPiece.replaceForPlayer = this.replaceForPlayer.subPieces[x][y];
                }
                subPieces[x][y] = subPiece;
                subPiece.generateSubPieces();
                subPiece.parentPiece = this;
            }
        }
    }
    
}
