import java.util.HashSet;

public class LevelContents {
    // grid of level blocks
    private String[][] levelGrid;
    // set containing every actively simulated object
    private HashSet<ActiveObject> activeSet;
    // reference to player
    private Player playerObj;

    public LevelContents(String[][] levelGrid, HashSet<ActiveObject> activeSet) {
        this.levelGrid = levelGrid;
        this.activeSet = activeSet;
        // check for and make reference to player
        for(ActiveObject object: activeSet) {
            if(object.getType() == "P") {
                playerObj = (Player)object;
            }
        }
    }

    // return specific contents
    public String[][] getLevelGrid() {
        return levelGrid;
    }
    public HashSet<ActiveObject> getActiveSet() {
        return activeSet;
    }
    public Player getPlayer() {
        return playerObj;
    }
}
