package org.peakaboo.ui.swing.environment;

import org.peakaboo.framework.swidget.icons.IconSet;

public enum PeakabooIcons implements IconSet {
	
	OPTIONS_DETECTOR,
	OPTIONS_PEAKMODEL,
	OPTIONS_CURVEFIT,
	OPTIONS_SOLVER,
	OPTIONS_APP,
	
	MAP,
	PLOT,
	
	AUTO,
	FILTER,
	
	SELECT_CONTINUOUS_AREA,
	SELECT_ELLIPSE,
	SELECT_LASSO,
	SELECT_RECTANGULAR,
	
	MENU_VIEW,
	MENU_ENERGY,
	
	;

	public static final String PATH = "/org/peakaboo/ui/swing/icons/";
	
	@Override
	public String path() {
		return PATH;
	}

}
