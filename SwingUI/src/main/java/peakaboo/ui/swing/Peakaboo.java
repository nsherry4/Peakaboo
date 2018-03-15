package peakaboo.ui.swing;

import java.awt.Dimension;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;

import commonenvironment.Env;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.datasource.plugin.DataSourceLoader;
import peakaboo.filter.model.FilterLoader;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterFrame;
import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;



public class Peakaboo
{
	private static final Logger LOGGER = PeakabooLog.get();


	public static void showError(Throwable e, String message) {
		showError(e, message, null);
	}
	
	public static void showError(Throwable e, String message, String text)
	{
		SwingUtilities.invokeLater(() -> {
			TaskDialog errorDialog = new TaskDialog("Peakaboo Error");
			errorDialog.setIcon(StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
			errorDialog.setInstruction(message);
			
			String realText = text;
			
			if (realText != null) {
				realText += "\n";
			} else {
				realText = e.getMessage() + "\n";
			}
			realText += "The problem is of type " + e.getClass().getSimpleName();
			errorDialog.setText(realText);
				
			JTextArea stacktrace = new JTextArea();
			stacktrace.setEditable(false);
			stacktrace.setText((e != null) ? Strings.stackStraceAsString(e) : "No additional information available");
			
			JScrollPane scroller = new JScrollPane(stacktrace);
			scroller.setPreferredSize(new Dimension(500, 200));
			errorDialog.getDetails().setExpandableComponent(scroller);
			errorDialog.getDetails().setExpanded(true);
		
			errorDialog.show();
		});
			
	}
	

	
	private static void runPeakaboo()
	{

		LOGGER.log(Level.INFO, "Max heap size = " + Env.heapSize());
		
		if (Env.heapSize() <= 128){
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
			TabbedPlotterFrame peakaboo = new TabbedPlotterFrame();
		} catch (Exception e) {
			
			PeakabooLog.get().log(Level.SEVERE, "Critical Error in Peakaboo", e);
			
			//if the user chooses to close rather than restart, break out of the loop
			showError(e, "Peakaboo has encountered a problem and must exit");
			System.exit(1);
			
		}
		
	}
	
	public static void errorHook() {
		PeakabooLog.get().addHandler(new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				if (record.getLevel() == Level.SEVERE) {
					Throwable t = record.getThrown();
					String m = record.getMessage();
					showError(t, m);
				}
			}
			
			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void close() throws SecurityException {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void run() {
		
		LOGGER.log(Level.INFO, "Starting " + Version.title);
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		Swidget.initialize(Version.splash, Version.icon, () -> {
			errorHook();
			PeakabooLog.init();
			PeakTableReader.readPeakTable();
			DataSourceLoader.load();
			FilterLoader.load();
			runPeakaboo();
		});
		
		
	}
	
	public static void main(String[] args)
	{	
		run();
	}


}
