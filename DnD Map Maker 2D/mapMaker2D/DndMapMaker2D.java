/*
 * File:    DndMapMaker2D.java
 * Package: mapMaker2D
 * Author:  Zachary Gill
 */

package mapMaker2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.TitledBorder;

import graphy.camera.Camera;
import graphy.main.Environment;
import graphy.math.vector.Vector;
import graphy.objects.base.Scene;
import graphy.objects.complex.ImageSquare;

/**
 * Allows the creation of 2D DnD Maps.
 */
public class DndMapMaker2D extends Scene {
    
    //Constants
    
    /**
     * The size of the map.
     */
    public static final Vector MAP_DIM = new Vector(250, 250);
    
    /**
     * The render size of each square of the map.
     */
    public static final double PIECE_SIZE = 0.5;
    
    
    //Static Fields
    
    /**
     * The list of available pieces for the map.
     */
    public static final Map<String, Piece> pieces = new LinkedHashMap<>();
    
    static {
        loadPieces();
    }
    
    
    //Fields
    
    /**
     * The layout of the map.
     */
    private final Piece[][] map = new Piece[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
    
    /**
     * The map squares of the map.
     */
    private final ImageSquare[][] mapSquares = new ImageSquare[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
    
    /**
     * The selected piece.
     */
    private Piece selectedPiece = null;
    
    /**
     * The selected labels for pieces.
     */
    private final Map<Piece, JLabel> selectedLabels = new HashMap<>();
    
    
    //Main Method
    
    /**
     * The main method for the DnD Make Maker 2D scene.
     *
     * @param args The arguments to the main method.
     * @throws Exception When the Scene class cannot be constructed.
     */
    public static void main(String[] args) throws Exception {
        runScene(DndMapMaker2D.class);
    }
    
    
    //Constructors
    
    /**
     * Constructor for a DnD Make Maker 2D scene.
     *
     * @param environment The Environment to render the DnD Make Maker 2D in.
     */
    public DndMapMaker2D(Environment environment) {
        super(environment);
    }
    
    
    //Methods
    
    /**
     * Calculates the components that compose the DnD Map Maker 2D.
     */
    @Override
    public void calculate() {
        for (int x = 0; x < MAP_DIM.getX(); x++) {
            for (int y = 0; y < MAP_DIM.getY(); y++) {
                ImageSquare square = new ImageSquare(Color.WHITE, new Vector((x - (MAP_DIM.getX() / 2)) * PIECE_SIZE, (y - (MAP_DIM.getY() / 2)) * PIECE_SIZE, 0), PIECE_SIZE);
                square.addFrame(Color.BLACK);
                registerComponent(square);
                mapSquares[x][y] = square;
            }
        }
    }
    
    /**
     * Sets up components for the DnD Map Maker 2D scene.
     */
    @Override
    public void initComponents() {
        environment.setBackground(Color.BLACK);
        
        int width = Environment.screenX;
        int height = Environment.screenY;
        environment.setSceneSize((int) (width * 0.74), (int) (height * 0.94));
        
        environment.frame.getContentPane().remove(environment.renderPanel);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, (int) (width * 0.015), 0, (int) (width * 0.015));
        environment.frame.getContentPane().add(environment.renderPanel, constraints);
        
        JPanel sidePane = new JPanel();
        sidePane.setSize(new Dimension((int) (width * 0.2), (int) (height * 0.94)));
        sidePane.setPreferredSize(sidePane.getSize());
        sidePane.setLayout(new GridBagLayout());
        constraints.gridx = 1;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(0, 0, 0, (int) (width * 0.015));
        environment.frame.getContentPane().add(sidePane, constraints);
        
        JPanel selectorPanel = new JPanel();
        selectorPanel.setSize(new Dimension((int) (width * 0.1925), 100 * pieces.size() + 50));
        selectorPanel.setPreferredSize(selectorPanel.getSize());
        selectorPanel.setLayout(new GridBagLayout());
        selectorPanel.setBorder(BorderFactory.createEtchedBorder());
        
        GridBagConstraints selectorConstraints = new GridBagConstraints();
        selectorConstraints.gridy = -1;
        for (Map.Entry<String, Piece> piece : pieces.entrySet()) {
            JPanel piecePanel = new JPanel();
            piecePanel.setSize(new Dimension((int) (width * 0.19), 100));
            piecePanel.setPreferredSize(piecePanel.getSize());
            piecePanel.setLayout(new GridBagLayout());
            selectorConstraints.gridy++;
            selectorPanel.add(piecePanel, selectorConstraints);
            
            GridBagConstraints pieceConstraints = new GridBagConstraints();
            JLabel pieceSelectedLabel = new JLabel(" ");
            selectedLabels.put(piece.getValue(), pieceSelectedLabel);
            pieceSelectedLabel.setSize(new Dimension(20, 20));
            pieceSelectedLabel.setPreferredSize(pieceSelectedLabel.getSize());
            pieceConstraints.gridx = 0;
            pieceConstraints.anchor = GridBagConstraints.LINE_START;
            pieceConstraints.weightx = 1.0;
            piecePanel.add(pieceSelectedLabel, pieceConstraints);
            
            JButton pieceButton = new JButton(new ImageIcon(piece.getValue().icon));
            pieceButton.setSize(new Dimension(100, 100));
            pieceButton.setPreferredSize(pieceButton.getSize());
            pieceButton.setBorder(BorderFactory.createEmptyBorder());
            pieceButton.setContentAreaFilled(false);
            pieceButton.addActionListener(e -> {
                if (selectedPiece != null) {
                    selectedLabels.get(selectedPiece).setText(" ");
                }
                selectedPiece = pieces.get(piece.getKey());
                selectedLabels.get(selectedPiece).setText("X");
            });
            pieceConstraints.gridx = 1;
            piecePanel.add(pieceButton, pieceConstraints);
            
            JLabel pieceLabel = new JLabel(piece.getKey());
            pieceLabel.setSize(new Dimension(150, 100));
            pieceLabel.setPreferredSize(pieceLabel.getSize());
            pieceConstraints.gridx = 2;
            piecePanel.add(pieceLabel, pieceConstraints);
        }
        
        JScrollPane scrollPane = new JScrollPane(selectorPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(25, 0));
        scrollPane.setSize(new Dimension((int) (width * 0.195), (int) (height * 0.8825)));
        scrollPane.setPreferredSize(scrollPane.getSize());
        scrollPane.setBorder(new TitledBorder("Map Pieces"));
        scrollPane.setLayout(new ScrollPaneLayout());
        GridBagConstraints subConstraints = new GridBagConstraints();
        subConstraints.insets = new Insets(0, 0, (int) (height * 0.0025), 0);
        sidePane.add(scrollPane, subConstraints);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(new Dimension((int) (width * 0.195), (int) (height * 0.05)));
        buttonPanel.setPreferredSize(buttonPanel.getSize());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.setLayout(new GridBagLayout());
        subConstraints.gridy = 1;
        subConstraints.insets = new Insets(0, 0, 0, 0);
        sidePane.add(buttonPanel, subConstraints);
        
        JButton saveButton = new JButton();
        saveButton.setText("Save");
        saveButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter the name of the Map to Save:");
            if (name != null) {
                saveState(name);
            }
        });
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.insets = new Insets(0, (int) (width * 0.005), 0, (int) (width * 0.005));
        buttonPanel.add(saveButton, buttonConstraints);
        
