import java.awt.image.BufferedImage;
import java.util.HashSet;

import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;


public class GridPanel extends JPanel implements MouseListener, MouseMotionListener {
    // panel data
    private int width, height, trueHeight, trueWidth, gridWidth, gridHeight, squareSize;
    // Contents of the current drawn out level
    private String[][] levelGrid;
    // Current block being placed (current paint)
    private String currentBlock;
    public GridPanel(int width, int height, int gridWidth, int gridHeight) {
        // listen for mouse interaction
        addMouseListener(this);
        addMouseMotionListener(this);
        
        // current height and width of grid being drawn
        this.width = width;
        this.height = height;
        // essentially the preferred width and height of the grid, handled manually
        trueHeight = this.height;
        trueWidth = this.width;
        // number of grid cells in height and width
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        // size of each cell in pixels as currently drawn
        squareSize = width/gridWidth;
        // initiate empty level grid
        levelGrid = new String[gridHeight][gridWidth];
        for( int f = 0; f < gridHeight; f++) {
            for( int e = 0; e < gridWidth; e++) {
                levelGrid[f][e] = "A";
            }
        }

        setBackground(Color.white);
        setPreferredSize(new Dimension(width,height));
        // default object being drawn is block
        currentBlock = "B";
    }


    // draw grid of blocks
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBlocks(levelGrid, g);
    }


    public int getGridWidth() {
        return gridWidth;
    }
    public int getGridHeight() {
        return gridHeight;
    }

    // resize to new number of cells in width
    public void setGridWidth(int newWidth) {
        gridWidth = newWidth;
        // resize pixel dimensions
        fitGridToSpace();
        // copy over to newly resized level grid
        String[][] newLevelGrid = new String[gridHeight][gridWidth];
        for( int f = 0; f < gridHeight; f++) {
            for( int e = 0; e < gridWidth; e++) {
                if(f < levelGrid.length && e < levelGrid[0].length) {
                    newLevelGrid[f][e] = levelGrid[f][e];
                } else {
                    newLevelGrid[f][e] = "A";
                }
            }
        }
        levelGrid = newLevelGrid;

        repaint();
    }
    // resize to new number of cells in height
    public void setGridHeight(int newHeight) {
        gridHeight = newHeight;
        // resize cell size
        fitGridToSpace();
        // copy over to newly resized level grid
        String[][] newLevelGrid = new String[gridHeight][gridWidth];
        for( int f = 0; f < gridHeight; f++) {
            for( int e = 0; e < gridWidth; e++) {
                if(f < levelGrid.length && e < levelGrid[0].length) {
                    newLevelGrid[f][e] = levelGrid[f][e];
                } else {
                    newLevelGrid[f][e] = "A";
                }
            }
        }
        levelGrid = newLevelGrid;

        repaint();
    }
    public void fitGridToSpace() {
        if(gridHeight > gridWidth) {
            squareSize = (int)(trueHeight/gridHeight);
        } else {
            squareSize = (int)(trueWidth/gridWidth);
        }
        height = squareSize * gridHeight;
        width = squareSize * gridWidth;
    }

    // change which block is currently being drawn
    public void setBlock(String block) {
        currentBlock = block;
    }
    // convert given pixel coordinates to grid coordinates
    // set that grid position to a new value
    private void gridPosSet(double x, double y, String newValue) {
        levelGrid[(int)(y/squareSize)][(int)(x/squareSize)] = newValue;
    }

    // checks if current level grid contains a player
    public boolean levelHasPlayer() {
        for( int f = 0; f < gridHeight; f++) {
            for( int e = 0; e < gridWidth; e++) {
                if(levelGrid[f][e].equals("P")) {
                    return true;
                }
            }
        }
        return false;
    }

    // when square is clicked
    public void handleMouseEvent(MouseEvent event) {
        if(currentBlock.equals("P")) {
            for( int f = 0; f < gridHeight; f++) {
                for( int e = 0; e < gridWidth; e++) {
                    if(levelGrid[f][e].equals("P")) {
                        levelGrid[f][e] = "A";
                    }
                }
            }
        }
        Point point = event.getPoint();
        int pointX = (int)point.getX();
        int pointY = (int)point.getY();
        if(pointX > 0 && pointX < width && pointY > 0 && pointY < height) {
            gridPosSet(pointX, pointY, currentBlock);
        }
        repaint();
    }
    public void mousePressed(MouseEvent event) {
        handleMouseEvent(event);
    }

    public void mouseDragged(MouseEvent event) {
        handleMouseEvent(event);
    }
    // unsused mouse interactions
    public void mouseClicked(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}
    public void mouseMoved(MouseEvent event) {}




    // draw out grid
    public void drawBlocks(String[][] grid, Graphics g) {
        // draw white background
        g.setColor(Color.WHITE);
        g.fillRect(0,0, width, height);
        // run through each cell of grid
        for( int f = 0; f < gridHeight; f++) {
            for( int e = 0; e < gridWidth; e++) {
                // draw spike as red trianle
                if(grid[f][e].equals("S")) {
                    g.setColor(Color.RED);
                    int[] xPoints = {(int)((e + 0.5) * squareSize),e * squareSize,(e + 1) * squareSize};
                    int[] yPoints = {f * squareSize,(f + 1) * squareSize,(f + 1) * squareSize};
                    g.fillPolygon(xPoints, yPoints, 3);
                }  else if(grid[f][e].equals("C")) { // coins as yellow circle
                    g.setColor(Color.YELLOW);
                    g.fillOval(e * squareSize, f * squareSize, squareSize, squareSize);
                } else if(!grid[f][e].equals("A")) { // draw all other non-air cells as colored rectangles
                    if(grid[f][e].equals("B")) { // block - black
                        g.setColor(Color.BLACK);
                    } else if(grid[f][e].equals("P")) { // player - blue
                        g.setColor(Color.BLUE);
                    } else if(grid[f][e].equals("E")) { // level end - green
                        g.setColor(Color.GREEN);
                    }
                    g.fillRect(e * squareSize, f * squareSize, squareSize, squareSize);
                }
            }
        }
        // draw grid lines
        g.setColor(Color.BLACK);
        for( int f = 1; f < gridHeight; f++) {
            g.drawLine(0,f * squareSize,width,f * squareSize);
        }
        for( int e = 1; e < gridWidth; e++) {
            g.drawLine(e * squareSize,0,e * squareSize,height);
        }
    }
    
    public String[][] getLevelGrid() {
        return levelGrid;
    }
    public void setLevelGrid(String[][] grid) {
        levelGrid = grid;
    }
}