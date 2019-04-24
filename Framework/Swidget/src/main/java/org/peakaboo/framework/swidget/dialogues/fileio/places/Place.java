package org.peakaboo.framework.swidget.dialogues.fileio.places;

import java.io.File;

import javax.swing.Icon;

public interface Place {
	Icon getIcon();
	String getName();
	File getFile();
	boolean isRoot();
}