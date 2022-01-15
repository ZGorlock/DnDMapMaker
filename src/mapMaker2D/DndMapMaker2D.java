/*
 * File:    DndMapMaker2D.java
 * Package: mapMaker2D
 * Author:  Zachary Gill
 */

package mapMaker2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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
import mapParser.BlackSpaceReducer;
import mapParser.DndMapParser;

/**
 * Allows the creation of 2D DnD Maps.
 */
public class DndMapMaker2D extends Scene {
    
    //Constants
    
    /**
     * The size of the map.
     */
    public static final Vector MAP_DIM = new Vector(100, 100);
    
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
    private Piece[][] map = new Piece[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
    
    /**
     * The map squares of the map.
     */
    private MapSquare[][] mapSquares = new MapSquare[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
    
    /**
     * The labels of the map squares.
     */
    private String[][] labels = new String[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
    
    /**
     * The notes of the map squares.
     */
    private String[][] notes = new String[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
    
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
        DndMapMaker2D.environment = environment;
    }
    
    private static Environment environment;
    
    
    //Methods
    
    /**
     * Calculates the components that compose the DnD Map Maker 2D.
     */
    @Override
    public void calculate() {
        for (int x = 0; x < MAP_DIM.getX(); x++) {
            for (int y = 0; y < MAP_DIM.getY(); y++) {
                MapSquare square = new MapSquare(Color.WHITE, new Vector((x - (MAP_DIM.getX() / 2)) * PIECE_SIZE, (y - (MAP_DIM.getY() / 2)) * PIECE_SIZE, 0), PIECE_SIZE);
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
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
                    boolean ctrl = (e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK;
                    if ((selectedPiece == null) && !ctrl) {
                        return;
                    }
                    
                    boolean hit = false;
                    for (int x = 0; x < mapSquares.length; x++) {
                        for (int y = 0; y < mapSquares[0].length; y++) {
                            MapSquare mapSquare = mapSquares[x][y];
                            if (mapSquare != null && mapSquare.isRendered()) {
                                List<Vector> prepared = mapSquare.getPrepared();
                                if (prepared.size() == 4 &&
                                        e.getX() > prepared.get(0).getX() &&
                                        e.getX() < prepared.get(1).getX() &&
                                        e.getY() > prepared.get(0).getY() &&
                                        e.getY() < prepared.get(3).getY()) {
                                    
                                    if (ctrl) {
                                        String label = JOptionPane.showInputDialog("Label:");
                                        if (label != null) {
                                            labels[x][y] = label;
                                            mapSquare.setLabel(label.replaceAll("[:,;]", ""));
                                            String note = JOptionPane.showInputDialog("Note:");
                                            if (note != null) {
                                                notes[x][y] = note;
                                                mapSquare.setNote(note.replaceAll("[:,;]", ""));
                                            }
                                        }
                                        hit = true;
                                        break;
                                    }
                                    
                                    if (((x + selectedPiece.sizeX) <= mapSquares.length) &&
                                            ((y + selectedPiece.sizeY) <= mapSquares[0].length)) {
                                        
                                        boolean overlap = false;
                                        for (int i = x; i < (x + selectedPiece.sizeX); i++) {
                                            for (int j = y; j < (y + selectedPiece.sizeY); j++) {
                                                if (i == x && j == y) {
                                                    continue;
                                                }
                                                if (map[i][j] != null) {
                                                    overlap = true;
                                                    break;
                                                }
                                            }
                                            if (overlap) {
                                                break;
                                            }
                                        }
                                        
                                        if (!overlap) {
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
                                                mapSquare.setImage(null);
                                            } else {
                                                for (int i = 0; i < selectedPiece.sizeX; i++) {
                                                    for (int j = 0; j < selectedPiece.sizeY; j++) {
                                                        map[x + i][y + j] = selectedPiece.subPieces[i][j];
                                                    }
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
            Timer noteTimer = null;
            
            @Override
            public void mouseDragged(MouseEvent e) {
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean ctrl = (e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK;
                if (noteTimer != null) {
                    noteTimer.cancel();
                }
                
                for (int x = 0; x < mapSquares.length; x++) {
                    for (int y = 0; y < mapSquares[0].length; y++) {
                        MapSquare mapSquare = mapSquares[x][y];
                        if (mapSquare != null && mapSquare.isRendered()) {
                            List<Vector> prepared = mapSquare.getPrepared();
                            if (prepared.size() == 4 &&
                                    e.getX() > prepared.get(0).getX() &&
                                    e.getX() < prepared.get(1).getX() &&
                                    e.getY() > prepared.get(0).getY() &&
                                    e.getY() < prepared.get(3).getY()) {
                                
                                mapSquare.setColor(Color.GREEN);
                                if (map[x][y] != null) {
                                    mapSquare.setImage(map[x][y].highlightedIcon);
                                }
                                
                                if (ctrl && (mapSquare.note != null) && !mapSquare.note.isEmpty()) {
                                    noteTimer = new Timer();
                                    noteTimer.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            final Graphics2D g2 = (Graphics2D) environment.renderPanel.getGraphics();
                                            Color saveColor = g2.getColor();
                                            g2.setColor(Color.BLACK);
                                            g2.drawString(mapSquare.note, e.getX(), e.getY());
                                            g2.setColor(saveColor);
                                        }
                                    }, 0, 5);
                                }
                                
                            } else {
                                mapSquare.setColor(Color.WHITE);
                                if (map[x][y] != null) {
                                    mapSquare.setImage(map[x][y].icon);
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
        File saveDirectory = new File("SAVE");
        if (!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }
        File save = new File(saveDirectory, mapName + ".save");
        
        StringBuilder state = new StringBuilder();
        state.append(Environment.origin.getX()).append(":").append(Environment.origin.getY()).append(":").append(Environment.origin.getZ()).append(",");
        Vector camera = Camera.getActiveCameraView().getLocation();
        state.append(camera.getX()).append(":").append(camera.getY()).append(":").append(camera.getZ());
        
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                boolean hasPiece = (map[x][y] != null && pieces.containsKey(map[x][y].name));
                boolean hasLabel = labels[x][y] != null;
                boolean hasNotes = notes[x][y] != null;
                if (hasPiece || hasLabel || hasNotes) {
                    state.append(',').append(x).append(':').append(y).append(':')
                            .append(hasPiece ? map[x][y].name : "").append(':')
                            .append(hasLabel ? labels[x][y] : "").append(':')
                            .append(hasNotes ? notes[x][y] : "");
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
        
        File saveDirectory = new File("SAVE");
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
        
        map = new Piece[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
        mapSquares = new MapSquare[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
        labels = new String[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
        notes = new String[(int) MAP_DIM.getX()][(int) MAP_DIM.getY()];
        calculate();
        
        String[] mapPieces = state.split(",");
        
        String[] origin = mapPieces[0].split(":");
        double originX = Double.parseDouble(origin[0]);
        double originY = Double.parseDouble(origin[1]);
        double originZ = Double.parseDouble(origin[2]);
        Environment.origin = new Vector(originX, originY, originZ);
        
        String[] camera = mapPieces[1].split(":");
        double phi = Double.parseDouble(camera[0]);
        double theta = Double.parseDouble(camera[1]);
        double rho = Double.parseDouble(camera[2]);
        Camera.getActiveCameraView().setLocation(phi, theta, rho);
        
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = null;
            }
        }
        for (int p = 2; p < mapPieces.length; p++) {
            String[] mapPieceData = mapPieces[p].split(":", -1);
            int x = Integer.parseInt(mapPieceData[0]);
            int y = Integer.parseInt(mapPieceData[1]);
            Piece piece = mapPieceData[2].isEmpty() ? null : pieces.get(mapPieceData[2]);
            String label = ((mapPieceData.length < 4) || mapPieceData[3].isEmpty()) ? null : mapPieceData[3];
            String note = ((mapPieceData.length < 5) || mapPieceData[4].isEmpty()) ? null : mapPieceData[4];
            
            if (piece != null) {
                for (int i = 0; i < piece.sizeX; i++) {
                    for (int j = 0; j < piece.sizeY; j++) {
                        map[x + i][y + j] = piece.subPieces[i][j];
                        mapSquares[x + i][y + j].setImage(piece.subPieces[i][j].icon);
                    }
                }
            }
            if (label != null) {
                labels[x][y] = label;
                mapSquares[x][y].setLabel(label);
            }
            if (note != null) {
                notes[x][y] = note;
                mapSquares[x][y].setNote(note);
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
        saveState(mapName + "-export");
        
        File outputDirectory = new File("OUTPUT");
        File exportDirectory = new File(outputDirectory, mapName);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
        if (!exportDirectory.exists()) {
            exportDirectory.mkdir();
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
        
        Map<String, String> poi = new LinkedHashMap<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if ((labels[x][y] != null) && !labels[x][y].isEmpty() &&
                        (notes[x][y] != null) && !notes[x][y].isEmpty()) {
                    poi.put(labels[x][y], notes[x][y]);
                }
            }
        }
        
        BufferedImage dmMap = new BufferedImage(((maxX - minX + 1) * Piece.PIECE_SIZE), (((maxY - minY + 1) * Piece.PIECE_SIZE) + ((poi.size() + 1) * 24)), BufferedImage.TYPE_INT_RGB);
        BufferedImage playerMap = new BufferedImage(((maxX - minX + 1) * Piece.PIECE_SIZE), ((maxY - minY + 1) * Piece.PIECE_SIZE), BufferedImage.TYPE_INT_RGB);
        Graphics dmMapGraphics = dmMap.getGraphics();
        Graphics playerMapGraphics = playerMap.getGraphics();
        
        dmMapGraphics.setColor(Color.WHITE);
        playerMapGraphics.setColor(Color.WHITE);
        dmMapGraphics.fillRect(0, 0, dmMap.getWidth(), dmMap.getHeight());
        playerMapGraphics.fillRect(0, 0, playerMap.getWidth(), playerMap.getHeight());
        dmMapGraphics.setColor(Color.DARK_GRAY);
        dmMapGraphics.setFont(new Font("Consolas", Font.ITALIC, 24));
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Piece piece = (map[x][y] == null) ? pieces.get("Space") : map[x][y];
                BufferedImage dmIcon = piece.icon;
                BufferedImage playerIcon = (piece.replaceForPlayer == null) ? dmIcon : piece.replaceForPlayer.icon;
                
                dmMapGraphics.drawImage(dmIcon, (x - minX) * Piece.PIECE_SIZE, (y - minY) * Piece.PIECE_SIZE, null);
                if ((labels[x][y] != null) && !labels[x][y].isEmpty()) {
                    dmMapGraphics.drawString(labels[x][y],
                            ((x - minX) * Piece.PIECE_SIZE + 17), ((y - minY) * Piece.PIECE_SIZE - 17));
                }
                playerMapGraphics.drawImage(playerIcon, (x - minX) * Piece.PIECE_SIZE, (y - minY) * Piece.PIECE_SIZE, null);
            }
        }
        
        if (!poi.isEmpty()) {
            final int bottom = maxY - minY + 1;
            final AtomicInteger poiIndex = new AtomicInteger(1);
            final int labelLength = poi.keySet().stream().mapToInt(String::length).max().orElse(1);
            
            dmMapGraphics.setColor(new Color(32, 32, 32));
            dmMapGraphics.drawLine(0, (bottom * Piece.PIECE_SIZE), dmMap.getWidth(), (bottom * Piece.PIECE_SIZE));
            dmMapGraphics.setColor(Color.DARK_GRAY);
            
            poi.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> {
                char[] spacing = new char[labelLength - e.getKey().length()];
                Arrays.fill(spacing, ' ');
                dmMapGraphics.drawString(e.getKey() + new String(spacing) + " - " + e.getValue(),
                        (Piece.PIECE_SIZE + 7), (bottom * Piece.PIECE_SIZE + (24 * poiIndex.getAndIncrement()) + 10));
            });
        }
        
        File dmOutput = new File(exportDirectory, mapName + " (print).png");
        File playerOutput = new File(exportDirectory, mapName + " (player).png");
        try {
            ImageIO.write(dmMap, "png", dmOutput);
            ImageIO.write(playerMap, "png", playerOutput);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + mapName + " could not be exported!");
        }
        
        DndMapParser.main(new String[] {playerOutput.getAbsolutePath()});
        BlackSpaceReducer.main(new String[] {new File(exportDirectory, "map-Player").getAbsolutePath()});
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
        pieces.put("Path", new Piece(new File(resourceDir, "path.png"), "Path"));
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
        pieces.put("Secret Door Horizontal", new Piece(new File(resourceDir, "secretDoorHorizontal.png"), "Secret Door Horizontal", pieces.get("Border")));
        pieces.put("Secret Door Vertical", new Piece(new File(resourceDir, "secretDoorVertical.png"), "Secret Door Vertical", pieces.get("Border")));
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
        pieces.put("Ramp Up", new Piece(new File(resourceDir, "rampUp.png"), "Ramp Up"));
        pieces.put("Ramp Down", new Piece(new File(resourceDir, "rampDown.png"), "Ramp Down"));
        pieces.put("Ramp Left", new Piece(new File(resourceDir, "rampLeft.png"), "Ramp Left"));
        pieces.put("Ramp Right", new Piece(new File(resourceDir, "rampRight.png"), "Ramp Right"));
    }
    
}
