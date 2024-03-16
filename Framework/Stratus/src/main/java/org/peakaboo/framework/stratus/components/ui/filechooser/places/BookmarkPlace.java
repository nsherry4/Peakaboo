package org.peakaboo.framework.stratus.components.ui.filechooser.places;

import java.io.File;

import javax.swing.Icon;

import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;

class BookmarkPlace implements Place {
	
	private String name;
	private File file;
	private StockIcon home, desktop, folder;
	
	public BookmarkPlace(File file, String name) {
		this.name = name;
		this.file = file;
		this.home = StockIcon.PLACE_HOME;
		this.desktop = StockIcon.PLACE_DESKTOP;
		this.folder = StockIcon.PLACE_FOLDER;
	}

	@Override
	public Icon getIcon(IconSize size) {
		if (getName().equals("Home")) {
			return home.toImageIcon(size);
		}
		if (getName().equals("Desktop")) {
			return desktop.toImageIcon(size);
		}
		return folder.toImageIcon(size);
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