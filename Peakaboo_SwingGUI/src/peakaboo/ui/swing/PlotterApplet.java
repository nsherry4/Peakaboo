package peakaboo.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import peakaboo.fileio.AbstractFile;
import peakaboo.ui.swing.plotting.PeakabooContainer;
import peakaboo.ui.swing.plotting.PlotPanel;


public class PlotterApplet extends JApplet
{

	PlotPanel plotter;
	
	PeakabooContainer container;
	
	
	public PlotterApplet()
	{
		
		container = new PeakabooContainer() {
			
			public void validate()
			{
				PlotterApplet.this.validate();
			}
			
		
			public void setTitle(String s)
			{
				//Do nothing
			}
			
		
			public void setJMenuBar(JMenuBar jmb)
			{
				PlotterApplet.this.setJMenuBar(jmb);
			}
			
		
			public void repaint()
			{
				PlotterApplet.this.repaint();
			}
			
		
			public boolean isApplet()
			{
				return true;
			}
			
		
			public Window getWindow()
			{
				return null;
			}
			
		
			public Panel getPanel()
			{
				return PlotterApplet.this;
			}
			
		
			public Component getComponent()
			{
				return PlotterApplet.this;
			}


			public void close()
			{
				System.exit(0);
			}
		};
		
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
		
		
	
		plotter = new PlotPanel(container);
		getContentPane().add(plotter);

	
		setPreferredSize(new Dimension(1000, 470));

		
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
	
}
