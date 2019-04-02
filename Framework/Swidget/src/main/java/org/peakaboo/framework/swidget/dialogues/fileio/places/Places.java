package org.peakaboo.framework.swidget.dialogues.fileio.places;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

public interface Places {

	List<Place> getAll();

	default boolean has(File file) {
		return get(file) != null;
	}
	
	default Place get(File file) {
		for (Place d : getAll()) {
			if (d.getFile().equals(file)) {
				return d;
			}
		}
		return null;
	}
	
	default int index(File file) {
		for (int i = 0; i < getAll().size(); i++) {
			Place d = getAll().get(i);
			if (d.getFile().equals(file)) {
				return i;
			}
		}
		return -1;
	}
	
	static boolean supported() {
		return (get() != null);
	}
	
	static Places get() {
		FileSystemView v = FileSystemView.getFileSystemView();
		if (v.getClass().getSimpleName().equals("UnixFileSystemView")) {
			return new UnixPlaces();
		}
		if (v.getClass().getSimpleName().equals("WindowsFileSystemView")) {
			return new WindowsPlaces();
		}
		return null;
	}
	
}