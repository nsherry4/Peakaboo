package org.peakaboo.framework.swidget.widgets.filechooser.breadcrumb;

import java.io.File;
import java.util.function.Function;

import org.peakaboo.framework.swidget.widgets.breadcrumb.BreadCrumbEntry;
import org.peakaboo.framework.swidget.widgets.filechooser.places.Place;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentToggleButton;

public class FileBreadCrumbEntry extends BreadCrumbEntry<File> {

	public FileBreadCrumbEntry(FileBreadCrumb parent, File item, Function<File, String> formatter) {
		super(parent, item, formatter);
	}

	@Override
	protected FluentToggleButton make() {
		FileBreadCrumb parent = (FileBreadCrumb) super.parent;
		Place dir = parent.getPlaces().get(getItem());
		FluentToggleButton button = super.make();
		if (dir != null && dir.isRoot()) {
			//not a good idea -- the ImageButton may regenerate it's UI based on internal state 
			button.setIcon(dir.getIcon());
		}
		return button;
	}
	
}
