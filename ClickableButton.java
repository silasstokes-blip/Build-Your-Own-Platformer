import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// sets up button with action detector determining if button was clicked
public class ClickableButton extends JButton {
    // If button is clicked and program is yet to complete buttons action
    private boolean clicked;
    // listens for if button is clicked
    private ButtonClickListener listener;
    public ClickableButton(String name) {
        super(name);
        clicked = false;
        listener = new ButtonClickListener(this);
        this.addActionListener(listener);
    }
    
    public void setClicked(boolean newClick) {
        clicked = newClick;
    }
    public boolean getClicked() {
        return clicked;
    }
}