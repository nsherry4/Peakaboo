



import peakaboo.controller.LiveController;
import peakaboo.dataset.provider.DataSetProvider;
import peakaboo.fileio.implementations.LiveDataSource;
import peakaboo.ui.swing.PlotterApplet;
import peakaboo.ui.swing.PlotterFrame;


public class PeakabooApplet extends PlotterApplet
{
	
	LiveController controller;
	
	LiveDataSource ds;
	DataSetProvider dsp;
	
	public PeakabooApplet()
	{
		
		super();
		

		
		PlotterFrame plotter = new PlotterFrame();
		
		controller = new LiveController(plotter.plotPanel.getController());
		
	}
	
	
	
	//called from applet magic on start-up
	@Override
	public void init()
	{
		//addScan(10, "1 2 3 4");
		//addScan(11, "1 2 7 4");
		//addScan(14, "1 9 3 4");
	}
	
	
	
	
	
	public void addScan(int index, String scan)
	{
		
		controller.addScan(index, scan);
		
		return;
		
	}
	
	public static void main(String args[])
	{
		
	}
	
}