        JButton exportButton = new JButton();
        exportButton.setText("Export");
        exportButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter the name of the Map to Export:");
            if (name != null) {
                exportState(name);
            }
        });
        buttonConstraints.gridx = 1;
        buttonPanel.add(exportButton, buttonConstraints);
        
        JButton loadButton = new JButton();
        loadButton.setText("Load");
        loadButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter the name of the Map to Load:");
            if (name != null) {
                loadState(name);
            }
        });
        buttonConstraints.gridx = 2;
        buttonPanel.add(loadButton, buttonConstraints);
        
        environment.frame.pack();
    }
    
    /**
     * Sets up cameras for the DnD Map Maker 2D scene.
     */
    @Override
    public void setupCameras() {
        Camera camera = new Camera(this, false, true);
        camera.setPanMode(true);
        camera.setRho(10);
    }
    
    /**
     * Sets up controls for the DnD Map Maker 2D scene.
     */
    @Override
    public void setupControls() {
        environment.renderPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            
            @SuppressWarnings("ManualArrayCopy")
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (selectedPiece == null) {
                        return;
                    }
                    
                    boolean hit = false;
                    for (int x = 0; x < mapSquares.length; x++) {
                        for (int y = 0; y < mapSquares[0].length; y++) {
                            ImageSquare imageSquare = mapSquares[x][y];
                            if (imageSquare != null && imageSquare.isRendered()) {
                                List<Vector> prepared = imageSquare.getPrepared();
                                if (prepared.size() == 4 &&
                                        e.getX() > prepared.get(0).getX() &&
                                        e.getX() < prepared.get(1).getX() &&
                                        e.getY() > prepared.get(0).getY() &&
                                        e.getY() < prepared.get(3).getY()) {
                                    
                                    if (((x + selectedPiece.sizeX) <= mapSquares.length) &&
                                            ((y + selectedPiece.sizeY) <= mapSquares[0].length)) {
                                        if (map[x][y] != null) {
                                            Piece piece = (map[x][y].parentPiece == null) ? map[x][y] : map[x][y].parentPiece;
                                            int xOffset = 0;
                                            int yOffset = 0;
                                            if (map[x][y].parentPiece != null) {
                                                String[] nameParts = map[x][y].name.split(":");
                                                if (nameParts.length == 3) {
                                                    xOffset = -Integer.parseInt(nameParts[1]);
                                                    yOffset = -Integer.parseInt(nameParts[2]);
                                                }
                                            }
                                            
                                            for (int i = 0; i < piece.sizeX; i++) {
                                                for (int j = 0; j < piece.sizeY; j++) {
                                                    map[x + xOffset + i][y + yOffset + j] = null;
                                                    mapSquares[x + xOffset + i][y + yOffset + j].setImage(null);
                                                }
                                            }
                                        }
                                        
                                        if (selectedPiece.name.equalsIgnoreCase("Nothing")) {
                                            imageSquare.setImage(null);
                                        } else {
                                            for (int i = 0; i < selectedPiece.sizeX; i++) {
                                                for (int j = 0; j < selectedPiece.sizeY; j++) {
                                                    map[x + i][y + j] = selectedPiece.subPieces[i][j];
                                                }
                                            }
                                        }
                                    }
                                    
                                    hit = true;
                                    break;
                                }
                            }
                        }
                        if (hit) {
                            break;
                        }
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
            
        });
        
        environment.renderPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                for (int x = 0; x < mapSquares.length; x++) {
                    for (int y = 0; y < mapSquares[0].length; y++) {
                        ImageSquare imageSquare = mapSquares[x][y];
                        if (imageSquare != null && imageSquare.isRendered()) {
                            List<Vector> prepared = imageSquare.getPrepared();
                            if (prepared.size() == 4 &&
                                    e.getX() > prepared.get(0).getX() &&
                                    e.getX() < prepared.get(1).getX() &&
                                    e.getY() > prepared.get(0).getY() &&
                                    e.getY() < prepared.get(3).getY()) {
                                imageSquare.setColor(Color.GREEN);
                                if (map[x][y] != null) {
                                    imageSquare.setImage(map[x][y].highlightedIcon);
                                }
                            } else {
                                imageSquare.setColor(Color.WHITE);
                                if (map[x][y] != null) {
                                    imageSquare.setImage(map[x][y].icon);
                                }
                            }
                        }
                    }
                }
            }
            
        });
    }
    
    /**
     * Saves the state of the map layout.
     *
     * @param mapName The name of the map.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveState(String mapName) {
        File saveDirectory = new File("saves");
        if (!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }
        File save = new File(saveDirectory, mapName + ".save");
        
        StringBuilder state = new StringBuilder();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y] != null && pieces.containsKey(map[x][y].name)) {
                    state.append(state.length() > 0 ? "," : "").append(x).append(":").append(y).append(":").append(map[x][y].name);
                }
            }
        }
        try {
            Files.write(save.toPath(), state.toString().getBytes());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + mapName + " could not be saved!");
        }
    }
    
    /**
     * Loads the state of the map layout.
     *
     * @param mapName The name of the map.
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void loadState(String mapName) {
        saveState(UUID.randomUUID().toString());
        
        File saveDirectory = new File("saves");
        if (!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }
        File save = new File(saveDirectory, mapName + ".save");
        
        if (!save.exists()) {
            JOptionPane.showMessageDialog(null, "Error: " + mapName + " save does not exist!");
            return;
        }
        
        String state;
        try {
            state = new String(Files.readAllBytes(save.toPath()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + mapName + " could not be loaded!");
            return;
        }
        String[] mapPieces = state.split(",");
        
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = null;
            }
        }
        for (String mapPiece : mapPieces) {
            String[] mapPieceData = mapPiece.split(":");
            int x = Integer.parseInt(mapPieceData[0]);
            int y = Integer.parseInt(mapPieceData[1]);
            Piece piece = pieces.get(mapPieceData[2]);
            
            for (int i = 0; i < piece.sizeX; i++) {
                for (int j = 0; j < piece.sizeY; j++) {
                    map[x + i][y + j] = piece.subPieces[i][j];
                    mapSquares[x + i][y + j].setImage(piece.subPieces[i][j].icon);
                }
            }
        }
    }
    
    /**
     * Exports the state of the map layout.
     *
     * @param mapName The name of the map.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void exportState(String mapName) {
        File outputDirectory = new File("output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y] != null) {
                    minX = Math.min(x, minX);
                    minY = Math.min(y, minY);
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                }
            }
        }
        
        BufferedImage dmMap = new BufferedImage((maxX - minX + 1) * Piece.PIECE_SIZE, (maxY - minY + 1) * Piece.PIECE_SIZE, BufferedImage.TYPE_INT_RGB);
        BufferedImage playerMap = new BufferedImage((maxX - minX + 1) * Piece.PIECE_SIZE, (maxY - minY + 1) * Piece.PIECE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics dmMapGraphics = dmMap.getGraphics();
        Graphics playerMapGraphics = playerMap.getGraphics();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Piece piece = (map[x][y] == null) ? pieces.get("Space") : map[x][y];
                BufferedImage dmIcon = piece.icon;
                BufferedImage playerIcon = (piece.replaceForPlayer == null) ? dmIcon : piece.replaceForPlayer.icon;
                dmMapGraphics.drawImage(dmIcon, (x - minX) * Piece.PIECE_SIZE, (y - minY) * Piece.PIECE_SIZE, null);
                playerMapGraphics.drawImage(playerIcon, (x - minX) * Piece.PIECE_SIZE, (y - minY) * Piece.PIECE_SIZE, null);
            }
        }
        
        File dmOutput = new File(outputDirectory, mapName + "-dm.png");
        File playerOutput = new File(outputDirectory, mapName + "-player.png");
        try {
            ImageIO.write(dmMap, "png", dmOutput);
            ImageIO.write(playerMap, "png", playerOutput);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + mapName + " could not be exported!");
        }
    }
    
    
    //Static Methods
    
    /**
     * Loads the available map pieces.
     */
    private static void loadPieces() {
        File resourceDir = new File("resource/mapMaker2D");
        
        pieces.put("Nothing", new Piece(new File(resourceDir, "nothing.png"), "Nothing"));
        pieces.put("Space", new Piece(new File(resourceDir, "space.png"), "Space"));
        pieces.put("Border", new Piece(new File(resourceDir, "border.png"), "Border"));
        pieces.put("Doorway Horizontal", new Piece(new File(resourceDir, "doorwayHorizontal.png"), "Doorway Horizontal"));
        pieces.put("Doorway Vertical", new Piece(new File(resourceDir, "doorwayVertical.png"), "Doorway Vertical"));
        pieces.put("Door Horizontal", new Piece(new File(resourceDir, "doorHorizontal.png"), "Door Horizontal"));
        pieces.put("Door Vertical", new Piece(new File(resourceDir, "doorVertical.png"), "Door Vertical"));
        pieces.put("Locked Door Horizontal", new Piece(new File(resourceDir, "lockedDoorHorizontal.png"), "Locked Door Horizontal", pieces.get("Door Horizontal")));
        pieces.put("Locked Door Vertical", new Piece(new File(resourceDir, "lockedDoorVertical.png"), "Locked Door Vertical", pieces.get("Door Vertical")));
        pieces.put("Trapped Door Horizontal", new Piece(new File(resourceDir, "trappedDoorHorizontal.png"), "Trapped Door Horizontal", pieces.get("Door Horizontal")));
        pieces.put("Trapped Door Vertical", new Piece(new File(resourceDir, "trappedDoorVertical.png"), "Trapped Door Vertical", pieces.get("Door Vertical")));
        pieces.put("Locked Trapped Door Horizontal", new Piece(new File(resourceDir, "lockedTrappedDoorHorizontal.png"), "Locked Trapped Door Horizontal", pieces.get("Door Horizontal")));
        pieces.put("Locked Trapped Door Vertical", new Piece(new File(resourceDir, "lockedTrappedDoorVertical.png"), "Locked Trapped Door Vertical", pieces.get("Door Vertical")));
        pieces.put("Window Horizontal", new Piece(new File(resourceDir, "windowHorizontal.png"), "Window Horizontal"));
        pieces.put("Window Vertical", new Piece(new File(resourceDir, "windowVertical.png"), "Window Vertical"));
        pieces.put("Down Stairs Up", new Piece(new File(resourceDir, "downStairsUp.png"), "Down Stairs Up"));
        pieces.put("Down Stairs Down", new Piece(new File(resourceDir, "downStairsDown.png"), "Down Stairs Down"));
        pieces.put("Down Stairs Left", new Piece(new File(resourceDir, "downStairsLeft.png"), "Down Stairs Left"));
        pieces.put("Down Stairs Right", new Piece(new File(resourceDir, "downStairsRight.png"), "Down Stairs Right"));
        pieces.put("Up Stairs Up", new Piece(new File(resourceDir, "upStairsUp.png"), "Up Stairs Up"));
        pieces.put("Up Stairs Down", new Piece(new File(resourceDir, "upStairsDown.png"), "Up Stairs Down"));
        pieces.put("Up Stairs Left", new Piece(new File(resourceDir, "upStairsLeft.png"), "Up Stairs Left"));
        pieces.put("Up Stairs Right", new Piece(new File(resourceDir, "upStairsRight.png"), "Up Stairs Right"));
    }
    
}
