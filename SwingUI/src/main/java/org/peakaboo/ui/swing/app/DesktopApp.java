package org.peakaboo.ui.swing.app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.peakaboo.framework.accent.Platform;
import org.peakaboo.app.Version;
import org.peakaboo.framework.accent.log.OneLog;


public class DesktopApp {

	private DesktopApp() {
		//Not Constructable
	}
	
	public static void browser(String rawUrl) {
		//Check for http instead of http:// so that we don't add it to https:// urls
		String url = rawUrl.toLowerCase().startsWith("http") ? rawUrl : "http://" + rawUrl;
		
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(url));
		} catch (UnsupportedOperationException | IOException | URISyntaxException e1) {
			openDocument(url);
		}

		
	}
	

	private static void openDocument(final String location) {
		
		final String ERROR = "Failed to Open Document";
		
		switch (Platform.getOS()) {
			case WINDOWS:
				try {
					//proper way of launching a webpage viewer
					Runtime.getRuntime().exec("start " + location);
				}
				catch (IOException e) {
					OneLog.log(Level.SEVERE, ERROR, e);
				}
				break;
				
			case MAC:
				
				try {
					//proper way of launching a webpage viewer
					Runtime.getRuntime().exec("open " + location);
				}
				catch (IOException e) {
					OneLog.log(Level.SEVERE, ERROR, e);
				}
				break;
			
			case UNIX:
			case OTHER:
			default:
				
				try {
					//proper way of launching a webpage viewer
					Runtime.getRuntime().exec("xdg-open " + location);
				} catch (IOException e) {
					OneLog.log(Level.SEVERE, ERROR, e);
				}
				break;

			
		}

	}
	
	public static File appDir() {
		return Platform.appDir(Version.PROGRAM_NAME);
	}
	public static File appDir(String subdir) {
		return Platform.appDirEntry(Version.PROGRAM_NAME, subdir);
	}
	
}

