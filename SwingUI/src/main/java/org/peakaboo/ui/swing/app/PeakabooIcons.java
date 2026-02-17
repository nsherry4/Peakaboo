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
	OPTIONS_ERRORS,
	
	MAP,
	PLOT,
	
	AUTO,
	FILTER,
	PLUGIN_SYMBOLIC,

	SELECT_RECTANGULAR,
	SELECT_ELLIPSE,
	SELECT_POLYGON,
	SELECT_LASSO,
	SELECT_CONTINUOUS_AREA,

	
	MENU_VIEW,
	MENU_ENERGY,
	MENU_SETTINGS,
	MENU_PALETTE,
	MENU_PLUGIN,
	MENU_MAIN,
	
	;

	public static final String ASSET_PATH = "/org/peakaboo/ui/swing/icons/";
	
	@Override
	public String path() {
		return ASSET_PATH;
	}

}
