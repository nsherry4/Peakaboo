package peakaboo.ui.swing.mapping;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.maptab.MapTabController;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterManager;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class MapperFrame extends JFrame
{

	private JTabbedPane			tabs;
	public File					savePictureFolder;
	public File					dataSourceFolder;
	protected MappingController controller;
	protected IPlotController	plotController;
		
	EventfulTypeListener<String> controllerListener;
	
	private TabbedPlotterManager 	parentPlotter;

	
	
	public MapperFrame(TabbedPlotterManager plotter, MappingController controller, IPlotController plotcontroller)
	{
		super("Map - " + controller.mapsController.getDatasetTitle());
		this.controller = controller;
		this.plotController = plotcontroller;
		this.parentPlotter = plotter;
		
		init();

		setLocationRelativeTo(plotter.getWindow());
		
	}
	
	
	
	public TabbedPlotterManager getParentPlotter() {
		return parentPlotter;
	}



	public MappingController showDialog()
	{
		

		
		setVisible(true);
		
		return controller;
	}


	private void init()
	{
		setPreferredSize(new Dimension(900, 700));


		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		

		tabs = new JTabbedPane();	
		//tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		

		pane.add(tabs, BorderLayout.CENTER);
		createMapperPanel();


		
		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e){}
			
		
			public void windowIconified(WindowEvent e){}
			
		
			public void windowDeiconified(WindowEvent e){}
			
		
			public void windowDeactivated(WindowEvent e){}
			
		
			public void windowClosing(WindowEvent e)
			{
				controller.removeListener(controllerListener);
			}
			
		
			public void windowClosed(WindowEvent e){}
			
		
			public void windowActivated(WindowEvent e){}
		});
		
		tabs.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				
				//is the new-tab tab the only tab?
				if (tabs.getTabCount() == 1)
				{
					createMapperPanel();
					tabs.setSelectedIndex(0);
				}
				
				//does the new-tab tab the focused tab?
				if (tabs.getSelectedIndex() == tabs.getTabCount() -1)
				{
					createMapperPanel();
				}
				
				MapperPanel viewer = ((MapperPanel)tabs.getSelectedComponent());
				
				if (viewer != null) {
					controller.setTabController( viewer.getTabController() );
					controller.getActiveTabController().invalidateInterpolation();
					viewer.fullRedraw();
					
				}
								
			}
		});
		
		

		this.pack();
		
		controllerListener = s -> {				

			
			//update the working tab's title
			if (tabs.getSelectedComponent() != null && tabs.getSelectedComponent() instanceof MapperPanel)
			{			
				tabs.setTitleAt(tabs.getSelectedIndex(), controller.getActiveTabController().mapLongTitle());
			}
			
		};
		
		controller.addListener(controllerListener);
			
		controller.updateListeners("");

	}

	private void createMapperPanel()
	{
		
		MapTabController tabController = new MapTabController(controller, controller.mapsController.getMapResultSet().getAllTransitionSeries());
		
		controller.setTabController(tabController);
		final MapperPanel viewer = new MapperPanel(tabController, controller, this);
				
		
		final TabIconButton closeButton = new TabIconButton(StockIcon.WINDOW_CLOSE);
		closeButton.addListener(new EventfulListener() {
			
			public void change()
			{	
				int index = tabs.getSelectedIndex();
				
				//detatch the listener, or else old tabpane listeners which only 
				//check the bounds of the click against where it painted *last time*
				//will start closing the active tab on you
				closeButton.detatchListener();
				
				if (index > 0) tabs.setSelectedIndex(index-1);
				if (index == 0) tabs.setSelectedIndex(1);
				tabs.remove(index);
			}
		});
		
		
		
		if (tabs.getTabCount() == 0)
		{
			tabs.addTab("", closeButton, viewer);
			//tabs.setTabComponentAt(tabs.getTabCount()-1, controls);
			tabs.setTitleAt(tabs.getTabCount()-1, viewer.getTabController().mapLongTitle());
		} else {
			
			//detatch the listener, or else old tabpane listeners which only 
			//check the bounds of the click against where it painted *last time*
			//will start closing the active tab on you
			((TabIconButton)(tabs.getIconAt(tabs.getTabCount()-1))).detatchListener();
			
			tabs.setIconAt(tabs.getTabCount()-1, closeButton);
			tabs.setTitleAt(tabs.getTabCount()-1, viewer.getTabController().mapLongTitle());
			tabs.setComponentAt(tabs.getTabCount()-1, viewer);
		}
		
		
				
		create_NewTab_Tab();
		
		tabs.setSelectedComponent(viewer);
					
	}
	
	private void create_NewTab_Tab(){
		
		TabIconButton newmapButton = new TabIconButton("map-new");
		
		tabs.addTab("", newmapButton, new ClearPanel());		
	}
	
	
	public void actionSavePicture()
	{
		MapperPanel viewer = ((MapperPanel)tabs.getSelectedComponent());
		
		if (savePictureFolder == null) savePictureFolder = dataSourceFolder;
		savePictureFolder = viewer.savePicture(savePictureFolder);
				
	}
	public void actionSaveCSV()
	{

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		controller.getActiveTabController().mapAsCSV(baos);
		try
		{
			savePictureFolder = SwidgetIO.saveFile(this, "Save Map(s) as Text", "txt", "Text File", savePictureFolder, baos);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	
}
