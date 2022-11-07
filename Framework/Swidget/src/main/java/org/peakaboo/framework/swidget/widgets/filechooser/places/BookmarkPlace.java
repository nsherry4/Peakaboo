package org.peakaboo.framework.swidget.widgets.filechooser.places;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

class BookmarkPlace implements Place {
	
	private String name;
	private File file;
	private Icon home, desktop;
	
	public BookmarkPlace(File file, String name) {
		this.name = name;
		this.file = file;
		this.home = StockIcon.PLACE_HOME.toImageIcon(IconSize.BUTTON);
		this.desktop = StockIcon.PLACE_DESKTOP.toImageIcon(IconSize.BUTTON);
	}

	@Override
	public Icon getIcon() {
		if (getName().equals("Home")) {
			return home;
		}
		if (getName().equals("Desktop")) {
			return desktop;
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