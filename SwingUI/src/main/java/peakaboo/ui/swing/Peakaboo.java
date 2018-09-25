package peakaboo.ui.swing;

import java.awt.Dimension;
import java.io.File;
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

import eventful.EventfulConfig;
import peakaboo.common.Env;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.common.PeakabooConfiguration;
import peakaboo.common.PeakabooConfiguration.MemorySize;
import peakaboo.curvefit.peak.table.CombinedPeakTable;
import peakaboo.curvefit.peak.table.DelegatingPeakTable;
import peakaboo.curvefit.peak.table.KrausePeakTable;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.table.SerializedPeakTable;
import peakaboo.curvefit.peak.table.XrayLibPeakTable;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.ui.swing.environment.DesktopApp;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterFrame;
import stratus.StratusLookAndFeel;
import stratus.theme.LightTheme;
import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.TextWrapping;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.LayerDialog.MessageType;



public class Peakaboo
{
	private static Timer gcTimer;
	

	private static void showError(Throwable e, String message) {
		showError(e, message, null);
	}
	
	
	private static void showError(Throwable e, String message, String text) {
		TaskDialog errorDialog = new TaskDialog("Peakaboo Error");
		errorDialog.setIcon(StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
		errorDialog.setInstruction(TextWrapping.wrapTextForMultiline(message));
		
		String realText = text;
				
		if (realText != null) {
			realText += "<br />";
		} else if (e != null) {
			if (e.getMessage() != null) {
				realText = e.getMessage() + "<br/>";
			} else {
				realText = "";
			}
			realText += "The problem is of type " + e.getClass().getSimpleName();
		} else {
			realText = "";
		}

		realText = "<html>" + realText + "</html>";
		errorDialog.setText(realText);
			
		JTextArea stacktrace = new JTextArea();
		stacktrace.setEditable(false);
		stacktrace.setText((e != null) ? Strings.stackStraceAsString(e) : "No additional information available");
		
		JScrollPane scroller = new JScrollPane(stacktrace);
		scroller.setPreferredSize(new Dimension(500, 200));
		errorDialog.getDetails().setExpandableComponent(scroller);
		errorDialog.getDetails().setExpanded(true);
	
		errorDialog.show();
	}
	

	private static void warnDevRelease() {
		if (!Version.release){
			String message = "This build of Peakaboo is not a final release version.\nAny results you obtain should be treated accordingly.";
			String title = "Development Build of Peakaboo";
			
			new LayerDialog(title, message, MessageType.INFO).showInWindow(null, true);
			
		}
	}
	
	private static void warnLowMemory() {
		PeakabooLog.get().log(Level.INFO, "Max heap size = " + Env.maxHeap() + "MB");
		
		if (PeakabooConfiguration.memorySize == MemorySize.SMALL){
			String message = "This system's Java VM is only allocated " + Env.maxHeap()
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
		} catch (Throwable e) {
			PeakabooLog.get().log(Level.SEVERE, "Peakaboo has encountered a problem and must exit", e);
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
		
		peakaboo.common.PeakabooConfiguration.compression = true;
		peakaboo.common.PeakabooConfiguration.diskstore = true;
		PeakabooLog.init(DesktopApp.appDir("Logging"));
		
		PeakabooLog.get().log(Level.INFO, "Starting " + Version.longVersionNo + " - " + Version.buildDate);
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		StratusLookAndFeel laf = new StratusLookAndFeel(new LightTheme());
		setAppTitle("Peakaboo 5");
		
		
		//warm up the peak table, which is lazy
		//do this in a separate thread so that it proceeds in parallel 
		//with all the other tasks, since this is usually the longest 
		//running init job
		Thread peakLoader = new Thread(() -> {
			PeakTable original = PeakTable.SYSTEM.getSource();
			String filename;
			if (Version.release) {
				filename = "derived-peakfile-" + Version.longVersionNo + ".yaml";
			} else {
				filename = "derived-peakfile-" + Version.longVersionNo + "-" + Version.buildDate + ".yaml";
			}
			File peakdir = DesktopApp.appDir("PeakTable");
			peakdir.mkdirs();
			File peakfile = new File(DesktopApp.appDir("PeakTable") + "/" + filename);
			
			PeakTable.SYSTEM.setSource(new SerializedPeakTable(original, peakfile));
		});
		peakLoader.setDaemon(true);
		peakLoader.start();
		
		Swidget.initialize(Version.splash, Version.icon, "Peakaboo", () -> {
			setLaF(laf);
			EventfulConfig.uiThreadRunner = SwingUtilities::invokeLater;
			errorHook();
			startGCTimer();
			warnLowMemory();
			warnDevRelease();

			//Init plugins
			FilterPluginManager.init(DesktopApp.appDir("Plugins/Filter"));
			DataSourcePluginManager.init(DesktopApp.appDir("Plugins/DataSource"));
			DataSinkPluginManager.init(DesktopApp.appDir("Plugins/DataSink"));
			
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
