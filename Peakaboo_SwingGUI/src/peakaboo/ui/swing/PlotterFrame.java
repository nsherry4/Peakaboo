package peakaboo.ui.swing;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Window;

import javax.swing.JFrame;

import peakaboo.common.Version;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.containers.SwidgetFrame;
import swidget.icons.IconFactory;



/**
 * This class is the main window for Peakaboo, the plotting window
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotterFrame extends SwidgetFrame
{

	public PlotterFrame()
	{
		
		setIconImage(IconFactory.getImage(Version.icon));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 470));
		
		setTitle(Version.title);
		
		getContentPane().add(new PlotPanel(this));
		
		// Display the window.
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	@Override
	public Component getComponent()
	{
		return this;
	}

	@Override
	public Window getWindow()
	{
		return this;
	}

	@Override
	public Panel getPanel()
	{
		return null;
	}

	@Override
	public boolean isApplet()
	{
		return false;
	}

	@Override
	public void close()
	{
		System.exit(0);
	}	

}