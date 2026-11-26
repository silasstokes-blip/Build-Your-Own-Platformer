import java.util.Scanner;
import java.util.concurrent.Flow;
import java.io.File;
import java.util.HashSet;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.Math.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class LevelCreator {
    // grid of currently designed level contents
    private String[][] levelGrid;
    // names and values of various player variables
    private HashMap<String,Double> levelVariables;
    // initial size of each grid cell in pixels
    private static double SQUARE_SIZE = 20.0;
    // panels containing components manipulating player settings
    // each a line in the player tab of level creator menu
    private JPanel[] playerSettingPanel;
    // sliders collecting inputs for player settings
    private JSlider[] playerSettingSlider;
    // text describing player jump height and time, if file saving/loading worked, level start errors
    private JLabel jumpDetails, fileResults, startError;
    // panel for drawing out level
    private GridPanel gridPanel;
    // text input fields for level size, .txt file to load/save
    private JTextField widthText, heightText, fileText;
    // buttons for options of blocks to draw in level
    private JRadioButton[] blockButton;
    // buttons to start level, load file, and save file respectively
    private ClickableButton startButton, loadButton, saveButton;
    // level creator frame
    private JFrame frame;


    // create and organize all menu components for level editor
    public void createLevelEditor() {

        // Level editor JFrame
        frame = new JFrame("Level Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize( new Dimension(800, 800) );
		frame.setLayout(new FlowLayout());

        // Panel with all menu contents
        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        // tabs for player, level, and file menus
        JTabbedPane tabs = new JTabbedPane();
        panel.add(tabs, BorderLayout.NORTH);
        // each tab
        JPanel playerPanel = new JPanel(new GridLayout(15,1));
        tabs.addTab("Player", playerPanel);
        JPanel levelPanel = new JPanel(new BorderLayout());
        tabs.addTab("Level", levelPanel);
        JPanel filePanel = new JPanel(new GridLayout(15,1));
        tabs.addTab("File", filePanel);
        

        /////////////////////////////////PLAYER MENU////////////////////////////////////////////////
        // create given number of lines of player settings
        int playerPanelCount = 5;
        playerSettingPanel = new JPanel[playerPanelCount];
        for( int f = 0; f < playerPanelCount; f++) {
            playerSettingPanel[f] = new JPanel(new FlowLayout());
            playerPanel.add(playerSettingPanel[f]);
        }
        // create given number of player setting sliders
        int playerSliderCount = 4;
        playerSettingSlider = new JSlider[playerSliderCount];
        for( int f = 0; f < playerSliderCount; f++) {
            playerSettingSlider[f] = new JSlider();
            playerPanel.add(playerSettingSlider[f]);
        }

        // player walk speed control
        playerSettingPanel[0].add(new JLabel("Player Speed: Slow"));
        playerSettingSlider[0].setMinimum(2);
        playerSettingSlider[0].setMaximum(8);
        playerSettingSlider[0].setValue(4);
        playerSettingPanel[0].add(playerSettingSlider[0]);
        playerSettingPanel[0].add(new JLabel("Fast"));

        // player jump force control
        playerSettingPanel[1].add(new JLabel("Jump Force: Low"));
        playerSettingSlider[1].setMinimum(4);
        playerSettingSlider[1].setMaximum(20);
        playerSettingSlider[1].setValue(12);
        playerSettingPanel[1].add(playerSettingSlider[1]);
        playerSettingPanel[1].add(new JLabel("High"));
        
        // player gravity control
        playerSettingPanel[2].add(new JLabel("Gravity: Low"));
        playerSettingSlider[2].setMinimum(30);
        playerSettingSlider[2].setMaximum(200);
        playerSettingSlider[2].setValue(75);
        playerSettingPanel[2].add(playerSettingSlider[2]);
        playerSettingPanel[2].add(new JLabel("High"));

        // player jump details/info (jump height and jump time)
        jumpDetails = new JLabel();
        playerSettingPanel[3].add(jumpDetails);
        setJumpDetails();

        // player terminal velocity control
        playerSettingPanel[4].add(new JLabel("Terminal Velocity: Slow"));
        playerSettingSlider[3].setMinimum(3);
        playerSettingSlider[3].setMaximum(15);
        playerSettingSlider[3].setValue(15);
        playerSettingPanel[4].add(playerSettingSlider[3]);
        playerSettingPanel[4].add(new JLabel("Fast"));
        

        /////////////////////////////////LEVEL MENU////////////////////////////////////////////////
        // grid for drawing out level
        gridPanel = new GridPanel(400,400,20,20);
        levelPanel.add(gridPanel, BorderLayout.NORTH);
        
        // text lines for level options
        JPanel levelPanel2 = new JPanel(new GridLayout(7,1));
        levelPanel.add(levelPanel2, BorderLayout.CENTER);

        // line gathering level grid width and height using text fields
        JPanel levelSizePanel = new JPanel(new FlowLayout());
        levelPanel2.add(levelSizePanel);
        levelSizePanel.add(new JLabel("Width: "));
        widthText = new JTextField("20");
        levelSizePanel.add(widthText);
        levelSizePanel.add(new JLabel("Height: "));
        heightText = new JTextField("20");
        levelSizePanel.add(heightText);

        // buttons for options of blocks to place
        ButtonGroup blockButtons = new ButtonGroup();
        blockButton = new JRadioButton[6];
        blockButton[0] = new JRadioButton("Air");
        blockButton[1] = new JRadioButton("Block");
        blockButton[2] = new JRadioButton("Player");
        blockButton[3] = new JRadioButton("Level End");
        blockButton[4] = new JRadioButton("Spike");
        blockButton[5] = new JRadioButton("Coin");
        // add each button to panel
        for(int f = 0; f < 6; f++) {
            blockButtons.add(blockButton[f]);
            levelPanel2.add(blockButton[f]);
        }
        // start with "Block" as default selected object
        blockButton[1].setSelected(true); 

        /////////////////////////////////LEVEL MENU////////////////////////////////////////////////
        // temp panels uses to format each menu component as its own line
        // field collecting name of file to load/save
        fileText = new JTextField("testLevel.txt");
        JPanel tempPanel = new JPanel(new FlowLayout());
        filePanel.add(tempPanel);
        tempPanel.add(fileText);

        // load file button
        loadButton = new ClickableButton("Load File");
        tempPanel = new JPanel(new FlowLayout());
        filePanel.add(tempPanel);
        tempPanel.add(loadButton);

        // save file button
        saveButton = new ClickableButton("Save File");
        tempPanel = new JPanel(new FlowLayout());
        filePanel.add(tempPanel);
        tempPanel.add(saveButton);

        // text saying the results of file loading/saving
        fileResults = new JLabel("");
        tempPanel = new JPanel(new FlowLayout());
        filePanel.add(tempPanel);
        tempPanel.add(fileResults);


        // Button to start level
        startButton = new ClickableButton("Start Level");
        panel.add(startButton, BorderLayout.CENTER);

        tempPanel = new JPanel();
        panel.add(tempPanel, BorderLayout.SOUTH);
        startError = new JLabel("");
        startError.setForeground(Color.RED);
        tempPanel.add(startError);

		frame.setVisible(true);

        
    }


    // read in a given .txt file with level settings
    public void readLevelFile(String fileName) throws FileNotFoundException{
        // start scanning file
        File levelFile = new File(fileName);
        Scanner levelScan = new Scanner(levelFile);
        
        // gather grid width and height at top of file
        int width = levelScan.nextInt();
        int height = levelScan.nextInt();
        // load in grid contents
        levelGrid = new String[height][width];
        for( int f = 0; f < height; f++) {
            for( int e = 0; e < width; e++) {
                levelGrid[f][e] = levelScan.next();
            }
        }

        // read in and save player variables from bottom of file
        levelVariables = new HashMap<String,Double>();
        String varName = ""; // name of currently read variable
        Double varValue = 0.0; // value of current variable
        while(levelScan.hasNext()) {
            if(levelScan.hasNextDouble()) { // read variable value
                varValue = levelScan.nextDouble();
                // log variable name and value
                levelVariables.put(varName, varValue);
            } else { // read variable name
                varName = levelScan.next();
            }
        }
        // finish scanning
        levelScan.close();

        // set variable sliders to right levels
        playerSettingSlider[0].setValue((int)getVariable("walkSpeed"));
        playerSettingSlider[1].setValue((int)(getVariable("jumpForce") * -1));
        playerSettingSlider[2].setValue((int)(getVariable("grav") * 100));
        playerSettingSlider[3].setValue((int)getVariable("terminalVel"));
        // set text fields to right level size
        widthText.setText("" + width);
        heightText.setText("" + height);
        // set grid to correct level contents
        gridPanel.setGridHeight(height);
        gridPanel.setGridWidth(width);
        gridPanel.setLevelGrid(levelGrid);
    }

    // save level data to a new text file 
    public void writeLevelFile(String fileName) throws FileNotFoundException, IOException {
        FileWriter writer = new FileWriter(fileName, false);
                        
        // write grid dimensions
        int gridWidth = gridPanel.getGridWidth();
        int gridHeight = gridPanel.getGridHeight();
        writer.write(gridWidth + " " + gridHeight + "\n");

        // write level grid contents
        levelGrid = gridPanel.getLevelGrid();
        // string as line storing given row of cells
        String rowBlocks;
        for(int f = 0; f < gridHeight; f++) {
            rowBlocks = "";
            for(int i = 0; i < gridWidth; i++) {
                rowBlocks += levelGrid[f][i] + " ";
            }
            writer.write(rowBlocks + "\n");
        }

        // write player variables
        writer.write("walkSpeed " + (double)playerSettingSlider[0].getValue() + "\n");
        writer.write("jumpForce " + (double)(playerSettingSlider[1].getValue() * -1.0) + "\n");
        writer.write("grav " + (double)(playerSettingSlider[2].getValue()/100.0) + "\n");
        writer.write("terminalVel " + (double)playerSettingSlider[3].getValue() + "\n");

        // finish writing
        writer.close();
    }

    // loop running level editor
    public void runLevelEditor() {
        while(true) {
            // initiate start button as unpressed
            startButton.setClicked(false);

            // loop until level is started
            while(!startButton.getClicked()) {

                // when file save or load buttons are clicked
                loadOrSaveFile();

                // update details line for jump based on sliders
                setJumpDetails();
                
                // resize level grid if necessary
                resizeLevel();

                // set current block level is being painted with
                // based on button
                for(int f = 0; f < 6; f++) {
                    if(blockButton[f].isSelected()) {
                        if(f == 0) { // air
                            gridPanel.setBlock("A");
                        } else if(f == 1) { // block
                            gridPanel.setBlock("B");
                        } else if(f == 2) { // player
                            gridPanel.setBlock("P");
                        } else if(f == 3) { // level end
                            gridPanel.setBlock("E");
                        } else if(f == 4) { // spike
                            gridPanel.setBlock("S");
                        } else if(f == 5) { // coin
                            gridPanel.setBlock("C");
                        }
                    }
                }
            }
            if(gridPanel.levelHasPlayer()) {
                startError.setText("");
                break;
            } else {
                startError.setText("Error: Level must have a player!");
            }
        }

        // Store values for each player setting
        levelVariables = new HashMap<String,Double>();
        levelVariables.put("walkSpeed", (double)playerSettingSlider[0].getValue());
        levelVariables.put("jumpForce", (double)(playerSettingSlider[1].getValue() * -1.0));
        levelVariables.put("grav", (double)(playerSettingSlider[2].getValue()/100.0));
        levelVariables.put("terminalVel", (double)playerSettingSlider[3].getValue());
        // Sore level grid
        levelGrid = gridPanel.getLevelGrid();
    }


    // check and respond when file load or save buttons are clicked
    public void loadOrSaveFile() {
        // if either button pressed
        if(loadButton.getClicked() || saveButton.getClicked()) {
            // don't allow non text file inputs
            if(!fileText.getText().endsWith(".txt")) {
                fileResults.setText("Error: Must be a .txt file");
                fileResults.setForeground(Color.RED);

            } else if(saveButton.getClicked()) { // Try level save
                // see if file exists
                boolean fileExists;
                try {
                    fileExists = testFileExists(fileText.getText());
                } catch (FileNotFoundException e) {
                    fileExists = false;
                }

                // don't allow overwriting of existing file
                if(fileExists) {
                    fileResults.setText("Error: File already exists");
                    fileResults.setForeground(Color.RED);

                } else { // if file doesn't exist
                    try { // try saving file
                        writeLevelFile(fileText.getText());
                        // success message
                        fileResults.setText("Level successfully saved to " + fileText.getText());
                        fileResults.setForeground(Color.GREEN);

                    } catch (IOException e2) {
                        fileResults.setText("Error: Level failed to save");
                        fileResults.setForeground(Color.RED);
                    }
                }
            } else try {
                readLevelFile(fileText.getText());
                fileResults.setText("File loaded successfully!");
                fileResults.setForeground(Color.GREEN);
            } catch(FileNotFoundException e) {
                fileResults.setText("Error: File doesn't exist");
                fileResults.setForeground(Color.RED);
            }
            // reset button states to allow for further input
            loadButton.setClicked(false);
            saveButton.setClicked(false);
        }
    }


    // convert string to integer
    public int toInteger(String text) {
        // -1 for non-integer string
        // if string is empty
        if(text.length() == 0) {
            return -1;
        }

        int sum = 0;
        // run through each base spot
        for(int f = 0; f < text.length();f++) {
            // add to integer total
            sum *= 10;
            if(text.charAt(f) >= 48 && text.charAt(f) <= 57) {
                sum += (int)text.charAt(f) - 48;
            // if character isn't a digit, return -1
            } else {
                return -1;
            }
        }
        return sum; // valid integer total
    }

    // test and check if a given file exists, return results
    public boolean testFileExists(String name) throws FileNotFoundException {
        File levelFile = new File(name);
        if(levelFile.exists() && !levelFile.isDirectory()) { 
            return true;
        }
        return false;
    }

    // return level grid
    public String[][] getLevelGrid() {
        return levelGrid;
    }
    // read stored value for given variable name
    public double getVariable(String varName) {
        return levelVariables.get(varName);
    }

    // set the text for the jump details label describing jump height and time
    public void setJumpDetails() {
        double v = (double)playerSettingSlider[1].getValue() * -1.0; // jump force slider
        double a = (double)playerSettingSlider[2].getValue()/100.0; // grav slider
        double jumpHeight = (Math.floor(((v * v)/(2.0 * a))/SQUARE_SIZE * 10.0)/10.0);
        double jumpTime = Math.floor(-(2 * v/a) * 10.0/60.0)/10.0;
        jumpDetails.setText("Jump Height: " + jumpHeight
         + " blocks   Jump Time: " + jumpTime + " seconds");
    }
    // check level width and height text fields and adjusts accordingly
    public void resizeLevel() {
        if(!widthText.isFocusOwner()) {
            int widthNumb = toInteger(widthText.getText());
            if(widthNumb > 0 && widthNumb != gridPanel.getGridWidth()) {
                // clamp max level width to 75 blocks
                if(widthNumb > 75) {
                    widthText.setText("75");
                    widthNumb = 75;
                }
                gridPanel.setGridWidth(widthNumb);
            }
        }
        if(!heightText.isFocusOwner()) {
            int heightNumb = toInteger(heightText.getText());
            if(heightNumb > 0 && heightNumb != gridPanel.getGridHeight()) {
                // clamp max level height to 40 blocks
                if(heightNumb > 40) {
                    heightText.setText("40");
                    heightNumb = 40;
                }
                gridPanel.setGridHeight(heightNumb);
            }
        }
    }
}