package org.peakaboo.framework.swidget.dialogues.fileio.specialdirectories;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;

public class WindowsSpecialDirectories implements SpecialDirectories {

	Map<File, String> roots = new LinkedHashMap<>();
	
	public WindowsSpecialDirectories() {
		String home = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		register(new File(home), "Home");
		register(new File(home+"/Desktop"), "Desktop");
		register(new File(home+"/Documents"), "Documents");
		register(new File(home+"/Downloads"), "Downloads");
		register(new File(home+"/Music"), "Music");
		register(new File(home+"/Pictures"), "Pictures");
		register(new File(home+"/Videos"), "Videos");
	}
	
	private void register(File file, String name) {
		if (!file.exists()) {
			return;
		}
		roots.put(file, name);
	}
	
	
	@Override
	public Map<File, String> getRoots() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableMap(roots);
	}

}
