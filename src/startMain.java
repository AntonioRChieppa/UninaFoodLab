import gui.*;
import javax.swing.SwingUtilities;


public class startMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new LoginFrame().setVisible(true);
		});
	}

}
