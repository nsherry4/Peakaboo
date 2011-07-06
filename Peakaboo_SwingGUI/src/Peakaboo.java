



import javax.swing.JOptionPane;

import commonenvironment.Env;

import peakaboo.common.Version;
import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.ui.swing.PlotterFrame;

import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;



public class Peakaboo
{

	/**
	 * Performs one-time only start-up tasks like reading the peak table
	 */
	public static void initialize()
	{
		PeakTableReader.readPeakTable();
		
		Swidget.initialize();
		
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		
		
		
		
	}
	
	public static void main(String[] args)
	{
		
		initialize();

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{

				if (Env.heapSize() < 120){
					JOptionPane.showMessageDialog(
						null,
						"This system's Java VM is only allocated " + Env.heapSize()
						+ "MB of memory: processing large data sets may be quite slow, if not impossible.",
						"Low on Memory",
						JOptionPane.INFORMATION_MESSAGE,
						StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
				}
					
				
				//Any errors that don't get handled anywhere else come here and get shown
				//to the user and printed to standard out.
				try {
						
					new PlotterFrame();
					
				} catch (Exception e) {
					
					e.printStackTrace();
					StringBuilder sb = new StringBuilder();
					
					sb.append("A general error has occured in " + Version.program_name + ":\n\n");
					
					for (StackTraceElement ste : e.getStackTrace()) {
						sb.append( ste.toString() + "\n" );
					}
					
					JOptionPane.showMessageDialog(null, sb.toString(), "General Error in " + Version.program_name, JOptionPane.ERROR_MESSAGE, StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
					
				}

			}
		});

	}


}
