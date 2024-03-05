package org.peakaboo.framework.stratus.api;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.components.dialogs.SplashScreen;
import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class Stratus {

	public static final String KEY_WINDOW_FOCUSED = "stratus-window-focus";
	public static final String KEY_BUTTON_BORDER_PAINTED = "stratus-button-border-painted";
	public static final String KEY_BUTTON_NOTIFICATION_DOT = "stratus-button-notification-dot";
	
    public enum ButtonState {
    	DISABLED,
    	ENABLED,
    	MOUSEOVER,
    	FOCUSED,
    	PRESSED,
    	DEFAULT,
    	SELECTED
    }
	

    
	private static SplashScreen splashWindow;
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
					removeSplash();
				});
			});
		} else {
			SwingUtilities.invokeLater(() -> {
				startupTasks.run();
			});
		}
		
	}
	
	public static void removeSplash() {
		if (splashWindow == null) return;
		splashWindow.setVisible(false);
	}
    
	public static Graphics2D g2d(Graphics g) {
		g = g.create();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		return g2d;

	}
    
    
	public static BufferedImage acceleratedImage(int width, int height) {
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		return config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

	public static boolean hasTheme() {
		return getTheme() != null;
	}
	
	public static Theme getTheme() {
		try {
			return (Theme) UIManager.get("stratus-theme");
		} catch (NullPointerException e) {
			return null;
		}
	}
		
	public static boolean focusedWindow(JComponent component) {
		if (component == null) return true;
		if (component.getRootPane() == null) return true;
		Object prop = component.getRootPane().getClientProperty(Stratus.KEY_WINDOW_FOCUSED);
		if (prop == null) {
			return true;
		}
		try {
			return (boolean)prop;
		} catch (ClassCastException e) {
			return true;
		}
	}
    
	public static JScrollPane scrolled(Component component) {
		JScrollPane scroller = new JScrollPane();
		scroller.setViewportView(component);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		return scroller;
	}		
	
	public static Font registerFont(String path) throws FontFormatException, IOException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font f = Font.createFont(Font.TRUETYPE_FONT, StratusLookAndFeel.class.getResourceAsStream(path));
		ge.registerFont(f);
		return f;
	}

	public static boolean lowGraphicsMode = false;
	
}
