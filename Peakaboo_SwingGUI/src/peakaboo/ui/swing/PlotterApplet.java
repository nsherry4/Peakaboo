package peakaboo.ui.swing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.UIManager;

import commonenvironment.AbstractFile;

import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.Swidget;
import swidget.icons.IconFactory;


public class PlotterApplet extends JApplet
{

	PlotPanel plotter;
	
	
	
	public PlotterApplet()
	{
		
		Swidget.initialize();
		
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		
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
		
		
	
		//plotter = new PlotPanel(container);
		//getContentPane().add(plotter);

	
		//setPreferredSize(new Dimension(1000, 470));

		
	}
	
	
	
	//called from applet magic on start-up
	@Override
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
	
}
