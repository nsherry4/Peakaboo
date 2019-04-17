package org.peakaboo.framework.swidget.dialogues.fileio.places;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

class MountpointPlace implements Place {
	
	private File mountpoint;
	
	public MountpointPlace(File mountpoint) {
		this.mountpoint = mountpoint;
	}

	@Override
	public Icon getIcon() {
		return StockIcon.DEVICE_HARDDISK.toImageIcon(IconSize.BUTTON);
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