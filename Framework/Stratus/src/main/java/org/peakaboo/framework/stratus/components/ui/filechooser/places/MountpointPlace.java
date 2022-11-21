package org.peakaboo.framework.stratus.components.ui.filechooser.places;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;

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