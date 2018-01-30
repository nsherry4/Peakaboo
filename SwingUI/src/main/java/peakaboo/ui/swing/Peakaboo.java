package peakaboo.ui.swing;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;

import commonenvironment.Env;
import peakaboo.common.Version;
import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.datasource.plugin.DataSourceLoader;
import peakaboo.filter.model.FilterLoader;
import peakaboo.ui.swing.plotting.PlotterFrame;
import swidget.Swidget;
import swidget.dialogues.SplashScreen;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;



public class Peakaboo
{

	private static SplashScreen splash;
	
	private static void readPeakTable()
	{
		PeakTableReader.readPeakTable();
	}
	

	public static boolean showError(Window parent, Throwable e)
	{
		TaskDialog errorDialog = new TaskDialog("Peakaboo");
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
	
	private static void showSplash() {
		splash = new SplashScreen(IconFactory.getImageIcon(Version.splash));
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
			splash.setVisible(false);

		} catch (Exception e) {
			
			e.printStackTrace();
			
			//if the user chooses to close rather than restart, break out of the loop
			showError(peakaboo, e);
			System.exit(1);
			
		}
		
	}
	
	public static void run() {
		
		//Needed to work around https://bugs.openjdk.java.net/browse/JDK-8130400
		System.setProperty("sun.java2d.xrender", "false");
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		Swidget.initialize();
		
		
		SwingUtilities.invokeLater(() -> showSplash());
		
		//Sleep here just long enough to make sure that the swing event queue 
		//can draw the splash screen before we load more work into it. From
		//observation, 500ms isn't long enough???
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(() -> readPeakTable());
		SwingUtilities.invokeLater(() -> DataSourceLoader.load() );
		SwingUtilities.invokeLater(() -> FilterLoader.load() );
		
		SwingUtilities.invokeLater(() -> runPeakaboo());

		
	}
	
	public static void main(String[] args)
	{	
		run();
	}


}
