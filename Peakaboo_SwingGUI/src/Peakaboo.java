



import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import commonenvironment.Env;

import peakaboo.ui.swing.PlotterFrame;

import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;



public class Peakaboo
{

	public static void main(String[] args)
	{
		

		Swidget.initialize();
				
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		
		//if this version of the JVM is new enough to support the Nimbus Look and Feel, use it
		try
		{
			if (! Env.isMac() && !Env.isWindows()) UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

			if (Env.isMac() || Env.isWindows()) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			
		}
		
		


		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{

				if (Env.heapSize() < 250){
					JOptionPane.showMessageDialog(
						null,
						"This system's Java VM is only allocated " + Env.heapSize()
						+ "MB of memory: processing large data sets may be quite slow, or impossible.",
						"Low on Memory",
						JOptionPane.INFORMATION_MESSAGE,
						StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
				}
				
						
				//TODO: JAVA 5 doesn't seem to resize windows properly on linux (at least not with compiz)
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				PlotterFrame peakaboo = new PlotterFrame();

			}
		});

	}


}
