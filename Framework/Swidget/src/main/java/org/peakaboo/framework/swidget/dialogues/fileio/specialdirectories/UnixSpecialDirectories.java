package org.peakaboo.framework.swidget.dialogues.fileio.specialdirectories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

public class UnixSpecialDirectories implements SpecialDirectories {
	
	Map<String, String> aliases = new HashMap<>(); {
		aliases.put("XDG_DESKTOP_DIR", "Desktop");
		aliases.put("XDG_DOCUMENTS_DIR", "Documents");
		aliases.put("XDG_DOWNLOAD_DIR", "Downloads");
		aliases.put("XDG_PUBLICSHARE_DIR", "Public");
		aliases.put("XDG_MUSIC_DIR", "Music");
		aliases.put("XDG_PICTURES_DIR", "Pictures");
		aliases.put("XDG_VIDEOS_DIR", "Videos");
	}
	Map<File, String> roots = new LinkedHashMap<>();

	@Override
	public Map<File, String> getRoots() {
		return Collections.unmodifiableMap(roots);
	}
	

	public UnixSpecialDirectories() {
		String home = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		register(new File(home), "Home");
		
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
	}
	
	
	private void register(File file, String name) {
		if (!file.exists()) {
			return;
		}
		roots.put(file, name);
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
				register(new File(rootPath), alias);
			}
		}
	}
	
	private void loadFallback(String home) {
		register(new File(home + "/Desktop"), "Desktop");
		register(new File(home + "/Documents"), "Documents");
		register(new File(home + "/Downloads"), "Downloads");
		register(new File(home + "/Music"), "Music");
		register(new File(home + "/Pictures"), "Pictures");
		register(new File(home + "/Videos"), "Videos");
	}
	
}