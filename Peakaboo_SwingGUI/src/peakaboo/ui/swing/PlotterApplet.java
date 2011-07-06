package peakaboo.ui.swing;

import java.awt.Color;

import javax.swing.JApplet;
import javax.swing.JLabel;

import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.Swidget;
import swidget.icons.IconFactory;


public class PlotterApplet extends JApplet
{

	PlotPanel plotter;
	
	
	
	public PlotterApplet()
	{
		
		Swidget.initialize();
		
		PeakTableReader.readPeakTable();
		
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";
		
		
		
		JLabel logo = new  JLabel(IconFactory.getImageIcon("live"));
		logo.setOpaque(true);
		logo.setBackground(new Color(43, 56, 29));
		this.setPreferredSize(logo.getPreferredSize());
		this.getContentPane().add(logo);
				
	}
	
	
	
	//called from applet magic on start-up
	@Override
	public void init()
	{
		
		/*
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
		*/
		
	}
	
}
