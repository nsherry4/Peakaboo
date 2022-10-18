package org.peakaboo.ui.swing;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.peakaboo.common.Env;
import org.peakaboo.common.PeakabooConfiguration;
import org.peakaboo.common.PeakabooConfiguration.MemorySize;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.common.Version;
import org.peakaboo.common.Version.ReleaseType;
import org.peakaboo.controller.plotter.Settings;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.table.SerializedPeakTable;
import org.peakaboo.datasink.plugin.DataSinkPluginManager;
import org.peakaboo.datasource.plugin.DataSourcePluginManager;
import org.peakaboo.filter.model.FilterPluginManager;
import org.peakaboo.framework.druthers.serialize.YamlSerializer;
import org.peakaboo.framework.eventful.EventfulConfig;
import org.peakaboo.framework.stratus.StratusLookAndFeel;
import org.peakaboo.framework.stratus.theme.LightTheme;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.dialogues.ErrorDialog;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog.MessageType;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanelConfig;
import org.peakaboo.mapping.filter.model.MapFilterPluginManager;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.environment.DesktopApp;
import org.peakaboo.ui.swing.plotting.PlotFrame;



public class Peakaboo {
	private static Timer gcTimer;
	
	private static void showError(Throwable throwable, String message) {
		ErrorDialog errorDialog = new ErrorDialog(null, "Peakaboo Error", message, throwable);
		errorDialog.setVisible(true);
	}

	private static void checkDevRelease() {
		if (Version.releaseType != ReleaseType.RELEASE){
			String message = "This build of Peakaboo is not a final release version.\nAny results you obtain should be treated accordingly.";
			String title = "Development Build of Peakaboo";
			if (Version.releaseType == ReleaseType.CANDIDATE) {
				title = "Release Candidate for Peakaboo";
			}
			
			new LayerDialog(title, message, MessageType.INFO).showInWindow(null, true);
			
		}
	}
	
	private static void checkLowMemory() {
		PeakabooLog.get().log(Level.INFO, "Max heap size = " + Env.maxHeap() + "MB");
		
		if (PeakabooConfiguration.memorySize == MemorySize.TINY){
			String message = "This system's Java VM is only allocated " + Env.maxHeap()
			+ "MB of memory.\nProcessing large data sets may be quite slow, if not impossible.";
			String title = "Low Memory";
						
			new LayerDialog(title, message, MessageType.INFO).showInWindow(null, true);
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
				// NOOP
				
			}
			
			@Override
			public void close() throws SecurityException {
				// NOOP
				
			}
		});
	}
		
	private static void startGCTimer() {
		gcTimer = new Timer(1000*60, e -> System.gc());
		
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
		
		org.peakaboo.common.PeakabooConfiguration.diskstore = true;
		PeakabooLog.init(DesktopApp.appDir("Logging"));
		
		PeakabooLog.get().log(Level.INFO, "Starting " + Version.longVersionNo + " - " + Version.buildDate);
		IconFactory.customPath = "/org/peakaboo/ui/swing/icons/";
		StratusLookAndFeel laf = new StratusLookAndFeel(new LightTheme());
		
		
		//warm up the peak table, which is lazy
		//do this in a separate thread so that it proceeds in parallel 
		//with all the other tasks, since this is usually the longest 
		//running init job
		Thread peakLoader = new Thread(() -> {
			PeakTable original = PeakTable.SYSTEM.getSource();
			String filename;
			if (Version.releaseType == ReleaseType.RELEASE) {
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
		
		Swidget.initialize(Version.splash, Version.logo, "Peakaboo", () -> {
			//Init settings store
			try {
				File settingsDir = DesktopApp.appDir("Settings");
				boolean firstSettings = !settingsDir.exists();
				Settings.init(settingsDir);
				if (firstSettings) {
					//This is the first time running a version of Peakaboo that uses the new
					//Druthers settings store. We'll try to load existing settings into it
					transferSettings();
				}
				
			} catch (IOException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to load persistent settings, Peakaboo must now exit.", e);
				System.exit(2);
			}
			
			setLaF(laf);
			EventfulConfig.uiThreadRunner = SwingUtilities::invokeLater;
			errorHook();
			startGCTimer();
			checkLowMemory();
			checkDevRelease();
			uiPerformanceTune();

			//Init plugins
			FilterPluginManager.init(DesktopApp.appDir("Plugins/Filter"));
			MapFilterPluginManager.init(DesktopApp.appDir("Plugins/MapFilter"));
			DataSourcePluginManager.init(DesktopApp.appDir("Plugins/DataSource"));
			DataSinkPluginManager.init(DesktopApp.appDir("Plugins/DataSink"));
			
			//Any additional plugin types provided per-tier
			Tier.provider().initializePlugins();
			

			try {
				peakLoader.join();
			} catch (InterruptedException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to start up properly, Peakaboo must now exit.", e);
				System.exit(2);
			}
			runPeakaboo();
		});
		
		
	}
	
	//TODO: Remove this in Peakaboo 6
	/**
	 * This method exists to transfer settings from the old
	 * method of storing them to the new one
	 */
	private static void transferSettings() {
		File oldFile = new File(DesktopApp.appDir() + "/settings.yaml");
		try {
			Map<String, Map<String, Boolean>> oldSettings = YamlSerializer.deserializeGeneric(oldFile);
			Map<String, Boolean> oldPersistent = oldSettings.get("persistent");
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.constantscale", 	oldPersistent.get("consistentScale"));
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.monochrome", 		oldPersistent.get("monochrome"));
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.fit.intensity", 	oldPersistent.get("showElementFitIntensities"));
			Settings.provider().setBoolean("org.peakaboo.controller.plotter.view.fit.markers", 	oldPersistent.get("showElementFitMarkers"));
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.fit.individual", 	oldPersistent.get("showIndividualFittings"));
		} catch (IOException e) {
			PeakabooLog.get().warning("Failed to transfer old settings");
		}
	}

	public static void main(String[] args)
	{	
		run();
	}


}
