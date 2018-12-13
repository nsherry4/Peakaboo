package peakaboo.ui.swing;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eventful.EventfulConfig;
import peakaboo.calibration.CalibrationPluginManager;
import peakaboo.common.Env;
import peakaboo.common.PeakabooConfiguration;
import peakaboo.common.PeakabooConfiguration.MemorySize;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.table.SerializedPeakTable;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.filter.model.FilterPluginManager;
import peakaboo.ui.swing.environment.DesktopApp;
import peakaboo.ui.swing.plotting.PlotFrame;
import stratus.StratusLookAndFeel;
import stratus.theme.LightTheme;
import swidget.Swidget;
import swidget.dialogues.ErrorDialog;
import swidget.icons.IconFactory;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.LayerDialog.MessageType;
import swidget.widgets.layerpanel.LayerPanelConfig;



public class Peakaboo
{
	private static Timer gcTimer;
	
	public static final boolean SHOW_QUANTITATIVE = false;

	private static void showError(Throwable throwable, String message) {
		ErrorDialog errorDialog = new ErrorDialog(null, "Peakaboo Error", message, throwable);
		errorDialog.setVisible(true);
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
		
		if (PeakabooConfiguration.memorySize == MemorySize.TINY){
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
			new PlotFrame();
		} catch (Throwable e) {
			PeakabooLog.get().log(Level.SEVERE, "Peakaboo has encountered a problem and must exit", e);
			System.exit(1);
		}
		
	}
	
	private static void errorHook() {
		

		
		//Set error handler that shows a popup
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
	
	private static void uiPerformanceTune() {
		if (PeakabooConfiguration.memorySize == MemorySize.TINY || PeakabooConfiguration.memorySize == MemorySize.SMALL) {
			LayerPanelConfig.blur = false;
		}
	}
	
	public static void run() {
		
		//Needed to work around https://bugs.openjdk.java.net/browse/JDK-8130400
		//NEED TO SET THESE RIGHT AT THE START BEFORE ANY AWT/SWING STUFF HAPPENS.
		//THAT INCLUDES CREATING ANY ImageIcon DATA FOR SPLASH SCREEN
		System.setProperty("sun.java2d.xrender", "false");
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
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
				filename = "derived-peakfile-" + Version.longVersionNo + ".dat";
			} else {
				filename = "derived-peakfile-" + Version.longVersionNo + "-" + Version.buildDate + ".dat";
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
			uiPerformanceTune();

			//Init plugins
			FilterPluginManager.init(DesktopApp.appDir("Plugins/Filter"));
			DataSourcePluginManager.init(DesktopApp.appDir("Plugins/DataSource"));
			DataSinkPluginManager.init(DesktopApp.appDir("Plugins/DataSink"));
			CalibrationPluginManager.init(DesktopApp.appDir("Plugins/CalibrationReference"));
			
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
