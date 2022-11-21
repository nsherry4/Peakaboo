package org.peakaboo.framework.swidget;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.components.dialogs.SplashScreen;
import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;



public class asdfSwidget
{

	
	
	private static Semaphore initWaiter = new Semaphore(1);
	public static void initializeAndWait(String appName) {
		try {
			initWaiter.acquire();
			initialize(() -> {
				initWaiter.release();
			}, appName);
			initWaiter.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void initialize(String appName) {
		initialize(() -> {}, appName);
	}
	
	public static void initialize(Runnable startupTasks, String appName) {
		initialize(null, null, null, appName, startupTasks);
	}
	

	private static SplashScreen splashWindow;
	public static void initialize(String splashPath, String splashBackground, String splashIcon, String appName, Runnable startupTasks) {
		
		//Needed to work around https://bugs.openjdk.java.net/browse/JDK-8130400
		//NEED TO SET THESE RIGHT AT THE START BEFORE ANY AWT/SWING STUFF HAPPENS.
		//THAT INCLUDES CREATING ANY ImageIcon DATA FOR SPLASH SCREEN
		System.setProperty("sun.java2d.xrender", "false");
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
				
		if (splashBackground != null && splashIcon != null) {
			SwingUtilities.invokeLater(() -> {
				splashWindow = new SplashScreen(IconFactory.getImageIcon(splashPath, splashBackground), IconFactory.getImage(splashPath, splashIcon), appName);
				splashWindow.repaint();
				
				SwingUtilities.invokeLater(() -> {
					startupTasks.run();
					splashWindow.setVisible(false);
				});
			});
		} else {
			SwingUtilities.invokeLater(() -> {
				startupTasks.run();
			});
		}
		
	}

	public static boolean isNumbusDerivedLaF() {
		try {
			return NimbusLookAndFeel.class.isAssignableFrom(UIManager.getLookAndFeel().getClass());
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isStratusLaF() {
		try {
			return StratusLookAndFeel.class.isAssignableFrom(UIManager.getLookAndFeel().getClass()) && Stratus.hasTheme();
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}

	public static Color dividerColor() {
		Color dividerColour = null;
		if (Swidget.isStratusLaF()) {
			dividerColour = Stratus.getTheme().getWidgetBorder();
		}
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		return dividerColour;
	}

	
}



