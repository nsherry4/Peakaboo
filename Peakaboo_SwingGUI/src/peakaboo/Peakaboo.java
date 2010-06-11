package peakaboo;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Peakaboo {


	public static void main(String[] args) {
		
		
		//if this version of the JVM is new enough to support the Nimbus Look and Feel, use it
		try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");		
						
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			//Do Nothing -- Not an error, just not supported 
		}

		
		
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				long maxHeap = Runtime.getRuntime().maxMemory() / 1024 / 1024;
				if (maxHeap < 250) JOptionPane.showMessageDialog(null, new JLabel("This system's Java VM is only allocated " + maxHeap + "MB of memory, processing large data sets may not be possible."), "Low on Memory", JOptionPane.INFORMATION_MESSAGE );
				
				//TODO: JAVA 5 doesn't seem to resize windows properly on linux (at least not with compiz)
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				peakaboo.ui.swing.PeakabooPlotterSwing peakaboo = new peakaboo.ui.swing.PeakabooPlotterSwing();

			}
		});

	}
	

}
