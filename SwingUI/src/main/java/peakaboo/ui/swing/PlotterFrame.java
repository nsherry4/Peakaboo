package peakaboo.ui.swing;


import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;

import javax.swing.JFrame;

import peakaboo.common.Version;
import peakaboo.datasource.model.DataSource;
import peakaboo.ui.swing.container.FrameContainer;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.IconFactory;



/**
 * This class is the main window for Peakaboo, the plotting window
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotterFrame extends JFrame
{

	public PlotPanel plotPanel;

	private static int openWindows = 0;
	
	public PlotterFrame()
	{
		
		openWindows++;
		
		setIconImage(IconFactory.getImage(Version.icon));
		setPreferredSize(new Dimension(1000, 470));
		
		setTitle(Version.title);
		
		plotPanel = new PlotPanel(new FrameContainer(this));
		getContentPane().add(plotPanel);
		
		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e)
			{}
		
			public void windowIconified(WindowEvent e)
			{}
		
			public void windowDeiconified(WindowEvent e)
			{}
		
			public void windowDeactivated(WindowEvent e)
			{}
			
			public void windowClosing(WindowEvent e)
			{
				openWindows--;
				if (openWindows == 0) System.exit(0);
			}
			
			public void windowClosed(WindowEvent e)
			{}
			
			public void windowActivated(WindowEvent e)
			{}
		});
		
		// Display the window.
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	public PlotterFrame(DataSource ds, InputStream sessionData)
	{
	
		this();
		
		
		
		//create a new datasource which is a subset of the passed one
		plotPanel.getController().data().setDataSource(ds);
		
		plotPanel.getController().loadPreferences(sessionData, false);
		
		//TODO: temporary work-around. Right now, the bad scan indexes aren't adjusted to fit the new data dimensions
		//so they cause good data to be discarded, or an index out of bounds exception to be thrown when the index
		//exceeds the dimensions of the new dataset
		plotPanel.getController().data().getDiscards().clear();		

		
	}


}