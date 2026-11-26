import java.awt.image.BufferedImage;
import java.util.HashSet;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;

// panel running and drawing the platformer
public class LevelDraw extends JPanel implements KeyListener {
    // panel properties
    private int WIDTH, HEIGHT, GRID_WIDTH, GRID_HEIGHT;
    // size of squares on screen
    private static final int SQUARE_SIZE = 20;
    // reference to object storing level contents
    private LevelContents contents;
    // keys currently held
    private HashSet<Character> keySet;
    // current active objects
    private HashSet<ActiveObject> activeSet;
    // grid of level contents
    private String[][] levelGrid;
    // used for debugging performance times
    private long firstTime;
    // frame storing this panel
    private JFrame frame;
    // timer running main platformer loop
    private Timer timer;
    public LevelDraw(int width, int height, int gridWidth, int gridHeight, LevelContents contents) {
        // open new window for platformer
        frame = new JFrame("Platformer");
        // correct for frame size
        int frameMarginX = 16;
        int frameMarginY = 43;
        frame.setSize( new Dimension(width + frameMarginX, height + frameMarginY));
        frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.add(this);

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        
        // read in panel properties
        this.WIDTH = width;
        this.HEIGHT = height;
        this.GRID_WIDTH = gridWidth;
        this.GRID_HEIGHT = gridHeight;

        // read in level contents
        this.contents = contents;
        keySet = new HashSet<Character>();
        activeSet = contents.getActiveSet();
        levelGrid = contents.getLevelGrid();

        firstTime = System.currentTimeMillis();
        
        setBackground(Color.white);
        Dimension dim = new Dimension(WIDTH,HEIGHT);
        setPreferredSize(dim);
    }
    
    // initiate main loop for level
    public void startTimer() {
        timer = new Timer(15, new ActionListener() {
            // triggered by the timer roughly every 17 milliseconds
            // timer only set for 15 milliseconds because code runs for roughly 2 milliseconds
            public void actionPerformed(ActionEvent event) {
                
                // for debugging
                //System.out.println(System.currentTimeMillis() - firstTime - 15);
                firstTime = System.currentTimeMillis();
                // update active objects
                for(ActiveObject object: activeSet) {
                    object.update(contents.getLevelGrid());
                }   
                if(!isShowing()) {
                    timer.stop();
                }
                
                repaint();
            }
        });

        // start the animation
        timer.start();
    }

    // make note of keys pressed and released
    public void keyPressed(KeyEvent key) {
        keySet.add(key.getKeyChar());
    }
    public void keyReleased(KeyEvent key) {
        keySet.remove(key.getKeyChar());
    }
    public void keyTyped(KeyEvent key) {}

    public HashSet<Character> getKeySet() {
        return keySet;
    }


    // draw level
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBlocks(levelGrid, g);
        for(ActiveObject object: activeSet) {
            drawActiveObject(object, g);
        }
        drawCoinCount(contents.getPlayer(),g);
    }
    // draw grid with all blocks
    public void drawBlocks(String[][] grid, Graphics g) {
        // draw white background
        g.setColor(Color.WHITE);
        g.fillRect(0,0, WIDTH, HEIGHT);
        // run through each cell of grid
        for( int f = 0; f < GRID_HEIGHT; f++) {
            for( int e = 0; e < GRID_WIDTH; e++) {
                // draw spike as red trianle
                if(grid[f][e].equals("S")) {
                    g.setColor(Color.RED);
                    int[] xPoints = {(int)((e + 0.5) * SQUARE_SIZE),e * SQUARE_SIZE,(e + 1) * SQUARE_SIZE};
                    int[] yPoints = {f * SQUARE_SIZE,(f + 1) * SQUARE_SIZE,(f + 1) * SQUARE_SIZE};
                    g.fillPolygon(xPoints, yPoints, 3);
                }  else if(grid[f][e].equals("C")) { // coins as yellow circle
                    g.setColor(Color.YELLOW);
                    g.fillOval(e * SQUARE_SIZE, f * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                } else if(!grid[f][e].equals("A") && !grid[f][e].equals("P")) {
                    // draw all other non-air and non-player cells as colored rectangles
                    if(grid[f][e].equals("B")) { // block - black
                        g.setColor(Color.BLACK);
                    } else { // level end - green
                        g.setColor(Color.GREEN);
                    }
                    g.fillRect(e * SQUARE_SIZE, f * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
        }
    }
    // draw active object
    public void drawActiveObject(ActiveObject target,Graphics g) {
        // draw object with properties
        int objectX = target.getX();
        int objectY = target.getY();
        g.setColor(target.getColor());
        g.fillRect(objectX,objectY,SQUARE_SIZE,SQUARE_SIZE);

        // draw player level complete text if necessary
        if(target.getType().equals("P")) {
            if(((Player)target).getLevelDone()) {
                String text = "Level Complete!";
                // how far left of player x to draw string
                double stringXOffset = ((g.getFontMetrics().stringWidth(text)) * 0.4);
                g.drawString(text, (int)(objectX - stringXOffset),(int)(objectY - g.getFont().getSize2D()));
            }
        }
    }
    // Draw coin count collected by player in top left corner
    public void drawCoinCount(Player player, Graphics g) {
        String text = "Coins: " + player.getCoinCount();
        // where on screen to draw string
        int coinStringX = 5;
        int coinStringY = 15;
        g.drawString(text, coinStringX, coinStringY);
    }
}
