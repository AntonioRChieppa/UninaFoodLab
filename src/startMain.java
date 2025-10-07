import javax.swing.SwingUtilities;
import gui.*;


public class startMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new LoginFrame();
		});
	}

}
