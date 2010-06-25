package peakaboo.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.UIManager;

import peakaboo.fileio.AbstractFile;
import peakaboo.ui.swing.plotting.PeakabooContainer;
import peakaboo.ui.swing.plotting.PlotPanel;


public class PlotterApplet extends JApplet implements PeakabooContainer
{

	PlotPanel plotter;
	
	public PlotterApplet()
	{
		//if this version of the JVM is new enough to support the Nimbus Look and Feel, use it
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			//Do Nothing -- Not an error, just not supported 
		}
		
		
	
		plotter = new PlotPanel(this);
		getContentPane().add(plotter);

		setPreferredSize(new Dimension(1000, 470));
		setVisible(true);
		
	}
	
	
	
	//called from applet magic on start-up
	public void init()
	{
		
		
		String datafile = getParameter("datafile");
		if (datafile != null)
		{
			URL dataStream;
			try
			{
				dataStream = new URL(datafile);
				AbstractFile af = new AbstractFile(dataStream);
				List<AbstractFile> files = new LinkedList<AbstractFile>();
				files.add(af);
				plotter.loadFiles(files);
				
			}
			catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
	}
	
	
	public Component getComponent()
	{
		return this;
	}

	public Panel getPanel()
	{
		return this;
	}

	public Window getWindow()
	{
		return null;
	}

	public boolean isApplet()
	{
		return true;
	}

	public void setTitle(String s)
	{
		//do nothing - no window decorator for applets
	}
	
}
