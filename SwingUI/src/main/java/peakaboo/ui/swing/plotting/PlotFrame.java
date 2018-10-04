package peakaboo.ui.swing.plotting;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import peakaboo.common.Version;
import swidget.icons.IconFactory;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedLayerPanel;


public class PlotFrame extends JFrame
{

	
	private TabbedInterface<TabbedLayerPanel> tabControl;
	private static int openWindows = 0;
	
	public PlotFrame() {
	
		openWindows++;
		//containers = new HashMap<PlotPanel, TabbedContainer>();
		
		tabControl = new TabbedInterface<TabbedLayerPanel>(this, p -> "No Data", 150) {

			@Override
			protected PlotPanel createComponent() {
				PlotPanel plot =  new PlotPanel(this);
				plot.setProgramTitle("");
				return plot;
			}

			@Override
			protected void destroyComponent(TabbedLayerPanel component){}

			@Override
			protected void titleChanged(String title) {}
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
	
	


	public TabbedInterface<TabbedLayerPanel> getTabControl() {
		return tabControl;
	}

	
	

}
