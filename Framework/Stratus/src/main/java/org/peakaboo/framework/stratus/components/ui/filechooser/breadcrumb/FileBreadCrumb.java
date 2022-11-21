package org.peakaboo.framework.stratus.components.ui.filechooser.breadcrumb;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.peakaboo.framework.stratus.components.ui.breadcrumb.BreadCrumb;
import org.peakaboo.framework.stratus.components.ui.filechooser.places.Place;
import org.peakaboo.framework.stratus.components.ui.filechooser.places.Places;

public class FileBreadCrumb extends BreadCrumb<File> {

	private org.peakaboo.framework.stratus.components.ui.filechooser.places.Places places;
	
	public FileBreadCrumb(JFileChooser chooser) {
		this(chooser, Places.forPlatform());
	}
	
	public FileBreadCrumb(JFileChooser chooser, Places places) {
		super(FileBreadCrumb::formatStatic, f -> {
			if (!f.equals(chooser.getCurrentDirectory())) {
				chooser.setCurrentDirectory(f);
			}
		});
		
		this.places = places;
		
		this.setAlignment(BorderLayout.LINE_START);
		
		setEntryBuilder(item -> new FileBreadCrumbEntry(this, item, this::format));
		
		chooser.addPropertyChangeListener(l -> {
			File dir = chooser.getCurrentDirectory();
			this.setFile(dir);
		});
		
	}
	

	
	private static String formatStatic(File f) {
		Place place = Places.forPlatform().get(f);
		if (place != null && place.isRoot()) {
			return place.getName();
		} else {
			return FileSystemView.getFileSystemView().getSystemDisplayName(f);
		}
	}
	
	private String format(File f) {
		Place place = places.get(f);
		if (place != null && place.isRoot()) {
			return place.getName();
		} else {
			return FileSystemView.getFileSystemView().getSystemDisplayName(f);
		}
	}
	
	public void setFile(File f) {
		if (!f.isDirectory()) {
			f = f.getParentFile();
		}
		
		//If this File is already in the breadcrumb, don't reload everything, just change the selection
		if (contains(f)) {
			setSelected(f);
			return;
		}
		
		List<File> items = new ArrayList<>();
		File p = f;
		while (p != null) {
			items.add(0, p);
			if (places.has(p) && places.get(p).isRoot()) {
				break;
			}
			p = FileSystemView.getFileSystemView().getParentDirectory(p);
		}
		this.setAll(items, f);
	}


	Places getPlaces() {
		return places;
	}
	
	
	

}
