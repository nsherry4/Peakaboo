package org.peakaboo.framework.stratus.components.ui.filechooser.places;

import java.io.File;

import javax.swing.Icon;

import org.peakaboo.framework.stratus.api.icons.IconSize;

public interface Place {
	Icon getIcon(IconSize size);
	String getName();
	File getFile();
	boolean isRoot();
}