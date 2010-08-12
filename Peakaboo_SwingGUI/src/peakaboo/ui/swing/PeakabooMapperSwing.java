package peakaboo.ui.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import fava.datatypes.Range;

import peakaboo.common.DataTypeFactory;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.maptab.TabController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.mapping.results.MapResult;
import peakaboo.ui.swing.mapping.MapViewer;
import peakaboo.ui.swing.mapping.TabIconButton;
import scitypes.GridPerspective;
import scitypes.SigDigits;
import swidget.dialogues.PropertyDialogue;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ToolbarImageButton;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakabooMapperSwing extends JDialog
{

	protected MapController		controller;
	protected PlotController	plotController;
	

	private JTabbedPane			tabs;
	
	private JCheckBoxMenuItem	monochrome;
	private JMenuItem			title, spectrum, coords, dstitle;
	private ToolbarImageButton	readIntensities, examineSubset;
	
	public String				savePictureFolder;
	public String				dataSourceFolder;
	
	EventfulTypeListener<String> controllerListener;

	
	
	public PeakabooMapperSwing(JFrame owner, MapController controller, PlotController plotcontroller)
	{
		super(owner, "Map - " + controller.mapsController.getDatasetTitle());
		this.controller = controller;
		this.plotController = plotcontroller;
		
		init();
		
	}
	
	/*
	public PeakabooMapperSwing(
			JFrame owner, 
			AllMapsModel data, 
			String datasetName,
			String dataSourceFolder,
			String savePictureFolder,
			Coord<Integer> dataDimensions,
			MapResultSet originalData,
			PlotController plotcontroller
	)
	{
		
		super(owner, "Elemental Map - " + datasetName);
		setup(data, datasetName, dataSourceFolder, savePictureFolder, dataDimensions, originalData, plotcontroller);
		
	}
	
	
	private void setup(			
			AllMapsModel data, 
			String datasetName,
			String dataSourceFolder,
			String savePictureFolder,
			Coord<Integer> dataDimensions,
			MapResultSet originalData,
			PlotController plotcontroller)
	{
		this.dataSourceFolder = dataSourceFolder;
		this.savePictureFolder = savePictureFolder;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D toy = bi.createGraphics();
		
		controller = new MapController(toy, data, plotcontroller);
		controller.setMapData(data, datasetName, dataDimensions);
		
		this.originalData = originalData;
		
		init();
	}

*/
	

	public MapController showDialog()
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
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		pane.add(createToolbar(), BorderLayout.NORTH);
		pane.add(tabs, BorderLayout.CENTER);
		createMapsViewer();


		createMenuBar();
		
		
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
				controller.removeListener(controllerListener);
			}
			
		
			public void windowClosed(WindowEvent e)
			{}
			
		
			public void windowActivated(WindowEvent e)
			{}
		});
		
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
					createMapsViewer();
				}
				
				MapViewer viewer = ((MapViewer)tabs.getSelectedComponent());
				
				if (viewer != null) {
					controller.setTabController( viewer.getTabController() );
					controller.getActiveTabController().invalidateInterpolation();
					viewer.fullRedraw();
					
				}
								
			}
		});
		
		

		this.pack();
		
		controllerListener = new EventfulTypeListener<String>() {

			public void change(String s)
			{				
				monochrome.setSelected(controller.mapsController.getMonochrome());
				spectrum.setSelected(controller.mapsController.getShowSpectrum());
				coords.setSelected(controller.mapsController.getShowCoords());
				
				//update the working tab's title
				if (tabs.getSelectedComponent() != null && tabs.getSelectedComponent() instanceof MapViewer)
				{			
					tabs.setTitleAt(tabs.getSelectedIndex(), controller.getActiveTabController().mapLongTitle());
				}
				
				if (controller.getActiveTabController().hasBoundingRegion())
				{
					readIntensities.setEnabled(true);
					examineSubset.setEnabled(true);
				} else {
					readIntensities.setEnabled(false);
					examineSubset.setEnabled(false);
				}
				
			}
		};
		
		controller.addListener(controllerListener);
			
		controller.updateListeners("");

	}

	private void createMapsViewer()
	{
		
		TabController tabController = new TabController(controller, controller.mapsController.getMapResultSet().getAllTransitionSeries());
		
		controller.setTabController(tabController);
		final MapViewer viewer = new MapViewer(tabController, controller, this);
				
		
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
		
	private void createMenuBar()
	{


		// Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu;


		// Create the menu bar.
		menuBar = new JMenuBar();
		
		
		// FILE Menu
		menu = new JMenu("Maps");
		menu.setMnemonic(KeyEvent.VK_M);
		menu.getAccessibleContext().setAccessibleDescription("Actions on these Maps");
		menuBar.add(menu);
		
		
		JMenuItem savePicture = new JMenuItem("Save Image",StockIcon.DEVICE_CAMERA.toMenuIcon());
		savePicture.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				actionSavePicture();
			}
		});
		menu.add(savePicture);
		
		
		JMenuItem saveText = new JMenuItem("Save as Text", StockIcon.DOCUMENT_EXPORT.toMenuIcon());
		saveText.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				actionSaveCSV();
			}
		});
		menu.add(saveText);
		
		
		
		// VIEW Menu
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("Change the way the map is viewed");
		menuBar.add(menu);


		title = new JCheckBoxMenuItem("Show Elements List");
		dstitle = new JCheckBoxMenuItem("Show Dataset Title");
		spectrum = new JCheckBoxMenuItem("Show Spectrum");
		coords = new JCheckBoxMenuItem("Show Coordinates");

		title.setSelected(controller.mapsController.getShowTitle());
		spectrum.setSelected(controller.mapsController.getShowSpectrum());
		coords.setSelected(controller.mapsController.getShowCoords());
		dstitle.setSelected(controller.mapsController.getShowDatasetTitle());

		menu.add(title);
		menu.add(dstitle);
		menu.add(spectrum);
		menu.add(coords);

		spectrum.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.mapsController.setShowSpectrum(spectrum.isSelected());
			}
		});

		coords.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.mapsController.setShowCoords(coords.isSelected());
			}
		});

		title.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.mapsController.setShowTitle(title.isSelected());
			}
		});
		
		dstitle.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.mapsController.setShowDatasetTitle(dstitle.isSelected());
			}
		});


		menu.addSeparator();

		monochrome = new JCheckBoxMenuItem("Monochrome");
		monochrome.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.mapsController.setMonochrome(monochrome.isSelected());
			}
		});
		menu.add(monochrome);

		
		
		
		
		
		setJMenuBar(menuBar);

	}


	private JToolBar createToolbar()
	{
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		ToolbarImageButton savePicture = new ToolbarImageButton(StockIcon.DEVICE_CAMERA, "Save Image", "Save the current map as an image");
		savePicture.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				actionSavePicture();
			}
		});
		toolbar.add(savePicture);
		
		
		ToolbarImageButton saveText = new ToolbarImageButton(StockIcon.DOCUMENT_EXPORT, "Export as Text", "Export the current map as a comma separated value file");
		saveText.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				actionSaveCSV();
			}
		});
		toolbar.add(saveText);
		
		
		toolbar.addSeparator();
		
		
		readIntensities =  new ToolbarImageButton(StockIcon.BADGE_INFO, "Get Intensities", "Get fitting intensities for the selected region");
		readIntensities.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				Map<String, String> fittings = DataTypeFactory.<String, String>map();
				
				int xstart = controller.getActiveTabController().getDragStart().x;
				int ystart = controller.getActiveTabController().getDragStart().y;
				
				int xend = controller.getActiveTabController().getDragEnd().x;
				int yend = controller.getActiveTabController().getDragEnd().y;
				
				int size = (Math.abs(xstart - xend) + 1) * (Math.abs(ystart - yend) + 1);
				
				GridPerspective<Float> grid = new GridPerspective<Float>(controller.mapsController.getDataWidth(), controller.mapsController.getDataHeight(), 0f);
				
				for (MapResult r : controller.mapsController.getMapResultSet())
				{
					float sum = 0;
					for (int x : new Range(xstart, xend)) {
						for (int y : new Range(ystart, yend)){
							sum += r.data.get(grid.getIndexFromXY(x, y));
						}
					}
					sum /= size;
					
					fittings.put(r.transitionSeries.getDescription(), SigDigits.roundFloatTo(sum, 2));
					
				}
				
				new PropertyDialogue("Fitting Intensities", PeakabooMapperSwing.this, fittings);
			}
		});
		toolbar.add(readIntensities);
		
		
		examineSubset =  new ToolbarImageButton("view-subset", "Plot Region", "Plot the selected region");
		examineSubset.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				new PlotterFrame(controller.getDataSourceForSubset(controller.getActiveTabController().getDragStart(), controller.getActiveTabController().getDragEnd()), controller.getSerializedPlotSettings());
			}
		});
		toolbar.add(examineSubset);
		
		
		readIntensities.setEnabled(false);
		examineSubset.setEnabled(false);
		
		
		return toolbar;
		
	}
	
	
	public void actionSavePicture()
	{


		MapViewer viewer = ((MapViewer)tabs.getSelectedComponent());
		
		if (savePictureFolder == null) savePictureFolder = dataSourceFolder;
		savePictureFolder = viewer.savePicture(savePictureFolder);
				
	}
	public void actionSaveCSV()
	{

		ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();
		controller.getActiveTabController().mapAsCSV(baos);
		try
		{
			savePictureFolder = SwidgetIO.saveFile(this, "Save Map(s) as Text", "txt", "Text File", savePictureFolder, baos);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
