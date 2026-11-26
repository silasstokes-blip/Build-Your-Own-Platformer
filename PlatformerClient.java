import java.util.HashSet;
import java.io.FileNotFoundException;
import java.lang.Math.*;
import javax.swing.*;
import java.awt.*;


public class PlatformerClient {
    private static final int SQUARE_SIZE = 20;
        
    public static void main(String[] args) throws FileNotFoundException{
        // create level editor
        LevelCreator creator = new LevelCreator();
        creator.createLevelEditor();

        // run level editor indefinately
        // runs once for each time a level is started
        while(true) {
            creator.runLevelEditor();

            // get player setting
            double walkSpeed = creator.getVariable("walkSpeed");
            double jumpForce = creator.getVariable("jumpForce");
            double grav = creator.getVariable("grav");
            double terminalVel = creator.getVariable("terminalVel");
            
            // copy over level grid to run in level
            // create player object at appropriate spot
            String[][] levelGrid = creator.getLevelGrid();//readLevelFile("testLevel.txt");
            String[][] levelGridCopy = new String[levelGrid.length][levelGrid[0].length];
            Player playerObj = null;
            HashSet<ActiveObject> activeObjectSet = new HashSet<ActiveObject>();
            for( int f = 0; f < levelGrid.length; f++) {
                for( int e = 0; e < levelGrid[f].length; e++) {
                    levelGridCopy[f][e] = levelGrid[f][e];
                    if(levelGrid[f][e].equals("P")) {
                        playerObj = new Player(e * SQUARE_SIZE,f * SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE,
                            walkSpeed,jumpForce,grav,terminalVel);
                        activeObjectSet.add(playerObj);
                    }
                }
            }
            levelGrid = levelGridCopy;

            // object storing level contents
            LevelContents contents = new LevelContents(levelGrid,activeObjectSet);
            
            // level size in pixels
            int levelWidth = SQUARE_SIZE * levelGrid[0].length;
            int levelHeight = SQUARE_SIZE * levelGrid.length;

            // create panel running and drawing level
            LevelDraw drawer = new LevelDraw(levelWidth,levelHeight,levelGrid[0].length,
                levelGrid.length,contents);
            contents.getPlayer().setDrawer(drawer);
            
            // start platformer main loop
            drawer.startTimer();
        }
    }
}