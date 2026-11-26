import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ButtonClickListener implements ActionListener {
    private ClickableButton button;
    public ButtonClickListener(ClickableButton button) {
        this.button = button;
    }
    public void actionPerformed(ActionEvent event) {
		button.setClicked(true);
	}
}