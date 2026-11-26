import java.awt.Color;
import java.lang.Math.*;
import java.util.HashMap;
import java.util.HashSet;

public class Player extends ActiveObject {
    // instance variables
    private double x, y, ogX, ogY, hsp, vsp, w, h, walkSpeed, jumpForce, grav, terminalVel;
    // is player on the ground
    private boolean grounded = false;
    // has player jumped since the last time space was pressed
    private boolean jumpPressed = false;
    // number of coins collected
    private int coinCount = 0;
    // size of squares on screen
    private static final int SQUARE_SIZE = 20;
    // locally store copy of level grid to check collisions
    private String[][] grid;
    // Each key pressed so far and if it's currently being pressed
    private HashSet<Character> keyHoldHashSet;
    // The panel the platformer is running on
    private LevelDraw drawer;
    // if the player has reached the end of the level
    private boolean levelDone;

    public Player(double x, double y, double w, double h, double walkSpeed, 
            double jumpForce, double grav, double terminalVel) {
        // starting x and y of player, respawn position when player dies/resets
        ogX = x;
        ogY = y;
        // current x and y of player (top left corner of player sprite)
        // in pixels, not in grid coordinates
        this.x = ogX;
        this.y = ogY;
        // width and height of player square (defaults to one square are)
        this.w = w;
        this.h = h;
        // pixels moved by player per frame
        this.walkSpeed = walkSpeed;
        // initial jump velocity in negative pixels per frame
        this.jumpForce = jumpForce;
        // acceleration from gravity (positive)
        this.grav = grav;
        // terminal velocity in pixels per second
        this.terminalVel = terminalVel;

        keyHoldHashSet = new HashSet<Character>();
        levelDone = false;
    }


    // update the player each frame
    public void update(String[][] grid) {
        this.grid = grid;
        this.keyHoldHashSet = drawer.getKeySet();
        // decay on horizontal speed
        hsp *= 0.7;
        // apply force of gravity to vertical speed
        vsp = Math.min(vsp + grav, terminalVel);
        // if space is pressed and player can jump, jump
        if(grounded) {
            if(keyHold(' ')) {
                if(!jumpPressed) {
                    vsp = jumpForce;
                    jumpPressed = true;
                }
            } else {
                jumpPressed = false;
            }
        }

        // bottom wall collision
        // conditions 3 & 4 safeguard for high terminal velocities
        if(vsp > 0 && (gridPosEquals(x + 1, y + h + vsp, "B") || 
            gridPosEquals(x + w - 1, y + h + vsp, "B"))) {
            vsp = ((int)((y + h + vsp)/SQUARE_SIZE) * SQUARE_SIZE) - (y + h);
            grounded = true;
            
        } else {
            grounded = false;
        }
        
        if(vsp < 0) { // if player moving up
            // top wall collision
            if(gridPosEquals(x, y + vsp, "B") ||
                gridPosEquals(x + w - 1, y + vsp, "B")) {
                vsp = (((int)((y + vsp)/SQUARE_SIZE) + 1) * SQUARE_SIZE) - y;
            }
            // start falling if space no longer held
            if(!keyHold(' ')) {
                vsp /= 3;
            }
        }
        // walk left and right
        if(keyHold('a')) {
            hsp = -walkSpeed;
        }
        if(keyHold('d')) {
            hsp = walkSpeed;
        }
        // left wall collision
        if(hsp < 0 && (gridPosEquals(x + hsp, y, "B") ||
            gridPosEquals(x + hsp, y + h - 1, "B"))) {
                hsp = (((int)((x + hsp)/SQUARE_SIZE) + 1) * SQUARE_SIZE) - x;
        }
        // right wall collision
        if(hsp > 0 && (gridPosEquals(x + w + hsp,y,"B") ||
            gridPosEquals(x + w + hsp,y + h - 1,"B"))) {
                hsp = Math.floor((((int)((x + w + hsp)/SQUARE_SIZE)) * SQUARE_SIZE) - (x + w));
        }

        // move player according to velocity components
        x += Math.round(hsp);
        y += vsp;

        // manage player collisions with level edge, coins, spikes, and level end
        double centerX = x + (w/2);
        double centerY = y + (h/2);
        if(!((int)(centerY/SQUARE_SIZE) >= 0 && (int)(centerY/SQUARE_SIZE) <= grid.length && (int)(centerX/SQUARE_SIZE) >= 0 && (int)(centerX/SQUARE_SIZE) <= grid[0].length)) {
            reset();
            centerX = x + (w/2);
            centerY = y + (h/2);
        }
        coinCollide(centerX,centerY);
        spikeCollide(centerX,centerY);
        endCollide(centerX,centerY);
    }


    // is given point colliding with a coin
    private void coinCollide(double x, double y) {
        if(gridPosEquals(x,y,"C")) {
            coinCount += 1;
            gridPosSet(x,y,"A");
        }
    }
    // is given point colliding with a spike
    private void spikeCollide(double x, double y) {
        if(gridPosEquals(x,y,"S")) {
            reset();
        }
    }
    // is given point colliding with end of level
    private void endCollide(double x, double y) {
        if(gridPosEquals(x,y,"E")) {
            levelDone = true;
        }
    }

    // reset player to OG position (called upon death)
    public void reset() {
        x = ogX;
        y = ogY;
        hsp = 0;
        vsp = 0;
    }

    // convert given pixel coordinates to grid coordinates
    // see if that grid position contains a value
    private boolean gridPosEquals(double x, double y, String value) {
        return ((int)(y/SQUARE_SIZE) >= 0 && (int)(y/SQUARE_SIZE) < grid.length && (int)(x/SQUARE_SIZE) >= 0
         && (int)(x/SQUARE_SIZE) < grid[0].length) && grid[(int)(y/SQUARE_SIZE)][(int)(x/SQUARE_SIZE)].equals(value);
    }
    // convert given pixel coordinates to grid coordinates
    // set that grid position to a new value
    private void gridPosSet(double x, double y, String newValue) {
        grid[(int)(y/SQUARE_SIZE)][(int)(x/SQUARE_SIZE)] = newValue;
    }
    
    // set the window platformer is running on
    public void setDrawer(LevelDraw drawer) {
        this.drawer = drawer;
    }
    // is a given key currently held down on the keyboard?
    private boolean keyHold(char key) {
        return (keyHoldHashSet.contains(key));
    }
    // has the player finished the level?
    public boolean getLevelDone() {
        return levelDone;
    }
    // color of player
    public Color getColor() {
        return Color.BLUE;
    }
    // current player x
    public int getX() {
        return (int) x;
    }
    // current player y
    public int getY() {
        return (int) y;
    }
    // current number of coins collected
    public int getCoinCount() {
        return coinCount;
    }
    // class type as stored on level grid
    public String getType() {
        return "P";
    }
}
