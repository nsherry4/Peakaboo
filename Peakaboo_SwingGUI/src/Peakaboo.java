
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;

import commonenvironment.Env;

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
	private static void initialize()
	{
		

		//required to work around:
		//Exception in thread "AWT-EventQueue-0" java.lang.InternalError: not implemented yet
		//at sun.java2d.x11.X11SurfaceData.getRaster(X11SurfaceData.java:193)
		//on Linux
		//It must be set before Swing starts up.
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
		PeakTableReader.readPeakTable();
		
		
		Swidget.initialize();
		
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		
		
		
		
	}
	

	private static boolean showError(Window parent, Throwable e)
	{
		TaskDialog errorDialog = new TaskDialog(parent, "Peakaboo");
		errorDialog.setIcon(StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
		errorDialog.setInstruction("Peakaboo has encountered a problem and must exit");
		
		String text = "";
		if (Strings.isEmpty(text)) text = "The problem is of type " + e.getClass().getSimpleName();
		errorDialog.setText(text);
			
		JTextArea stacktrace = new JTextArea();
		stacktrace.setEditable(false);
		stacktrace.setText(Strings.stackStraceAsString(e));
		
		JScrollPane scroller = new JScrollPane(stacktrace);
		scroller.setPreferredSize(new Dimension(500, 200));
		errorDialog.getDetails().setExpandableComponent(scroller);
		errorDialog.getDetails().setExpanded(false);
		
		return (errorDialog.show().getTag().ordinal() != 0);
	}
	
	private static void runPeakaboo()
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
		PlotterFrame peakaboo = null;
		try {
				
			peakaboo = new PlotterFrame();

		} catch (Exception e) {
			
			e.printStackTrace();
			
			//if the user chooses to close rather than restart, break out of the loop
			showError(peakaboo, e);
			System.exit(1);
			
		}
		
	}
	
	public static void main(String[] args)
	{	


		initialize();
		
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				
				runPeakaboo();

			}
		});


	}


}
