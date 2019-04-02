package org.peakaboo.framework.swidget.dialogues.fileio.places;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

public class WindowsPlaces implements Places {

	List<Place> places = new ArrayList<>();
	
	public WindowsPlaces() {
		String home = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		register(new File(home), "Home");
		register(new File(home+"/Desktop"), "Desktop");
		register(new File(home+"/Documents"), "Documents");
		register(new File(home+"/Downloads"), "Downloads");
		//register(new File(home+"/Music"), "Music");
		register(new File(home+"/Pictures"), "Pictures");
		//register(new File(home+"/Videos"), "Videos");
	}
	
	private void register(File file, String name) {
		if (!file.exists()) {
			return;
		}
		if (!file.canRead()) {
			return;
		}
		places.add(new BookmarkPlace(file, name));
	}
	
	
	@Override
	public List<Place> getAll() {
		return Collections.unmodifiableList(places);
	}

}
