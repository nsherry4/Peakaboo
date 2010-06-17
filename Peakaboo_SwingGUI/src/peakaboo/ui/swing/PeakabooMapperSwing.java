package peakaboo.ui.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.AllMapsModel;
import peakaboo.controller.mapper.SingleMapModel;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.mapping.MapResultSet;
import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.mapping.MapTabControls;
import peakaboo.ui.swing.mapping.MapViewer;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.ImageButton.Layout;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakabooMapperSwing extends JDialog
{

	protected MapController		controller;
	private boolean				showControls;

	private JTabbedPane			tabs;
	
	private JCheckBoxMenuItem	monochrome;
	private JMenuItem			title, spectrum, coords, dstitle;
	
	private MapResultSet		originalData;
	
	public String				savePictureFolder;
	public String				dataSourceFolder;
	
	PeakabooSimpleListener 		controllerListener;

	public PeakabooMapperSwing(
			JFrame owner, 
			AllMapsModel data, 
			String datasetName, 
			boolean showControls, 
			String dataSourceFolder, 
			Coord<Integer> dataDimensions,
			MapResultSet originalData
	)
	{

		super(owner, "Elemental Map - " + datasetName, true);

		this.dataSourceFolder = dataSourceFolder;
		this.showControls = showControls;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D toy = bi.createGraphics();
		controller = new MapController(toy, data);
		controller.setMapData(data, datasetName, dataDimensions);

		this.originalData = originalData;
		
		init();

	}


	public PeakabooMapperSwing(JFrame owner, MapController defaultController, boolean showControls)
	{
		super(owner, true);

		controller = defaultController;
		this.showControls = showControls;

		init();
	}


	public MapController showDialog()
	{
		setVisible(true);
		
		controller.removeListener(controllerListener);
		originalData = null;
		
		return controller;
	}


	private void init()
	{
		setPreferredSize(new Dimension(900, 700));


		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		

		tabs = new JTabbedPane();	
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		pane.add(tabs, BorderLayout.CENTER);
		createMapsViewer();

		if (showControls) {
			//pane.add(createControls(), BorderLayout.LINE_START);
			createMenuBar();
		}
		
		
		tabs.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				
				//is the new-tab tab the only tab?
				if (tabs.getTabCount() == 1)
				{
					createMapsViewer();
					tabs.setSelectedIndex(0);
				}
				
				//does the new-tab tab the focused tab?
				if (tabs.getSelectedIndex() == tabs.getTabCount() -1)
				{
					tabs.setSelectedIndex(tabs.getTabCount() - 2);
				}
				
				MapViewer viewer = ((MapViewer)tabs.getSelectedComponent());
				
				if (viewer != null) {
					controller.setActiveTabModel( viewer.getMapViewModel() );
					controller.invalidateInterpolation();
					viewer.fullRedraw();
					//elementsListPanel.repaint();
				}
				
			}
		});
		
		

		this.pack();
		
		controllerListener = new PeakabooSimpleListener() {

			public void change()
			{				
				monochrome.setSelected(controller.getMonochrome());
				spectrum.setSelected(controller.getShowSpectrum());
				coords.setSelected(controller.getShowCoords());
				
			}
		};
		
		controller.addListener(controllerListener);
			
		controller.updateListeners();

	}

	private void createMapsViewer()
	{
		
		SingleMapModel viewModel = new SingleMapModel(originalData.clone());
		controller.setActiveTabModel(viewModel);
		MapViewer viewer = new MapViewer(viewModel, controller, this);
		
		MapTabControls controls = new MapTabControls(tabs, viewer);
		if (tabs.getTabCount() == 0)
		{
			tabs.addTab("", viewer);
			tabs.setTabComponentAt(tabs.getTabCount()-1, controls);
		} else {
			tabs.setTabComponentAt(tabs.getTabCount()-1, controls);
			tabs.setComponentAt(tabs.getTabCount()-1, viewer);
		}
				
		create_NewTab_Tab();
		
		tabs.setSelectedComponent(viewer);
					
	}
	
	private void create_NewTab_Tab(){
		
		tabs.addTab("New Map", new ClearPanel());
		
		ImageButton newtab = new ImageButton("map-new", "", "Create a new map", Layout.IMAGE, false, IconSize.BUTTON, Spacing.iNone(), Spacing.bSmall() );
		
		newtab.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				createMapsViewer();
			}
		});
		
		tabs.setTabComponentAt(tabs.getTabCount()-1, newtab);
		
		
	}
		
	private void createMenuBar()
	{


		// Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu;


		// Create the menu bar.
		menuBar = new JMenuBar();
		
		
		// VIEW Menu
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("Change the way the map is viewed");
		menuBar.add(menu);


		title = new JCheckBoxMenuItem("Show Elements List");
		dstitle = new JCheckBoxMenuItem("Show Dataset Title");
		spectrum = new JCheckBoxMenuItem("Show Spectrum");
		coords = new JCheckBoxMenuItem("Show Coordinates");

		title.setSelected(controller.getShowTitle());
		spectrum.setSelected(controller.getShowSpectrum());
		coords.setSelected(controller.getShowCoords());
		dstitle.setSelected(controller.getShowDatasetTitle());

		menu.add(title);
		menu.add(dstitle);
		menu.add(spectrum);
		menu.add(coords);

		spectrum.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowSpectrum(spectrum.isSelected());
			}
		});

		coords.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowCoords(coords.isSelected());
			}
		});

		title.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowTitle(title.isSelected());
			}
		});
		
		dstitle.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowDatasetTitle(dstitle.isSelected());
			}
		});


		menu.addSeparator();

		monochrome = new JCheckBoxMenuItem("Monochrome");
		monochrome.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setMonochrome(monochrome.isSelected());
			}
		});
		menu.add(monochrome);

		
		
		
		
		
		setJMenuBar(menuBar);

	}


	
}
