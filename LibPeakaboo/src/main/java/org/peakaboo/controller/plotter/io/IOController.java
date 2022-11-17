package org.peakaboo.controller.plotter.io;

import java.io.File;

import org.peakaboo.app.Env;
import org.peakaboo.framework.eventful.Eventful;

public class IOController extends Eventful {

	private File sessionFile;
	private File lastFolder;
	
	public IOController() {
		lastFolder = Env.homeDirectory();
		sessionFile = null;
	}
	
	public File getSessionFile() {
		return sessionFile;
	}
	public void setSessionFile(File sessionFile) {
		this.sessionFile = sessionFile;
		updateListeners();
	}
	public File getSessionFolder() {
		if (sessionFile == null) {
			return lastFolder;
		}
		return sessionFile.getParentFile();
	}
	
	public File getLastFolder() {
		return lastFolder;
	}
	public void setLastFolder(File lastFolder) {
		this.lastFolder = lastFolder;
		updateListeners();
	}
	
	public void setBothFromSession(File session) {
		this.sessionFile = session;
		this.lastFolder = session.getParentFile();
		updateListeners();
	}

	
	
}
