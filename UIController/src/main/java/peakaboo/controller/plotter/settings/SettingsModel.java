package peakaboo.controller.plotter.settings;



import java.io.Serializable;

import peakaboo.curvefit.peak.escape.EscapePeakType;
import scidraw.drawing.ViewTransform;


// Holds settings related to the way the data is presented to the user.
// This is here, rather than in the view because the drawing of the plot
// is actually executed in the controller so that it may set up the
// appropriate DrawingExtensions. Does not include information regarding
// how to draw the plot, although it does contain settings about which
// data to plot
public class SettingsModel implements Serializable
{
	
	public PersistentSettingsModel persistent;
	public SessionSettingsModel session;


	public SettingsModel()
	{
		persistent = new PersistentSettingsModel();
		session = new SessionSettingsModel();
	}

	public void copy(SettingsModel copy)
	{
		persistent = new PersistentSettingsModel(copy.persistent);
		session = new SessionSettingsModel(copy.session);		
	}

}
