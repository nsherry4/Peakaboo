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

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.StratusLookAndFeel;
import org.peakaboo.framework.swidget.dialogues.SplashScreen;
import org.peakaboo.framework.swidget.icons.IconFactory;



public class Swidget
{

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
	
	public static void initialize(Runnable startupTasks, String appName)
	{
		initialize(null, null, appName, startupTasks);
	}
	
	
	public static void initialize(String splashBackground, String splashIcon, String appName, Runnable startupTasks)
	{
		
		//Needed to work around https://bugs.openjdk.java.net/browse/JDK-8130400
		//NEED TO SET THESE RIGHT AT THE START BEFORE ANY AWT/SWING STUFF HAPPENS.
		//THAT INCLUDES CREATING ANY ImageIcon DATA FOR SPLASH SCREEN
		System.setProperty("sun.java2d.xrender", "false");
		System.setProperty("sun.java2d.pmoffscreen", "false");
		
				
		if (splashBackground != null && splashIcon != null) {
			SwingUtilities.invokeLater(() -> {
				splashWindow = new SplashScreen(IconFactory.getImageIcon(splashBackground), IconFactory.getImage(splashIcon), appName);
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
	
	public static String lineWrap(Component c, String text) {
		return lineWrap(c, text, 400);
	}
	
	public static String lineWrap(Component c, String text, int width) {
		if (text.contains("\n")) {
			String[] lines = text.split("\n");
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(lineWrap(c, line, width));
				sb.append("\n");
			}
			return sb.toString();
		}
		
		List<String> lines = new ArrayList<String>();
		
		Font font = c.getFont();
		FontMetrics metrics = c.getFontMetrics(font);
				
		String line = "";
		Graphics g = c.getGraphics();
		
		List<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
		
		
		lines.clear();
		while (words.size() > 0)
		{
		
			while ( metrics.getStringBounds(line, g).getWidth() < width )
			{
				if (words.size() == 0) break;
				if (!line.equals("")) line += " ";
				line = line + words.remove(0);
			}
			
			lines.add(line);
			line = "";
			
		}
		
		Optional<String> str = lines.stream().reduce((a, b) -> a + "\n" + b);
		return str.orElse("");
	}
	
	
	public static String lineWrapHTML(Component c, String text) {
		return "<html>" + lineWrap(c, text).replace("\n", "<br/>") + "</html>";
	}
	
	public static String lineWrapHTML(Component c, String text, int width) {
		return "<html>" + lineWrap(c, text, width).replace("\n", "<br/>") + "</html>";
	}
	
	
	public static void main(String[] args)
	{
		
	}
	
	public static Logger logger() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}

	public static final int DEFAULT_TEXTWRAP_WIDTH = 300;

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



