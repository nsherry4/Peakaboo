package peakaboo.ui.swing.plotting.tabbed;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import peakaboo.common.Version;
import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.datasource.model.DataSource;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.tabbedinterface.TabbedInterface;


public class TabbedPlotterFrame extends JFrame
{

	
	TabbedInterface<PlotPanel> tabControl;
	//private Map<PlotPanel, TabbedContainer> containers;
	
	private static int openWindows = 0;
	
	public TabbedPlotterFrame()
	{
	
		openWindows++;
		//containers = new HashMap<PlotPanel, TabbedContainer>();
		
		tabControl = new TabbedInterface<PlotPanel>(p -> "No Data", 150) {

			@Override
			protected PlotPanel createComponent()
			{
				TabbedPlotterManager container = new TabbedPlotterManager(TabbedPlotterFrame.this);
				PlotPanel plot =  new PlotPanel(container);
				plot.setProgramTitle("");
				return plot;
			}

			@Override
			protected void destroyComponent(PlotPanel component){}

			@Override
			protected void titleChanged(String title)
			{
				String windowTitle = "";
				windowTitle += title + " - " + Version.title;
				setTitle(windowTitle);
			}
		};
		
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
		
		
		setPreferredSize(new Dimension(1000, 473));
		setIconImage(IconFactory.getImage(Version.icon));
		setTitle("Peakaboo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabControl.init();
		
		add(tabControl);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
	}
	
	


	public TabbedInterface<PlotPanel> getTabControl() {
		return tabControl;
	}

	
	

}
