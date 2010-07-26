package peakaboo.ui.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eventful.EventfulListener;
import eventful.EventfulTypeListener;

import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.AllMapsModel;
import peakaboo.controller.mapper.SingleMapModel;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.swing.mapping.MapViewer;
import peakaboo.ui.swing.mapping.TabIconButton;
import peakaboo.ui.swing.widgets.pictures.SavePicture;
import scitypes.Coord;
import swidget.containers.SwidgetContainer;
import swidget.containers.SwidgetDialog;
import swidget.containers.SwidgetFrame;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakabooMapperSwing extends SwidgetDialog
{

	protected MapController		controller;
	private boolean				showControls;

	private JTabbedPane			tabs;
	
	private JCheckBoxMenuItem	monochrome;
	private JMenuItem			title, spectrum, coords, dstitle;
	
	private MapResultSet		originalData;
	
	public String				savePictureFolder;
	public String				dataSourceFolder;
	
	EventfulTypeListener<String> controllerListener;

	
	
	public PeakabooMapperSwing(
			SwidgetFrame owner, 
			AllMapsModel data, 
			String datasetName,
			boolean showControls, 
			String dataSourceFolder,
			String savePictureFolder,
			Coord<Integer> dataDimensions,
			MapResultSet originalData
	)
	{
		
		super(owner, "Elemental Map - " + datasetName, true);
		setup(data, datasetName, showControls, dataSourceFolder, savePictureFolder, dataDimensions, originalData);
		
	}
	
	public PeakabooMapperSwing(
			SwidgetContainer owner, 
			AllMapsModel data, 
			String datasetName,
			boolean showControls, 
			String dataSourceFolder,
			String savePictureFolder,
			Coord<Integer> dataDimensions,
			MapResultSet originalData
	)
	{

		super(owner, "Elemental Map - " + datasetName);
		setup(data, datasetName, showControls, dataSourceFolder, savePictureFolder, dataDimensions, originalData);


	}
	
	private void setup(			
			AllMapsModel data, 
			String datasetName,
			boolean showControls, 
			String dataSourceFolder,
			String savePictureFolder,
			Coord<Integer> dataDimensions,
			MapResultSet originalData)
	{
		this.dataSourceFolder = dataSourceFolder;
		this.savePictureFolder = savePictureFolder;
		this.showControls = showControls;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D toy = bi.createGraphics();
		controller = new MapController(toy, data);
		controller.setMapData(data, datasetName, dataDimensions);

		this.originalData = originalData;
		
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
					createMapsViewer();
				}
				
				MapViewer viewer = ((MapViewer)tabs.getSelectedComponent());
				
				if (viewer != null) {
					controller.setActiveTabModel( viewer.getMapViewModel() );
					controller.invalidateInterpolation();
					viewer.fullRedraw();
					
				}
				
			}
		});
		
		

		this.pack();
		
		controllerListener = new EventfulTypeListener<String>() {

			public void change(String s)
			{				
				monochrome.setSelected(controller.getMonochrome());
				spectrum.setSelected(controller.getShowSpectrum());
				coords.setSelected(controller.getShowCoords());
				
				//update the working tab's title
				if (tabs.getSelectedComponent() != null && tabs.getSelectedComponent() instanceof MapViewer)
				{
					MapViewer viewer = (MapViewer)tabs.getSelectedComponent();
				
					tabs.setTitleAt(tabs.getSelectedIndex(), viewer.getMapViewModel().mapLongTitle());
				}
				
			}
		};
		
		controller.addListener(controllerListener);
			
		controller.updateListeners("");

	}

	private void createMapsViewer()
	{
		
		SingleMapModel viewModel = new SingleMapModel(originalData.clone());
		controller.setActiveTabModel(viewModel);
		final MapViewer viewer = new MapViewer(viewModel, controller, this);
				
		
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
			tabs.setTitleAt(tabs.getTabCount()-1, viewer.getMapViewModel().mapLongTitle());
		} else {
			
			//detatch the listener, or else old tabpane listeners which only 
			//check the bounds of the click against where it painted *last time*
			//will start closing the active tab on you
			((TabIconButton)(tabs.getIconAt(tabs.getTabCount()-1))).detatchListener();
			
			tabs.setIconAt(tabs.getTabCount()-1, closeButton);
			tabs.setTitleAt(tabs.getTabCount()-1, viewer.getMapViewModel().mapLongTitle());
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


	
	public void actionSavePicture()
	{

		if (savePictureFolder == null) savePictureFolder = dataSourceFolder;
		savePictureFolder = new SavePicture(this, controller, savePictureFolder).getStartingFolder();

	}
	public void actionSaveCSV()
	{

		ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();
		controller.mapAsCSV(baos);
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
