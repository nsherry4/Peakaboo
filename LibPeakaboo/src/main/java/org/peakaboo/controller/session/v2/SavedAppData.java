package org.peakaboo.controller.session.v2;

import org.peakaboo.app.Version;
import org.peakaboo.tier.Tier;

public class SavedAppData {

	public String version;
	public String tier;
	public String name;
	
	public SavedAppData() {

	}
	
	public static SavedAppData current() {
		var app = new SavedAppData();
		app.version = Version.LONG_VERSION;
		app.name = Version.PROGRAM_NAME;
		app.tier = Tier.provider().tierName();
		return app;
	}
	
}
