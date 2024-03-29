package org.peakaboo.ui.swing;

import java.awt.Color;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.peakaboo.app.Env;
import org.peakaboo.app.PeakabooConfiguration;
import org.peakaboo.app.PeakabooConfiguration.MemorySize;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Settings;
import org.peakaboo.app.Version;
import org.peakaboo.app.Version.ReleaseType;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.table.SerializedPeakTable;
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.cyclops.Mutable;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.CyclopsSurface;
import org.peakaboo.framework.eventful.EventfulConfig;
import org.peakaboo.framework.plural.monitor.SimpleTaskMonitor;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.FileDrop;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;
import org.peakaboo.framework.stratus.laf.theme.Theme;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.AccentedBrightTheme;
import org.peakaboo.ui.swing.app.AccentedDuskTheme;
import org.peakaboo.ui.swing.app.CrashHandler;
import org.peakaboo.ui.swing.app.DesktopApp;
import org.peakaboo.ui.swing.app.DesktopSettings;
import org.peakaboo.ui.swing.plotting.PlotFrame;



public class Peakaboo {
	private static Timer gcTimer;
	
	private static void checkDevRelease() {
		if (Version.RELEASE_TYPE != ReleaseType.RELEASE){
			String message = "This build of Peakaboo is not a final release version.\nAny results you obtain should be treated accordingly.";
			String title = "Development Build of Peakaboo";
			if (Version.RELEASE_TYPE == ReleaseType.CANDIDATE) {
				title = "Release Candidate for Peakaboo";
			}
			
			new LayerDialog(title, message, StockIcon.BADGE_INFO).showInWindow(null, true);
			
		}
	}
	
	private static void checkLowMemory() {
		PeakabooLog.get().log(Level.INFO, "Max heap size = " + Env.maxHeap() + "MB");
		
		if (PeakabooConfiguration.memorySize == MemorySize.TINY){
			String message = "This system's Java VM is only allocated " + Env.maxHeap()
			+ "MB of memory.\nProcessing large data sets may be quite slow, if not impossible.";
			String title = "Low Memory";
						
			new LayerDialog(title, message, StockIcon.BADGE_INFO).showInWindow(null, true);
		}
	}
	
	private static void showPeakabooMainWindow() {

		//Any errors that don't get handled anywhere else come here and get shown
		//to the user and printed to standard out.
		try {
			new PlotFrame();
		} catch (Throwable e) {
			Stratus.removeSplash();
			PeakabooLog.get().log(Level.SEVERE, "Peakaboo has encountered a problem and must exit", e);
			System.exit(1);
		}
		
	}
	
