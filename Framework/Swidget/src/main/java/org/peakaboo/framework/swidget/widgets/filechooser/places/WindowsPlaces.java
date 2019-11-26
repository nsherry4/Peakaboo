package org.peakaboo.framework.swidget.widgets.filechooser.places;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.framework.swidget.Swidget;

class WindowsPlaces implements Places {

	List<Place> places = new ArrayList<>();
	static Map<String, Optional<File>> lookups = new HashMap<>();
	
	public WindowsPlaces() {
		addBookmark(env("UserProfile"), "Home");
		addBookmark(reg("Desktop"), "Desktop");
		addBookmark(reg("personal"), "Documents");
		addBookmark(reg("{374DE290-123F-4565-9164-39C4925E467B}"), "Downloads");
		addBookmark(reg("My Pictures"), "Pictures"); 
		//register(reg("My Music"), "Music");
		//register(reg("My Videos"), "Videos");
		
		//disks/filesystems
		for (File f : File.listRoots()) {
			addMountpoint(f);
		}

	}
	
	private Optional<File> env(String var) {
		if (!lookups.keySet().contains(var)) {
			try {
			    String dirname = System.getenv("UserProfile");
			    File dir = new File(dirname);
			    if (dir.exists()) {
			    	lookups.put(var, Optional.of(dir));
			    } else {
			    	lookups.put(var, Optional.empty());	
			    }
				
			} catch (Exception e) {
				Swidget.logger().log(Level.WARNING, "Could not look up value in registry", e);
				lookups.put(var, Optional.empty());
			}
		}
		return lookups.get(var);
	}
	
	private Optional<File> reg(String key) {
		if (!lookups.keySet().contains(key)) {
			try {
				Process p = Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v \"" + key + "\"");
				p.waitFor();
	
			    InputStream in = p.getInputStream();
			    byte[] b = new byte[in.available()];
			    in.read(b);
			    in.close();
	
			    String dirname = new String(b);
			    System.out.println(dirname);
			    System.out.flush();
			    String parts[] = dirname.split("\\s\\s+");
			    System.out.println(parts.toString());
			    dirname = parts[parts.length-1];
			    File dir = new File(dirname);
			    if (dir.exists()) {
			    	lookups.put(key, Optional.of(dir));
			    } else {
			    	lookups.put(key, Optional.empty());	
			    }
				
			} catch (Exception e) {
				Swidget.logger().log(Level.WARNING, "Could not look up value in registry", e);
				lookups.put(key, Optional.empty());
			}
		}
		return lookups.get(key);
	}
	
	private void addBookmark(Optional<File> fileopt, String name) {
		if (!fileopt.isPresent()) {
			return;
		}
		addBookmark(fileopt.get(), name);
	}
	
	
	private void addBookmark(File file, String name) {
		if (!file.exists()) {
			return;
		}
		places.add(new BookmarkPlace(file, name));
	}
	
	private void addMountpoint(File file) {
		if (!file.exists()) {
			return;
		}
		if (!file.canRead()) {
			return;
		}
		places.add(new MountpointPlace(file));
	}
	
	@Override
	public List<Place> getAll() {
		return Collections.unmodifiableList(places);
	}

}
