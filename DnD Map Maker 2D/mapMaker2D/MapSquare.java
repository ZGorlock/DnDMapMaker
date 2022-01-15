/*
 * File:    MapSquare.java
 * Package: mapMaker2D
 * Author:  Zachary Gill
 */

package mapMaker2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import graphy.math.vector.Vector;
import graphy.objects.complex.ImageSquare;

public class MapSquare extends ImageSquare {
    
    //Fields
    
    /**
     * The label to print on the Map Square.
     */
    protected String label;
    
    /**
     * The note of the Map Square.
     */
    protected String note;
    
    
    //Constructors
    
    /**
     * The constructor for a Map Square.
     *
     * @param color The color of the Map Square.
     * @param v1    The first point of the Map Square.
     * @param side  The side length of the Map Square.
     */
    public MapSquare(Color color, Vector v1, double side) {
        super(null, color, v1, side);
    }
    
    
    //Methods
    
    /**
     * Renders the Map Square on the screen.
     *
     * @param g2 The 2D Graphics entity.
     */
    @Override
    public void render(Graphics2D g2) {
        super.render(g2);
        if ((note != null) && !note.isEmpty()) {
            g2.setColor(Color.RED);
            g2.fillRect((int) (prepared.get(0).getX() + ((prepared.get(1).getX() - prepared.get(0).getX()) * 0.75)),
                    (int) (prepared.get(1).getY() + ((prepared.get(2).getY() - prepared.get(1).getY()) * 0.15)),
                    (int) ((prepared.get(1).getX() - prepared.get(0).getX()) * 0.15), (int) ((prepared.get(2).getY() - prepared.get(1).getY()) * 0.15));
            g2.setColor(getColor());
        }
        if (label != null) {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Consolas", Font.ITALIC, 20));
            g2.drawString(label, (int) (prepared.get(0).getX() + ((prepared.get(1).getX() - prepared.get(0).getX()) * 0.5) - 7),
                    (int) (prepared.get(1).getY() + ((prepared.get(2).getY() - prepared.get(1).getY()) * 0.5)) + 10);
            g2.setColor(getColor());
        }
    }
    
    
    //Getters
    
    /**
     * Returns the label to print on the Map Square.
     *
     * @return The label to print on the Map Square.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Returns the note of the Map Square.
     *
     * @return The note of the Map Square.
     */
    public String getNote() {
        return note;
    }
    
    
    //Setters
    
    /**
     * Sets the label to print on the Map Square.
     *
     * @param label The label.
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Sets the note of the Map Square.
     *
     * @param note The note.
     */
    public void setNote(String note) {
        this.note = note;
    }
    
}
