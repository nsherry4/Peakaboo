package org.peakaboo.framework.swidget.dialogues.fileio.specialdirectories;

import java.io.File;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

public interface SpecialDirectories {

	Map<File, String> getRoots();

	default Icon getIcon(File file) {
		if (getName(file).equals("Home")) {
			return StockIcon.PLACE_HOME.toImageIcon(IconSize.BUTTON);
		}
		if (getName(file).equals("Desktop")) {
			return StockIcon.PLACE_DESKTOP.toImageIcon(IconSize.BUTTON);
		}
		return FileSystemView.getFileSystemView().getSystemIcon(file);
	}
	
	default String getName(File file) {
		if (getRoots().containsKey(file)) {
			return getRoots().get(file);
		}
		return file.getName();
	}
	
	static boolean supported() {
		return (get() != null);
	}
	
	static SpecialDirectories get() {
		FileSystemView v = FileSystemView.getFileSystemView();
		if (v.getClass().getSimpleName().equals("UnixFileSystemView")) {
			return new UnixSpecialDirectories();
		}
		if (v.getClass().getSimpleName().equals("WindowsFileSystemView")) {
			return new WindowsSpecialDirectories();
		}
		return null;
	}
	
}