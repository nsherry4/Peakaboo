package org.peakaboo.controller.plotter.view;



import java.io.Serializable;


// Holds settings related to the way the data is presented to the user.
// This is here, rather than in the view because the drawing of the plot
// is actually executed in the controller so that it may set up the
// appropriate DrawingExtensions. Does not include information regarding
// how to draw the plot, although it does contain settings about which
// data to plot
public class ViewModel implements Serializable
{
	
	public PersistentViewModel persistent;
	public SessionViewModel session;


	public ViewModel()
	{
		persistent = new PersistentViewModel();
		session = new SessionViewModel();
	}



}
