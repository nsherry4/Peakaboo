package peakaboo.ui.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.MapModel;
import peakaboo.controller.mapper.MapScaleMode;
import peakaboo.controller.mapper.MapTabModel;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.mapping.MapResult;
import peakaboo.mapping.MapResultSet;
import peakaboo.ui.swing.icons.IconSize;
import peakaboo.ui.swing.mapping.MapTabControls;
import peakaboo.ui.swing.mapping.MapViewer;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.ImageButton.Layout;
import peakaboo.ui.swing.widgets.pictures.SavePicture;


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
	
	private String				dataSourceFolder;
	
	private JCheckBoxMenuItem	monochrome;
	private JMenuItem			title, spectrum, coords, dstitle;

	private JSpinner			shadesSpinner;
	private JCheckBox			contours;
	private JSpinner			width;
	private JSpinner			height;
	private JSpinner			interpolation;
	private JRadioButton 		visibleElements;
	private JRadioButton 		allElements;
	
	private JPanel				elementsListPanel;
	
	private String				savePictureFolder;
	
	private MapResultSet		originalData;
	
	private final static boolean SHOW_UI_FRAME_BORDERS = true; 

	public PeakabooMapperSwing(
			JFrame owner, 
			MapModel data, 
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
		controller = new MapController(toy);
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


	private void actionSavePicture()
	{

		if (savePictureFolder == null) savePictureFolder = dataSourceFolder;
		savePictureFolder = new SavePicture(this, controller, savePictureFolder).getStartingFolder();

	}



	public MapController showDialog()
	{
		setVisible(true);
		return controller;
	}


	private void init()
	{
		setPreferredSize(new Dimension(700, 500));


		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		

		tabs = new JTabbedPane();	
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		pane.add(tabs, BorderLayout.CENTER);
		createMapsViewer();

		if (showControls) {
			pane.add(createControls(), BorderLayout.LINE_START);
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
					elementsListPanel.repaint();
				}
				
			}
		});
		
		

		this.pack();
		
		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{

				width.setEnabled(! controller.dimensionsProvided());
				height.setEnabled(! controller.dimensionsProvided());
				
				shadesSpinner.setValue(controller.getSpectrumSteps());
				shadesSpinner.setEnabled(controller.getContours());
				contours.setSelected(controller.getContours());
				width.setValue(controller.getDataWidth());
				height.setValue(controller.getDataHeight());
				interpolation.setValue(controller.getInterpolation());
				
				allElements.setSelected(controller.getMapScaleMode() == MapScaleMode.ALL_ELEMENTS);
				visibleElements.setSelected(controller.getMapScaleMode() == MapScaleMode.VISIBLE_ELEMENTS);
				
				monochrome.setSelected(controller.getMonochrome());
				spectrum.setSelected(controller.getShowSpectrum());
				coords.setSelected(controller.getShowCoords());
				

			}
		});
			
		controller.updateListeners();

	}

	private void createMapsViewer()
	{
		
		MapTabModel viewModel = new MapTabModel(originalData.clone());
		controller.setActiveTabModel(viewModel);
		MapViewer viewer = new MapViewer(viewModel, controller);
		
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

	
	
	
	
	
	

	private JPanel createControls()
	{

		JPanel panel = new JPanel();
		panel.setBorder(Spacing.bSmall());
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = Spacing.iTiny();
		maingbc.ipadx = 0;
		maingbc.ipady = 0;
		
		//map settings
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		maingbc.weightx = 0.0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(createMapOptions(), maingbc);
		

		//map scale mode selector	
		maingbc.gridy += 1;
		maingbc.weightx = 0.0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(createScaleOptions(), maingbc);

		
		//elements list
		maingbc.gridy += 1;
		maingbc.weightx = 0.0;
		maingbc.weighty = 1.0;
		maingbc.fill = GridBagConstraints.BOTH;
		elementsListPanel = createElementsList();
		panel.add(elementsListPanel, maingbc);
		

		ImageButton savePictures = new ImageButton("picture", "Save as Images","Save all open maps as images", true );
		savePictures.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				actionSavePicture();				
			}
		});
		maingbc.gridy += 1;
		maingbc.weightx = 0.0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(savePictures, maingbc);
		
		return panel;
	}

	
	private JPanel createMapOptions()
	{
		
		JPanel mapProperties = new JPanel();
		if (SHOW_UI_FRAME_BORDERS) mapProperties.setBorder(new TitledBorder("Map Settings"));
		
		mapProperties.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = Spacing.iTiny();
		c.ipadx = 0;
		c.ipady = 0;


		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridy = 0;
		width = new JSpinner();
		width.setValue(controller.getDataWidth());
		width.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});

		c.gridx = 0;
		mapProperties.add(new JLabel("Width:"), c);
		c.gridx = 1;
		mapProperties.add(width, c);



		c.gridy += 1;
		height = new JSpinner();
		height.setValue(controller.getDataHeight());
		height.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});

		c.gridx = 0;
		mapProperties.add(new JLabel("Height:"), c);
		c.gridx = 1;
		mapProperties.add(height, c);



		c.gridy += 1;
		interpolation = new JSpinner();
		interpolation.setValue(controller.getInterpolation());
		interpolation.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setInterpolation((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		c.gridx = 0;
		mapProperties.add(new JLabel("Interpolation Passes:"), c);
		c.gridx = 1;
		mapProperties.add(interpolation, c);



		c.gridy += 1;
		contours = new JCheckBox("Contours");
		contours.setSelected(controller.getContours());
		contours.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setContours(((JCheckBox) e.getSource()).isSelected());
			}
		});


		shadesSpinner = new JSpinner();
		shadesSpinner.setValue(controller.getSpectrumSteps());
		shadesSpinner.setEnabled(controller.getContours());
		shadesSpinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setSpectrumSteps((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		c.gridx = 0;
		mapProperties.add(contours, c);
		c.gridx = 1;
		mapProperties.add(shadesSpinner, c);
		
		return mapProperties;
		
	}
	
	
	private JPanel createElementsList()
	{
		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BorderLayout());
		if (SHOW_UI_FRAME_BORDERS) elementsPanel.setBorder(new TitledBorder("Mapped Elements"));
		
		//elements list
		elementsPanel.add(createTransitionSeriesList(), BorderLayout.CENTER);
		
		return elementsPanel;
	}
	
	
	private JPanel createScaleOptions()
	{
		
		JPanel modeFrame = new JPanel();
		modeFrame.setBorder(new TitledBorder("Scale Intensities by:"));
		modeFrame.setLayout(new BoxLayout(modeFrame, BoxLayout.Y_AXIS));
		
		visibleElements = new JRadioButton("Visible Elements");
		allElements = new JRadioButton("All Elements");
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(visibleElements);
		scaleGroup.add(allElements);
		visibleElements.setSelected(true);
		
		visibleElements.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.VISIBLE_ELEMENTS);
			}
		});
		allElements.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.ALL_ELEMENTS);
			}
		});
		
		
		modeFrame.add(visibleElements);
		modeFrame.add(allElements);
		
		return modeFrame;
		
	}
	

	private JScrollPane createTransitionSeriesList()
	{
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) {
					Boolean bvalue = (Boolean) value;
					controller.getActiveTabModel().mapResults.getMap(rowIndex).visible = bvalue;
					controller.invalidateInterpolation();
				}
			}


			public void removeTableModelListener(TableModelListener l)
			{
				// TODO Auto-generated method stub

			}


			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) return true;
				return false;
			}


			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) {

					return controller.getActiveTabModel().mapResults.getMap(rowIndex).visible;

				} else {

					return controller.getActiveTabModel().mapResults.getMap(rowIndex);
				}

			}


			public int getRowCount()
			{
				return controller.getMapData().mapCount();
			}


			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) return "Map";
				return "Element";
			}


			public int getColumnCount()
			{
				return 2;
			}


			public Class<?> getColumnClass(int columnIndex)
			{
				if (columnIndex == 0) return Boolean.class;
				return MapResult.class;
			}


			public void addTableModelListener(TableModelListener l)
			{
				// TODO Auto-generated method stub

			}
		};

		JTable table = new JTable(m);

		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		JScrollPane scroll = new JScrollPane(table);

		scroll.setPreferredSize(new Dimension(200, scroll.getPreferredSize().height));

		return scroll;

	}

	
}
