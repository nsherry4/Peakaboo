package org.peakaboo.framework.swidget.dialogues.fileio.places;

import java.io.File;

import javax.swing.Icon;

interface Place {
	Icon getIcon();
	String getName();
	File getFile();
}