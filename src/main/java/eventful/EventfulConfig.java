package eventful;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

public class EventfulConfig {

	public static Consumer<Runnable> runThread = SwingUtilities::invokeLater;
	
}
