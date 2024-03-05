package org.peakaboo.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.peakaboo.framework.eventful.EventfulBeacon;

public class RecentSessions extends EventfulBeacon {

	public static final RecentSessions SYSTEM = new RecentSessions();
	public static final String SETTINGS_KEY_RECENT_SESSIONS = "org.peakaboo.app.recent-sessions";
	
	public static final int SIZE = 5; 
	
	List<File> files;
	
	public RecentSessions() {
		files = new ArrayList<>();
		restore();
	}
	
	public void addSessionFile(File sessionFile) {
		if (files.contains(sessionFile)) {
			files.remove(sessionFile);
		}
		files.add(0, sessionFile);
		while (files.size() > SIZE) {
			files.remove(files.size()-1);
		}
		persist();
		updateListeners();
	}
	
	public Optional<File> getLastSessionFile() {
		if (files.size() == 0) {
			return Optional.empty();
		} else {
			return Optional.of(files.get(0));
		}
	}
	
	public List<File> getRecentSessionFiles() {
		return Collections.unmodifiableList(files);
	}
	
	private List<String> filenames() {
		return files.stream().map(File::getAbsolutePath).toList();
	}
	
	private void persist() {
		Settings.provider().setList(SETTINGS_KEY_RECENT_SESSIONS, filenames());
	}
	
	private void restore() {
		var names = Settings.provider().getList(SETTINGS_KEY_RECENT_SESSIONS, filenames());
		files = new ArrayList<>(names.stream().map(File::new).filter(File::exists).toList());
		persist();
	}
}
