package org.peakaboo.framework.swidget.dialogues.fileio.places;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

class BookmarkPlace implements Place {
	
	private String name;
	private File file;
	
	public BookmarkPlace(File file, String name) {
		this.name = name;
		this.file = file;
	}

	@Override
	public Icon getIcon() {
		if (getName().equals("Home")) {
			return StockIcon.PLACE_HOME.toImageIcon(IconSize.BUTTON);
		}
		if (getName().equals("Desktop")) {
			return StockIcon.PLACE_DESKTOP.toImageIcon(IconSize.BUTTON);
		}
		return FileSystemView.getFileSystemView().getSystemIcon(file);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean isRoot() {
		return getName().equals("Home");
	}
	
}