	private static void errorHook() {
		

		
		//Set error handler that shows a popup
		PeakabooLog.getRoot().addHandler(new Handler() {
			
			@Override
			public void publish(LogRecord entry) {
				if (entry.getLevel() == Level.SEVERE) {
					Throwable t = entry.getThrown();
					String m = entry.getMessage();
					
					if (t == null && m.startsWith("\tat ")) {
						return;
					}
					
					CrashHandler.get().handle(m, t);
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
		if (PeakabooConfiguration.memorySize == MemorySize.TINY) {
			Stratus.lowGraphicsMode = true;
		}
	}


	public static void init() {
		//Needed to work around https://bugs.openjdk.java.net/browse/JDK-8130400
		//NEED TO SET THESE RIGHT AT THE START BEFORE ANY AWT/SWING STUFF HAPPENS.
		//THAT INCLUDES CREATING ANY ImageIcon DATA FOR SPLASH SCREEN
		System.setProperty("sun.java2d.xrender", "false");
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
		DesktopSettings.init();
		PeakabooLog.init(DesktopApp.appDir("Logging"));
		CrashHandler.init();
		
		PeakabooLog.get().log(Level.INFO, "Peakaboo is starting up.");
		PeakabooLog.get().log(Level.INFO, "This is " + Tier.provider().appName() + " version " + Version.LONG_VERSION + " - " + Version.buildDate);
		
		CyclopsSurface.init();
		
		//Configure the system thread pool to follow the Peakaboo user setting for thread count
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + Settings.getThreadCount());
	}
	
	public static void run() {

		//warm up the peak table, which is lazy
		//do this in a separate thread so that it proceeds in parallel 
		//with all the other tasks, since this is usually the longest 
		//running init job
		Thread peakLoader = new Thread(Peakaboo::initPeakTable);
		peakLoader.setDaemon(true);
		peakLoader.start();
		
		Stratus.initialize(Tier.provider().iconPath(), Version.SPLASH, Version.LOGO, "Peakaboo", () -> {

			Color accent = AccentedBrightTheme.accentColours.get(DesktopSettings.getAccentColour());
			if (accent == null) {
				accent = AccentedBrightTheme.accentColours.get("Blue");
			}
			Theme theme = new AccentedBrightTheme(accent);
			if (DesktopSettings.isDarkMode()) {
				theme = new AccentedDuskTheme(accent);
			}
			StratusLookAndFeel laf = new StratusLookAndFeel(theme);
			
			//Load Peakaboo's font
			try {
				Stratus.registerFont("/org/peakaboo/ui/swing/fonts/springsteel-lig.otf");
			} catch (FontFormatException | IOException e) {
				PeakabooLog.get().log(Level.WARNING, "", e);
			}
			
			
			setLaF(laf);
			EventfulConfig.uiThreadRunner = SwingUtilities::invokeLater;
			errorHook();
			startGCTimer();
			checkLowMemory();
			checkDevRelease();
			uiPerformanceTune();
		
			//Init plugins
			initPluginSystem();
						
			try {
				peakLoader.join();
			} catch (InterruptedException e) {
				Stratus.removeSplash();
				PeakabooLog.get().log(Level.SEVERE, "Failed to start up properly, Peakaboo must now exit.", e);
				System.exit(3);
			}
			showPeakabooMainWindow();
		});
		
		
	}
	
	/** 
	 * Performs the same final setup that the run() method does without initializing any GUI components
	 */
	public static void runHeadless() {
		initPeakTable();
		initPluginSystem();
	}
	
	private static void initPeakTable() {
		PeakTable original = PeakTable.SYSTEM.getSource();
		String filename;
		if (Version.RELEASE_TYPE == ReleaseType.RELEASE) {
			filename = "derived-peakfile-" + Version.LONG_VERSION + ".dat";
		} else {
			filename = "derived-peakfile-" + Version.LONG_VERSION + "-" + Version.buildDate + ".dat";
		}
		File peakdir = DesktopApp.appDir("PeakTable");
		peakdir.mkdirs();
		File peakfile = new File(DesktopApp.appDir("PeakTable") + "/" + filename);
		
		PeakTable.SYSTEM.setSource(new SerializedPeakTable(original, peakfile));
	}
	
	private static void initPluginSystem() {
		FilterRegistry.init(DesktopApp.appDir("Plugins/Filter"));
		MapFilterRegistry.init(DesktopApp.appDir("Plugins/MapFilter"));
		DataSourceRegistry.init(DesktopApp.appDir("Plugins/DataSource"));
		DataSinkRegistry.init(DesktopApp.appDir("Plugins/DataSink"));
		CurveFitterRegistry.init();
		FittingSolverRegistry.init();
		ChannelViewModeRegistry.init();
		FittingFunctionRegistry.init();
		
		//Any additional plugin types provided per-tier
		Tier.provider().initializePlugins();
	}


	public static void main(String[] args) {	
		init();
		run();
	}

	//TODO: is there a better place for this code to live?
	public static TaskMonitor<List<File>> getUrlsAsync(List<URL> urls, Consumer<Optional<List<File>>> callback) {
		
		Mutable<SimpleTaskMonitor<List<File>>> monitor = new Mutable<>();
		
		Supplier<List<File>> supplier = () -> {
			Mutable<Float> count = new Mutable<>(0f);
			List<File> files = new ArrayList<>();
			Consumer<Float> urlProgress = (Float percent) -> {
				float total = (count.get() + percent) / ((float)urls.size());
				monitor.get().setPercent(total);
			};
			for (URL url : urls) {
				try {
					File f = FileDrop.getUrlAsFile(url, urlProgress);
					files.add(f);
				} catch (IOException e) {
					PeakabooLog.get().log(Level.SEVERE, "Failed to download file " + url.toString());
					return null;
				}
				count.set(count.get()+1);
			}
			return files;
		};
		monitor.set(new SimpleTaskMonitor<>("Downloading Files", supplier, callback));
		return monitor.get();
	}


}
