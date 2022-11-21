package org.peakaboo.framework.stratus.components.ui.filechooser.places;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

class UnixPlaces implements Places {
	
	Map<String, String> aliases = new HashMap<>(); {
		aliases.put("XDG_DESKTOP_DIR", "Desktop");
		aliases.put("XDG_DOCUMENTS_DIR", "Documents");
		aliases.put("XDG_DOWNLOAD_DIR", "Downloads");
		//aliases.put("XDG_PUBLICSHARE_DIR", "Public");
		//aliases.put("XDG_MUSIC_DIR", "Music");
		aliases.put("XDG_PICTURES_DIR", "Pictures");
		//aliases.put("XDG_VIDEOS_DIR", "Videos");
	}
	//Map<File, String> roots = new LinkedHashMap<>();
	List<Place> places = new ArrayList<>();
	

	@Override
	public List<Place> getAll() {
		return Collections.unmodifiableList(places);
	}
	

	public UnixPlaces() {
		String home = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		addBookmark(new File(home), "Home");
		
		/*
		 * XDG is a FreeDesktop/Linux mechanism for name-mapping directories to special
		 * bookmarks. We try this first, then fall back to hard-coded values, since some
		 * unix systems like macos won't have this.
		 */
		try {
			loadXDG(home);
		} catch (IOException e) {
			loadFallback(home);
		}

		//filesystems
		addMountpoint(new File("/"));
		for (FileStore fs : FileSystems.getDefault().getFileStores()) {
			if (fs.isReadOnly()) {
				continue;
			}
			
			String path = fs.toString().split(" ")[0];
			if (
					path.startsWith("/Volume") || 
					path.startsWith("/mnt") ||
					path.startsWith("/media") ||
					path.startsWith(home)
				) {
				File mountpoint = new File(path);
				addMountpoint(mountpoint);
			}
			
			
			
		}
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
	

	private void loadXDG(String home) throws IOException {
		String configFile = home + "/.config/user-dirs.dirs";
		List<String> lines = Files.readAllLines(new File(configFile).toPath());
		lines = lines.stream()
			.map(l -> l.trim())
			.filter(l -> !l.startsWith("#"))
			.filter(l -> l.length() != 0)
			.collect(Collectors.toList());
		
		lines.sort(String::compareTo);
		for (String line : lines) {
			String[] parts = line.split("=");
			if (parts.length != 2) {
				continue;
			}
			if (aliases.containsKey(parts[0])) {
				String rootPath = parts[1];
				//remove quotation marks
				rootPath = rootPath.substring(1, rootPath.length()-1);
				
				String alias = aliases.get(parts[0]);
				if (rootPath.startsWith("$HOME")) {
					rootPath = rootPath.replaceAll("\\$HOME", home);
				} else if (rootPath.startsWith("/")) {
					//absolute path, do nothing
				} else {
					//invalid, skip
					continue;
				}
				addBookmark(new File(rootPath), alias);
			}
		}
	}
	
	private void loadFallback(String home) {
		addBookmark(new File(home + "/Desktop"), "Desktop");
		addBookmark(new File(home + "/Documents"), "Documents");
		addBookmark(new File(home + "/Downloads"), "Downloads");
		addBookmark(new File(home + "/Music"), "Music");
		addBookmark(new File(home + "/Pictures"), "Pictures");
		addBookmark(new File(home + "/Videos"), "Videos");
	}
	
}
