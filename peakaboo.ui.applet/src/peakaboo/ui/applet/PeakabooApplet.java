package peakaboo.ui.applet;




import javax.swing.JApplet;

import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.ui.swing.container.AppletContainer;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.Swidget;
import swidget.icons.IconFactory;


public class PeakabooApplet extends JApplet
{	
	
	public PeakabooApplet()
	{
		
		super();
		
		Swidget.initialize();
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";

		PeakTableReader.readPeakTable();
		
		PlotPanel plotPanel = new PlotPanel(new AppletContainer(this));
		add(plotPanel);
		
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
