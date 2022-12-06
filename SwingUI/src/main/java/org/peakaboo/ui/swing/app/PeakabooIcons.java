package org.peakaboo.ui.swing.app;

import org.peakaboo.framework.stratus.api.icons.IconSet;

public enum PeakabooIcons implements IconSet {
	
	OPTIONS_DETECTOR,
	OPTIONS_PEAKMODEL,
	OPTIONS_CURVEFIT,
	OPTIONS_SOLVER,
	OPTIONS_APP,
	OPTIONS_PERFORMANCE,
	OPTIONS_APPEARANCE,
	
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
	MENU_SETTINGS
	
	;

	public static final String PATH = "/org/peakaboo/ui/swing/icons/";
	
	@Override
	public String path() {
		return PATH;
	}

}
