import java.awt.Color;
// Class used for any dynamically moving objects
// In this version only used for player, could be expanded to create moving enemies
abstract class ActiveObject {
    abstract void update(String[][] grid);
    abstract int getX();
    abstract int getY();
    abstract Color getColor();
    abstract String getType();
}
