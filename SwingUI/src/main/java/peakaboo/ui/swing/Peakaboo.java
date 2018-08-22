package peakaboo.ui.swing;

import java.awt.Dimension;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;

import commonenvironment.Env;
import peakaboo.common.MemoryProfile;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.common.MemoryProfile.Size;
import peakaboo.curvefit.peak.table.CombinedPeakTable;
import peakaboo.curvefit.peak.table.KrausePeakTable;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.table.XrayLibPeakTable;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterFrame;
import stratus.StratusLookAndFeel;
import stratus.theme.LightTheme;
import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.LayerDialog.MessageType;



public class Peakaboo
{
	private static Timer gcTimer;
	

	private static void showError(Throwable e, String message) {
		showError(e, message, null);
	}
	
	private static void showError(Throwable e, String message, String text)
	{
		
		SwingUtilities.invokeLater(() -> {
			TaskDialog errorDialog = new TaskDialog("Peakaboo Error");
			errorDialog.setIcon(StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
			errorDialog.setInstruction(message);
			
			String realText = text;
			
			if (realText != null) {
				realText += "\n";
			} else if (e != null) {
				if (e.getMessage() != null) {
					realText = e.getMessage() + "\n";
				}
				realText += "The problem is of type " + e.getClass().getSimpleName();
			}
			
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
	

	private static void warnDevRelease() {
		if (!Version.release){
			String message = "This build of Peakaboo is not a final release version.\nAny results you obtain should be treated accordingly.";
			String title = "Development Build of Peakaboo";
			
			new LayerDialog(title, message, MessageType.INFO).showInWindow(null, true);
			
		}
	}
	
	private static void warnLowMemory() {
		PeakabooLog.get().log(Level.INFO, "Max heap size = " + Env.heapSize());
		
		if (MemoryProfile.size == Size.SMALL){
			String message = "This system's Java VM is only allocated " + Env.heapSize()
			+ "MB of memory.\nProcessing large data sets may be quite slow, if not impossible.";
			String title = "Low Memory";
						
			new LayerDialog(title, message, MessageType.INFO).showInWindow(null, true);
			
			//dialog.setAlwaysOnTop(true);
			//dialog.setVisible(true);
		}
	}
	
	private static void runPeakaboo()
	{

		//Any errors that don't get handled anywhere else come here and get shown
		//to the user and printed to standard out.
		try {
			new TabbedPlotterFrame();
		} catch (Exception e) {
			
			PeakabooLog.get().log(Level.SEVERE, "Critical Error in Peakaboo", e);
			
			//if the user chooses to close rather than restart, break out of the loop
			showError(e, "Peakaboo has encountered a problem and must exit");
			System.exit(1);
			
		}
		
	}
	
	private static void errorHook() {
		PeakabooLog.getRoot().addHandler(new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				if (record.getLevel() == Level.SEVERE) {
					Throwable t = record.getThrown();
					String m = record.getMessage();
					
					if (t == null && m.startsWith("\tat ")) {
						return;
					}
					
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
	
	private static void setAppTitle(String title) {
		//This was broken with Java 8/9
//		try
//		{
//		    Toolkit toolkit = Toolkit.getDefaultToolkit();
//		    Field awtAppClassNameField = toolkit.getClass().getDeclaredField("awtAppClassName");
//		    awtAppClassNameField.setAccessible(true);
//		    awtAppClassNameField.set(toolkit, title);
//		}
//		catch (NoSuchFieldException | IllegalAccessException e)
//		{
//		    e.printStackTrace();
//		}
		
	}
	
	private static void startGCTimer() {
		gcTimer = new Timer(1000*60, e -> {  
			System.gc(); 
		});
		
		gcTimer.setRepeats(true);
		gcTimer.start();
	}
	
	private static void setLaF(LookAndFeel laf) {
		try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to set Look and Feel", e);
		}
	}
	
	public static void run() {
		
		//Needed to work around https://bugs.openjdk.java.net/browse/JDK-8130400
		//NEED TO SET THESE RIGHT AT THE START BEFORE ANY AWT/SWING STUFF HAPPENS.
		//THAT INCLUDES CREATING ANY ImageIcon DATA FOR SPLASH SCREEN
		System.setProperty("sun.java2d.xrender", "false");
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
		
		
		PeakabooLog.get().log(Level.INFO, "Starting " + Version.longVersionNo + " - " + Version.buildDate);
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		StratusLookAndFeel laf = new StratusLookAndFeel(new LightTheme());
		setAppTitle("Peakaboo 5");
		
		
		//warm up the peak table, which is lazy
		//do this in a separate thread so that it proceeds in parallel 
		//with all the other tasks, since this is usually the longest 
		//running init job
		Thread peakLoader = new Thread(() -> PeakTable.SYSTEM.getAll());
		peakLoader.setDaemon(true);
		peakLoader.start();
		
		Swidget.initialize(Version.splash, Version.icon, "Peakaboo", () -> {
			setLaF(laf);
			PeakabooLog.init();
			errorHook();
			startGCTimer();
			warnLowMemory();
			warnDevRelease();
			DataSourcePluginManager.SYSTEM.load();
			FilterPluginManager.SYSTEM.load();
			DataSinkPluginManager.SYSTEM.load();
			try {
				peakLoader.join();
			} catch (InterruptedException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to load peak table", e);
			}
			runPeakaboo();
		});
		
		
	}
	
	public static void main(String[] args)
	{	
		run();
	}


}
