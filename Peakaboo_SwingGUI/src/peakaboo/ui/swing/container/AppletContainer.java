package peakaboo.ui.swing.container;

import java.awt.Container;
import java.awt.Window;

import javax.swing.JApplet;
import javax.swing.JMenuBar;

public class AppletContainer implements PeakabooContainer
{

	JApplet applet;
	
	public AppletContainer(JApplet applet)
	{
		this.applet = applet;
	}
	
	
	public Container getContainer()
	{
		return applet;
	}


	@Override
	public void setTitle(String title)
	{
		//nothing to do
	}


	@Override
	public void setJMenuBar(JMenuBar menubar)
	{
		applet.setJMenuBar(menubar);
	}


	@Override
	public Window getWindow()
	{
		return null;
	}
	
	
	
}
