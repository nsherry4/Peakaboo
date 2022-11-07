package org.peakaboo.framework.swidget.widgets.filechooser.places;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

class MountpointPlace implements Place {
	
	private File mountpoint;
	private Icon icon;
	
	public MountpointPlace(File mountpoint) {
		this.mountpoint = mountpoint;
		icon = StockIcon.DEVICE_HARDDISK.toImageIcon(IconSize.BUTTON);
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getName() {
		if (mountpoint.getAbsolutePath().equals("/")) {
			return "Filesystem";
		}
		return FileSystemView.getFileSystemView().getSystemDisplayName(mountpoint);
	}

	@Override
	public File getFile() {
		return mountpoint;
	}

	@Override
	public boolean isRoot() {
		return true;
	}
	
}