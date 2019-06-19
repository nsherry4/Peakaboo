package org.peakaboo.framework.swidget.widgets.filechooser.places;

import java.io.File;

import javax.swing.Icon;

public interface Place {
	Icon getIcon();
	String getName();
	File getFile();
	boolean isRoot();
}