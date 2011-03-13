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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import fava.Fn;
import fava.Functions;
import fava.datatypes.Pair;
import fava.datatypes.Range;
import fava.lists.FList;
import fava.signatures.FnMap;

import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.maptab.MapTabController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.mapping.correction.Corrections;
import peakaboo.mapping.correction.CorrectionsManager;
import peakaboo.mapping.results.MapResult;
import peakaboo.ui.swing.mapping.MapViewer;
import peakaboo.ui.swing.mapping.TabIconButton;
import scitypes.GridPerspective;
import scitypes.SigDigits;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.PropertyPanel;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakabooMapperSwing extends JDialog
{

	protected MappingController		controller;
	protected PlotController	plotController;
	

	private JTabbedPane			tabs;
	
	private JCheckBoxMenuItem	monochrome;
	private JMenuItem			title, spectrum, coords, dstitle;
	private ToolbarImageButton	readIntensities, examineSubset;
	
	public String				savePictureFolder;
	public String				dataSourceFolder;
	
	EventfulTypeListener<String> controllerListener;

	
	
	public PeakabooMapperSwing(JFrame owner, MappingController controller, PlotController plotcontroller)
	{
		super(owner, "Map - " + controller.mapsController.getDatasetTitle());
		this.controller = controller;
		this.plotController = plotcontroller;
		
		init();

		setLocationRelativeTo(owner);
		
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
		
		pane.add(createToolbar(), BorderLayout.NORTH);
		pane.add(tabs, BorderLayout.CENTER);
		createMapsViewer();


		createMenuBar();
		
		
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
		
		MapTabController tabController = new MapTabController(controller, controller.mapsController.getMapResultSet().getAllTransitionSeries());
		
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
		
		
		readIntensities =  new ToolbarImageButton(StockIcon.BADGE_INFO, "Get Intensities", "Get fitting intensities for the selected region", true);
		readIntensities.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				Map<String, String> fittings = new HashMap<String, String>();
				
				final Corrections corr = CorrectionsManager.getCorrections("WL");
				
				final int xstart = controller.getActiveTabController().getDragStart().x;
				final int ystart = controller.getActiveTabController().getDragStart().y;
				
				final int xend = controller.getActiveTabController().getDragEnd().x;
				final int yend = controller.getActiveTabController().getDragEnd().y;
				
				final int size = (Math.abs(xstart - xend) + 1) * (Math.abs(ystart - yend) + 1);
				
				final GridPerspective<Float> grid = new GridPerspective<Float>(controller.mapsController.getDataWidth(), controller.mapsController.getDataHeight(), 0f);
				
				
				//generate a list of pairings of TransitionSeries and their intensity values
				FList<Pair<TransitionSeries, Float>> averages = Fn.map(controller.mapsController.getMapResultSet(), new FnMap<MapResult, Pair<TransitionSeries, Float>>() {

					public Pair<TransitionSeries, Float> f(MapResult r)
					{
						float sum = 0;
						for (int x : new Range(xstart, xend)) {
							for (int y : new Range(ystart, yend)){
								sum += r.data.get(grid.getIndexFromXY(x, y));
							}
						}
						return new Pair<TransitionSeries, Float>(r.transitionSeries, sum / size);
					}});
				
				
				//get the total of all of the corrected values
				float total = averages.map(new FnMap<Pair<TransitionSeries,Float>, Float>() {

					public Float f(Pair<TransitionSeries, Float> p)
					{
						Float corrFactor = corr.getCorrection(p.first);
						return (corrFactor == null) ? 0f : p.second * corrFactor;
					}}).fold(Functions.addf());
				
				for (Pair<TransitionSeries, Float> p : averages)
				{
					float average = p.second;
					Float corrFactor = corr.getCorrection(p.first);
					String corrected = "(-)";
					if (corrFactor != null) corrected = "(~" + SigDigits.toIntSigDigit((average*corrFactor/total*100), 1) + "%)";
					
					fittings.put(p.first.getDescription(), SigDigits.roundFloatTo(average, 2) + " " + corrected);
				}
				
				PropertyPanel correctionsPanel = new PropertyPanel(fittings);
				final JDialog correctionsDialog = new JDialog(PeakabooMapperSwing.this, corr.getName(), true);
				
				Container c0 = correctionsDialog.getContentPane();
				JPanel c = new JPanel(new BorderLayout());
				c0.add(c);
				JPanel contentPanel = new JPanel(new BorderLayout());
				c.add(contentPanel, BorderLayout.CENTER);
				
				contentPanel.add(new JLabel("Concentrations accurate to a factor of 5", JLabel.CENTER), BorderLayout.SOUTH);
				contentPanel.add(correctionsPanel, BorderLayout.CENTER);
				contentPanel.setBorder(Spacing.bHuge());
				
				ButtonBox bbox = new ButtonBox(Spacing.bHuge());
				ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", "Close this window");
				close.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						correctionsDialog.setVisible(false);
						correctionsDialog.dispose();
					}
				});
				bbox.addRight(close);
				c.add(bbox, BorderLayout.SOUTH);
				
				
				correctionsDialog.pack();
				correctionsDialog.setLocationRelativeTo(PeakabooMapperSwing.this);
				correctionsDialog.setVisible(true);
				
				
			}
		});
		toolbar.add(readIntensities);
		
		
		examineSubset =  new ToolbarImageButton("view-subset", "Plot Region", "Plot the selected region", true);
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
			e.printStackTrace();
		}

	}
	
	
}
