package peakaboo.ui.swing.plotting;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ezware.dialog.task.TaskDialogs;

import commonenvironment.Apps;
import commonenvironment.Env;
import commonenvironment.IOOperations;
import eventful.EventfulEnumListener;
import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import fava.datatypes.Pair;
import peakaboo.common.Version;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.settings.ChannelCompositeMode;
import peakaboo.curvefit.model.transitionseries.EscapePeakType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.DataSourceLoader;
import peakaboo.datasource.DataSourceLookup;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.framework.PluginDataSource;
import peakaboo.filter.FilterLoader;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.swing.PeakabooMapperSwing;
import peakaboo.ui.swing.container.PeakabooContainer;
import peakaboo.ui.swing.misc.PluginData;
import peakaboo.ui.swing.plotting.datasource.DataSourceSelection;
import peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;
import scidraw.swing.SavePicture;
import scitypes.ReadOnlySpectrum;
import scitypes.SigDigits;
import swidget.dialogues.PropertyDialogue;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ComponentListPanel;
import swidget.widgets.DraggingScrollPaneListener;
import swidget.widgets.DropdownImageButton;
import swidget.widgets.DropdownImageButton.Actions;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.ZoomSlider;
import swidget.widgets.toggle.ComplexToggle;



public class PlotPanel extends ClearPanel
{

	private PeakabooContainer	container;


	//Non-UI
	IPlotController				controller;
	PlotCanvas					canvas;
	File						saveFilesFolder;
	File						savedSessionFileName;
	String                      programTitle;

	
	
	//===MAIN MENU WIDGETS===
	
	//FILE
	JMenuItem					snapshotMenuItem;
	JMenuItem					exportFittingsMenuItem;
	JMenuItem					exportFilteredDataMenuItem;
	JMenuItem					saveSession;

	//EDIT
	JMenuItem					undo, redo;


	JSpinner					scanNo;
	JLabel						scanLabel;
	JToggleButton				scanBlock;
	JLabel						channelLabel;

	//===TOOLBAR WIDGETS===
	JToolBar                    toolBar;
	
	//===PLOTTING UI WIDGETS===
	JSpinner					energy;
	ChangeListener				energyListener;
	ImageButton					toolbarSnapshot;
	DropdownImageButton			toolbarMap;
	ImageButton					toolbarInfo;
	ZoomSlider					zoomSlider;
	JPanel						bottomPanel;
	JPanel						scanSelector;
	JScrollPane					scrolledCanvas;
	

	public PlotPanel(PeakabooContainer container)
	{
		this.container = container;
		this.programTitle = " - " + Version.title;
		
		savedSessionFileName = null;

		controller = new PlotController();
				

		initGUI();

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String message)
			{
				setWidgetsState();
			}
		});

		setWidgetsState();



	}
	
	public String getProgramTitle()
	{
		return programTitle;
	}
	
	public void setProgramTitle(String title)
	{
		programTitle = title;
	}

	public void addToolbarButton(int position, Component c)
	{
		toolBar.add(c, position);
	}

	public IPlotController getController()
	{
		return controller;
	}


	private void setWidgetsState()
	{

		snapshotMenuItem.setEnabled(false);
		exportFittingsMenuItem.setEnabled(false);
		exportFilteredDataMenuItem.setEnabled(false);
		toolbarSnapshot.setEnabled(false);

		if (controller.data().hasDataSet())
		{

			bottomPanel.setEnabled(true);
			snapshotMenuItem.setEnabled(true);
			exportFittingsMenuItem.setEnabled(true);
			exportFilteredDataMenuItem.setEnabled(true);
			toolbarSnapshot.setEnabled(true);


			toolbarMap.setEnabled(controller.fitting().canMap());


			if (controller.data().getDataSet().hasMetadata())
			{
				toolbarInfo.setEnabled(true);
			}
			else
			{
				toolbarInfo.setEnabled(false);
			}

			setEnergySpinner();


			if (controller.settings().getChannelCompositeType() == ChannelCompositeMode.NONE)
			{

				scanNo.setValue(controller.settings().getScanNumber() + 1);
				scanBlock.setSelected(controller.data().getDiscards().isDiscarded(controller.settings().getScanNumber()));
				
				scanSelector.setEnabled(true);

			}
			else
			{
				scanSelector.setEnabled(false);
			}

		}
		else
		{
			bottomPanel.setEnabled(false);
		}

		undo.setEnabled(controller.history().canUndo());
		redo.setEnabled(controller.history().canRedo());
		undo.setText("Undo " + controller.history().getNextUndo());
		redo.setText("Redo " + controller.history().getNextRedo());

		zoomSlider.setValueEventless((int)(controller.settings().getZoom()*100));
		setTitleBar();

		container.getContainer().validate();
		container.getContainer().repaint();

	}


	private void setEnergySpinner()
	{
		//dont let the listeners get wind of this change		
		energy.removeChangeListener(energyListener);
		energy.setValue((double) controller.settings().getMaxEnergy());
		energy.addChangeListener(energyListener);
	}


	private void initGUI()
	{

		canvas = new PlotCanvas(controller, this);
		canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		

		channelLabel = new JLabel("");
		channelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		channelLabel.setFont(channelLabel.getFont().deriveFont(Font.PLAIN));

		canvas.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e){}


			public void mouseMoved(MouseEvent e)
			{
				mouseMoveCanvasEvent(e.getX());
			}

		});



		Container pane = this;

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		pane.setLayout(layout);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		populateToolbar(toolBar);
		pane.add(toolBar, c);

		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(1000, 100));
		
		scrolledCanvas = new JScrollPane(canvas);
		scrolledCanvas.setAutoscrolls(true);
		scrolledCanvas.setBorder(Spacing.bNone());
		
		
		scrolledCanvas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrolledCanvas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		new DraggingScrollPaneListener(scrolledCanvas.getViewport(), canvas);

		JPanel canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.add(scrolledCanvas, BorderLayout.CENTER);
		canvasPanel.add(createBottomBar(), BorderLayout.SOUTH);
		canvasPanel.setPreferredSize(new Dimension(600, 300));

		canvasPanel.addComponentListener(new ComponentListener() {
			
			public void componentShown(ComponentEvent e){}
					
			public void componentResized(ComponentEvent e)
			{
				canvas.updateCanvasSize();
			}
			
			public void componentMoved(ComponentEvent e){}
			
			public void componentHidden(ComponentEvent e){}
		});
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.add(new CurveFittingView(controller.fitting(), canvas), 0);
		tabs.add(new FiltersetViewer(controller.filtering(), container.getContainer()), 1);

		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, canvasPanel);
		split.setResizeWeight(0);
		split.setOneTouchExpandable(true);
		split.setBorder(Spacing.bNone());
		pane.add(split, c);

		
		createMenu();


	}


	private void setTitleBar()
	{
		container.setTitle(getTitleBarString());
	}


	private String getTitleBarString()
	{
		StringBuffer titleString;
		titleString = new StringBuffer();
		
		if (controller.data().hasDataSet())
		{
			titleString.append(controller.data().getDataSet().getScanData().datasetName());
			titleString.append(programTitle);
		} else {
			titleString.append("No Data");
			titleString.append(programTitle);
		}

		
		return titleString.toString();
	}


	private void populateToolbar(JToolBar toolbar)
	{

		toolbar.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;

		ImageButton ibutton = new ToolbarImageButton("document-open", "Open", "Open a new data set");
		ibutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				actionOpenData();
			}
		});
		toolbar.add(ibutton, c);

		// controls.add(button);

		toolbarSnapshot = new ToolbarImageButton(
			StockIcon.DEVICE_CAMERA,
			"Save Image",
			"Save a picture of the current plot");
		toolbarSnapshot.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				actionSavePicture();
			}
		});
		c.gridx += 1;
		toolbar.add(toolbarSnapshot, c);

		toolbarInfo = new ToolbarImageButton(
			StockIcon.BADGE_INFO,
			"Scan Info",
			"Displays extended information about this data set");
		toolbarInfo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				actionShowInfo();
			}
		});
		c.gridx += 1;
		toolbarInfo.setEnabled(false);
		toolbar.add(toolbarInfo, c);

		JPopupMenu mapMenu = new JPopupMenu();
		populateFittingMenu(mapMenu);
		
		
		toolbarMap = new DropdownImageButton("map", "Map Fittings", "Display a 2D map of the relative intensities of the fitted elements", IconSize.TOOLBAR_SMALL, ToolbarImageButton.significantLayout, mapMenu);
		toolbarMap.addListener(new EventfulEnumListener<Actions>() {
			
			public void change(Actions message)
			{
				switch (message)
				{
					case MAIN: 
						actionMap(FittingTransform.AREA);
						break;
					case MENU:
						break;
				}
			}
		});
		
		c.gridx += 1;
		toolbarMap.setEnabled(false);
		toolbar.add(toolbarMap, c);

		c.gridx += 1;
		c.weightx = 1.0;
		toolbar.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;


		JPanel energyControls = new ClearPanel();
		energyControls.setBorder(Spacing.bMedium());
		energyControls.setLayout(new GridBagLayout());
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 0;
		c2.weightx = 0;
		c2.weighty = 0;
		c2.fill = GridBagConstraints.NONE;
		c2.anchor = GridBagConstraints.EAST;

		JLabel energyLabel = new JLabel("Max Energy (keV): ");
		energyControls.add(energyLabel, c2);

		energy = new JSpinner();
		energy.setModel(new SpinnerNumberModel(20.48, 0.0, 204.8, 0.01));

		c2.gridx += 1;
		energyControls.add(energy, c2);
		energyListener = new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{

				float value = ((Double) energy.getValue()).floatValue();
				controller.settings().setMaxEnergy(value);

			}
		};
		energy.addChangeListener(energyListener);
		c.gridx += 1;
		toolbar.add(energyControls, c);

		ibutton = new ToolbarImageButton(StockIcon.MISC_ABOUT, "About");
		ibutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				actionAbout();
			}
		});
		c.gridx += 1;
		toolbar.add(ibutton, c);

	}

	
	private void populateFittingMenu(JComponent menu)
	{
		final JMenuItem mapArea = createMenuItem(
				"Map Fitting Area", null, null, 
				new ActionListener() {
			
					public void actionPerformed(ActionEvent e)
					{
						actionMap(FittingTransform.AREA);
					}
				},
				null, null
		);
		mapArea.setEnabled(false);
		menu.add(mapArea);
		
		
		final JMenuItem mapHeights = createMenuItem(
				"Map Fitting Heights", null, null, 
				new ActionListener() {
			
					public void actionPerformed(ActionEvent e)
					{
						actionMap(FittingTransform.HEIGHT);
					}
				},
				null, null
		);
		mapHeights.setEnabled(false);
		menu.add(mapHeights);
		
		
		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String message)
			{
				mapHeights.setEnabled(controller.fitting().canMap());
				mapArea.setEnabled(controller.fitting().canMap());
			}});
		
	}

	
	private JCheckBoxMenuItem createMenuCheckItem(String title, ImageIcon icon, String description, ActionListener listener, KeyStroke key, Integer mnemonic)
	{
		
		JCheckBoxMenuItem menuItem;
		if (icon != null) {
			menuItem = new JCheckBoxMenuItem(title, icon);
		} else {
			menuItem = new JCheckBoxMenuItem(title);
		}
		
		configureMenuItem(menuItem, description, listener, key, mnemonic);
		
		return menuItem;
		
	}
	
	private JMenuItem createMenuItem(String title, ImageIcon icon, String description, ActionListener listener, KeyStroke key, Integer mnemonic)
	{
		JMenuItem menuItem;
		if (icon != null) {
			menuItem = new JMenuItem(title, icon);
		} else {
			menuItem = new JMenuItem(title);
		}
		
		configureMenuItem(menuItem, description, listener, key, mnemonic);
		
		return menuItem;
		
	}
	
	private void configureMenuItem(JMenuItem menuItem, String description, ActionListener listener, KeyStroke key, Integer mnemonic)
	{
		if (key != null) menuItem.setAccelerator(key);
		if (mnemonic != null) menuItem.setMnemonic(mnemonic);
		if (description != null) menuItem.getAccessibleContext().setAccessibleDescription(description);
		if (description != null) menuItem.setToolTipText(description);
		if (listener != null) menuItem.addActionListener(listener);
	}
	
	private void createMenu()
	{

		JMenuBar menuBar;
		JMenu menu;

		menuBar = new JMenuBar();

		

		// FILE Menu
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("Read and Write Data Sets");

		
		menu.add(createMenuItem(
				"Open Data\u2026", StockIcon.DOCUMENT_OPEN.toMenuIcon(), "Opens new data sets.",
				e -> actionOpenData(),
				KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), KeyEvent.VK_O
		));
		

		
		
		menu.addSeparator();

		
		menu.add(createMenuItem(
				"Save Session", StockIcon.DOCUMENT_SAVE.toMenuIcon(), null, 
				e -> actionSaveSession(),
				null, null
		));
		
		menu.add(createMenuItem(
				"Load Session", null, null,
				e -> actionLoadSession(),
				null, null
		));
		


		menu.addSeparator();

		
		snapshotMenuItem = createMenuItem(
				"Export Plot as Image\u2026", StockIcon.DEVICE_CAMERA.toMenuIcon(), "Saves the current plot as an image",
				e -> actionSavePicture(),
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), KeyEvent.VK_P
		);
		menu.add(snapshotMenuItem);
		
		
		exportFilteredDataMenuItem = createMenuItem(
				"Export Filtered Data as Text", StockIcon.DOCUMENT_EXPORT.toMenuIcon(), "Saves the filtered data to a text file",
				e -> actionSaveFittedDataInformation(),
				null, null
		);
		menu.add(exportFilteredDataMenuItem);
		
		exportFittingsMenuItem = createMenuItem(
				"Export Fittings as Text", null, "Saves the current fitting data to a text file",
				e -> actionSaveFittingInformation(),
				null, null
		);
		menu.add(exportFittingsMenuItem);


		menu.addSeparator();

		menu.add(createMenuItem(
				"Plugin Status", null, "Shows information about loaded plugins",
				e -> {
					
					JTabbedPane tabs = new JTabbedPane();

					ComponentListPanel dsPanel = new ComponentListPanel(
						DataSourceLoader
						.getPluginSet()
						.getAll()
						.stream()
						.map(PluginData::new)
						.collect(Collectors.toList())
					);
					
					
					ComponentListPanel filterPanel = new ComponentListPanel(
						FilterLoader
						.getPluginSet()
						.getAll()
						.stream()
						.map(PluginData::new)
						.collect(Collectors.toList())
					);
					
					
					tabs.add("Data Sources", dsPanel);
					tabs.add("Filters", filterPanel);


					
					JDialog dialog = new JDialog(container.getWindow()); //TODO: owner
					dialog.setContentPane(tabs);
					
					dialog.setResizable(false);
					dialog.setPreferredSize(new Dimension(535, 500));
					dialog.setMinimumSize(dialog.getPreferredSize());
					dialog.setTitle("Peakaboo Plugins");
					dialog.setModal(false);
					dialog.setLocationRelativeTo(container.getWindow());
					dialog.setVisible(true);
					
				},
				null, null
		));	
		
		menu.add(createMenuItem(
				"Open Plugins Folder", null, "Opens the plugins folder to add or remove plugin files",
				e -> {
					File appDataDir = Env.appDataDirectory(Version.program_name, "Plugins");
					appDataDir.mkdirs();
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.open(appDataDir);
					} catch (IOException e1) {
						TaskDialogs.showException(e1);
						e1.printStackTrace();
					}
				},
				null, null
		));
		
	
		
		menu.addSeparator();
		
		
		menu.add(createMenuItem(
				"Exit", StockIcon.WINDOW_CLOSE.toMenuIcon(), "Exits the Program",
				e -> System.exit(0),
				null, KeyEvent.VK_X
		));

		menuBar.add(menu);









		// EDIT Menu
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription("Edit this data set");


		
		undo = createMenuItem(
				"Undo", StockIcon.EDIT_UNDO.toMenuIcon(), "Undoes a previous action",
				e -> controller.history().undo(),
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK), KeyEvent.VK_U
		);
		menu.add(undo);

		redo = createMenuItem(
				"Redo", StockIcon.EDIT_REDO.toMenuIcon(), "Redoes a previously undone action",
				e -> controller.history().redo(),
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK), KeyEvent.VK_R
		);
		menu.add(redo);

		menuBar.add(menu);






		// VIEW Menu
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("Change the way the plot is viewed");
		menuBar.add(menu);

		final JMenuItem logPlot, axes, monochrome, title, raw, fittings;

		
		logPlot = createMenuCheckItem(
				"Logarithmic Scale", null, "Toggles the plot between a linear and logarithmic scale",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setViewLog(menuitem.isSelected());
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), KeyEvent.VK_L
		);
		
		axes = createMenuCheckItem(
				"Axes", null, "Toggles display of axes and grid lines",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowAxes(menuitem.isSelected());
				},
				null, null
		);

		title = createMenuCheckItem(
				"Title", null, "Toggles display of the current data set's title",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowTitle(menuitem.isSelected());
				},
				null, null
		);

		monochrome = createMenuCheckItem(
				"Monochrome", null, "Toggles the monochrome colour palette",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setMonochrome(menuitem.isSelected());
				},
				null, KeyEvent.VK_M
		);
		
		

		raw = createMenuCheckItem(
				"Raw Data Outline", null, "Toggles an outline of the original raw data",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowRawData(menuitem.isSelected());
				},
				null, KeyEvent.VK_O
		);
		
		fittings = createMenuCheckItem(
				"Individual Fittings", null, "Switches between showing all fittings as a single curve and showing all fittings individually",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowIndividualSelections(menuitem.isSelected());
				},
				null, KeyEvent.VK_O
		);	
		
		menu.add(logPlot);
		menu.add(axes);
		menu.add(title);
		menu.add(monochrome);
		
		menu.addSeparator();
		
		menu.add(raw);
		menu.add(fittings);


		// Element Drawing submenu
		JMenu elementDrawing = new JMenu("Curve Fit");
		final JCheckBoxMenuItem etitles, emarkings, eintensities;

		
		etitles = createMenuCheckItem(
				"Element Names", null, "Label fittings with the names of their elements",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowElementTitles(menuitem.isSelected());
				},
				null, null
		);
		elementDrawing.add(etitles);

		
		emarkings = createMenuCheckItem(
				"Markings", null, "Label fittings with lines denoting their energies",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowElementMarkers(menuitem.isSelected());
				},
				null, null
		);
		elementDrawing.add(emarkings);

		
		eintensities = createMenuCheckItem(
				"Heights", null, "Label fittings with their heights",
				e -> {
					JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
					controller.settings().setShowElementIntensities(menuitem.isSelected());
				},
				null, null
		);
		elementDrawing.add(eintensities);

		
		menu.add(elementDrawing);
		
		
		
		
		JMenu escapePeaks = new JMenu("Escape Peaks");
		
		
		final ButtonGroup escapePeakGroup = new ButtonGroup();
		
		for (EscapePeakType t : EscapePeakType.values())
		{
			final JRadioButtonMenuItem escapeItem = new JRadioButtonMenuItem(t.show());
			escapePeakGroup.add(escapeItem);
			escapePeaks.add(escapeItem);
			if (t == EscapePeakType.SILICON) escapeItem.setSelected(true);
			
			
			final EscapePeakType finalt = t;
			
			escapeItem.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e)
				{
					escapeItem.setSelected(true);
					controller.settings().setEscapePeakType(finalt);
				}
			});
			
			controller.addListener(new EventfulTypeListener<String>() {

				public void change(String message)
				{
					escapeItem.setSelected( controller.settings().getEscapePeakType() == finalt );
				}});

		}
		
		menu.add(escapePeaks);
		

		menu.addSeparator();
		


		final JRadioButtonMenuItem individual, average, maximum;

		ButtonGroup viewGroup = new ButtonGroup();

		individual = new JRadioButtonMenuItem(ChannelCompositeMode.NONE.show());
		individual.setSelected(true);
		individual.setMnemonic(KeyEvent.VK_I);
		individual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		individual.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				controller.settings().setShowChannelMode(ChannelCompositeMode.NONE);
			}});
		viewGroup.add(individual);
		menu.add(individual);

		average = new JRadioButtonMenuItem(ChannelCompositeMode.AVERAGE.show());
		average.setMnemonic(KeyEvent.VK_M);
		average.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		average.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				controller.settings().setShowChannelMode(ChannelCompositeMode.AVERAGE);
			}});
		viewGroup.add(average);
		menu.add(average);

		maximum = new JRadioButtonMenuItem(ChannelCompositeMode.MAXIMUM.show());
		maximum.setMnemonic(KeyEvent.VK_T);
		maximum.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		maximum.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				controller.settings().setShowChannelMode(ChannelCompositeMode.MAXIMUM);
			}});
		viewGroup.add(maximum);
		menu.add(maximum);
		
		
		

		
		
		//Mapping Menu
		menu = new JMenu("Mapping");
		menu.setMnemonic(KeyEvent.VK_S);


		populateFittingMenu(menu);

		menuBar.add(menu);
		
		
		
		
		
		
		//HELP Menu
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem contents = createMenuItem(
				"Help", StockIcon.BADGE_HELP.toMenuIcon(), "",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionHelp();
					}
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), null
		);
		menu.add(contents);
		
		JMenuItem about = createMenuItem(
				"About", StockIcon.MISC_ABOUT.toMenuIcon(), "",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionAbout();
					}
				},
				null, null
		);
		menu.add(about);
		
		
		menuBar.add(menu);
		
		
		

		container.setJMenuBar(menuBar);

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{

				logPlot.setSelected(controller.settings().getViewLog());
				axes.setSelected(controller.settings().getShowAxes());
				title.setSelected(controller.settings().getShowTitle());
				monochrome.setSelected(controller.settings().getMonochrome());

				etitles.setSelected(controller.settings().getShowElementTitles());
				emarkings.setSelected(controller.settings().getShowElementMarkers());
				eintensities.setSelected(controller.settings().getShowElementIntensities());

				switch (controller.settings().getChannelCompositeType())
				{

					case NONE:
						individual.setSelected(true);
						break;
					case AVERAGE:
						average.setSelected(true);
						break;
					case MAXIMUM:
						maximum.setSelected(true);
						break;

				}

				raw.setSelected(controller.settings().getShowRawData());
				fittings.setSelected(controller.settings().getShowIndividualSelections());

			}
		});

	}


	private JPanel createBottomBar()
	{
		bottomPanel = new ClearPanel();
		bottomPanel.setBorder(Spacing.bTiny());
		bottomPanel.setLayout(new BorderLayout());

		channelLabel.setBorder(Spacing.bSmall());
		bottomPanel.add(channelLabel, BorderLayout.CENTER);

		zoomSlider = createZoomPanel();
		bottomPanel.add(zoomSlider, BorderLayout.EAST);

		scanSelector = new ClearPanel();
		scanSelector.setLayout(new BoxLayout(scanSelector, BoxLayout.X_AXIS));

		scanNo = new JSpinner();
		scanNo.getEditor().setPreferredSize(new Dimension(50, 0));
		scanLabel = new JLabel("Scan");
		scanLabel.setBorder(Spacing.bSmall());
		scanBlock = new ComplexToggle(
			StockIcon.CHOOSE_CANCEL,
			"Flag this scan to exclude it and extrapolate it from neighbouring points in maps", "");

		scanSelector.add(scanLabel);
		scanSelector.add(Box.createHorizontalStrut(2));
		scanSelector.add(scanNo);
		scanSelector.add(Box.createHorizontalStrut(4));
		scanSelector.add(scanBlock);
		scanSelector.add(Box.createHorizontalStrut(4));

		scanNo.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				JSpinner scan = (JSpinner) e.getSource();
				int value = (Integer) ((scan).getValue());
				controller.settings().setScanNumber(value - 1);
			}
		});
		bottomPanel.add(scanSelector, BorderLayout.WEST);

		scanBlock.setFocusable(false);
		scanBlock.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if (scanBlock.isSelected()) {
					controller.data().getDiscards().discard(controller.settings().getScanNumber());
				} else {
					controller.data().getDiscards().undiscard(controller.settings().getScanNumber());
				}
			}
		});

		return bottomPanel;

	}

	private ZoomSlider createZoomPanel()
	{
		final ZoomSlider slider = new ZoomSlider(10, 500, 10);
		slider.setValue(100);
		slider.addListener(new EventfulListener() {
			
			public void change()
			{
				controller.settings().setZoom(slider.getValue() / 100f);
			}
		});
		return slider;
	}
	

	// prompts the user with a file selection dialogue
	// reads the returned file list, loads the related
	// data set, and returns it to the caller
	public List<File> openNewDataset(String[][] exts, String[] desc)
	{
		return SwidgetIO.openFiles(container.getContainer(), "Select Data Files to Open", exts, desc, controller.data().getDataSet().getDataSourcePath());
	}


	private void mouseMoveCanvasEvent(int x)
	{

		int channel = canvas.channelFromCoordinate(x);
		float energy = controller.settings().getEnergyForChannel(channel);
		Pair<Float, Float> values = controller.settings().getValueForChannel(channel);

		StringBuilder sb = new StringBuilder();
		String sep = ",  ";

		if (values != null)
		{

			DecimalFormat fmtObj = new DecimalFormat("#######0.00");
			
			sb.append("View Type: ");
			sb.append(controller.settings().getChannelCompositeType().show());
			sb.append(sep);
			sb.append("Channel: ");
			sb.append(String.valueOf(channel));
			sb.append(sep);
			sb.append("Energy: ");
			sb.append(fmtObj.format(energy));
			sb.append(sep);
			sb.append("Value: ");
			sb.append(fmtObj.format(values.first));
			if (! values.first.equals(values.second)) {
				sb.append(sep);
				sb.append("Unfiltered Value: ");
				sb.append(fmtObj.format(values.second));
			}

		}
		else
		{
			
			sb.append("View Type: ");
			sb.append(controller.settings().getChannelCompositeType().show());
			sb.append(sep);
			sb.append("Channel: ");
			sb.append("-");
			
		}
		
		channelLabel.setText(sb.toString());
		
	}










	// ////////////////////////////////////////////////////////
	// UI ACTIONS
	// ////////////////////////////////////////////////////////

	private void actionAbout()
	{
		new swidget.dialogues.AboutDialogue(
				container.getWindow(),
				Version.program_name,
				"XRF Analysis Software",
				"www.sciencestudioproject.com",
				"Copyright &copy; 2009-2012 by <br> The University of Western Ontario and <br> The Canadian Light Source Inc.",
				IOOperations.readTextFromJar("/peakaboo/licence.txt"),
				IOOperations.readTextFromJar("/peakaboo/credits.txt"),
				Version.logo,
				Integer.toString(Version.versionNoMajor),
				Version.longVersionNo,
				Version.releaseDescription,
				Version.buildDate);
	}
	
	private void actionHelp()
	{
		Apps.browser("http://sciencestudio.net/downloads/Peakaboo4/Peakaboo%204%20Users%20Guide.pdf");
	}
	
	private void actionOpenData()
	{		
		/*
		List<DataSource> formats =  new ArrayList<DataSource>(DataSourceLoader.getDataSourcePlugins());
		String[][] exts = new String[formats.size()][];
		String[] descs = new String[formats.size()];
		for (int i = 0; i < formats.size(); i++)
		{
			exts[i] = formats.get(i).getFileFormat().getFileExtensions().toArray(new String[]{});
			descs[i] = formats.get(i).getFileFormat().getFormatName();
		}
		*/
		String[][] exts = {};
		String[] descs = {};

		List<File> files = openNewDataset(exts, descs);
		if (files == null) return;
		loadFiles(files);
		
	}
	

	public void loadFiles(List<File> filenames)
	{

		List<PluginDataSource> candidates =  DataSourceLoader.getPluginSet().newInstances();
		List<DataSource> formats = DataSourceLookup.findDataSourcesForFiles(filenames, candidates);
		
		if (formats.size() > 1)
		{
			DataSourceSelection selection = new DataSourceSelection();
			DataSource dsp = selection.pickDSP(container.getContainer(), formats);
			if (dsp != null) loadFiles(filenames, dsp);
		}
		else if (formats.size() == 0)
		{
			JOptionPane.showMessageDialog(
					this, 
					"Could not determine the data format of the selected file(s)", 
					"Open Failed", 
					JOptionPane.ERROR_MESSAGE, 
					StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
				);
		}
		else
		{
			loadFiles(filenames, formats.get(0));
		}
		
	}
	
	public void loadFiles(List<File> files, DataSource dsp)
	{
		if (files != null)
		{

			ExecutorSet<DatasetReadResult> reading = controller.data().TASK_readFileListAsDataset(files, dsp);
			ExecutorSetView view = new ExecutorSetView(container.getWindow(), reading);
			
			//handle some race condition where the window gets told to close too early on failure
			//I don't think its in my code, but I don't know for sure
			view.setVisible(false);
			
			DatasetReadResult result = reading.getResult();
			if (result.status == ReadStatus.FAILED)
			{
				JOptionPane.showMessageDialog(this, "Peakaboo could not open this dataset.\n" + result.message, "Open Failed", JOptionPane.OK_OPTION, StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
			}

			// set some controls based on the fact that we have just loaded a
			// new data set
			savedSessionFileName = null;

		}
	}


	private void actionMap(FittingTransform type)
	{

		if (!controller.data().hasDataSet()) return;


		final ExecutorSet<MapResultSet> tasks = controller.getMapCreationTask(type);
		if (tasks == null) return;

		new ExecutorSetView(container.getWindow(), tasks);


		if (tasks.getCompleted())
		{

			MappingController mapController = controller.checkoutMapController();
			PeakabooMapperSwing mapperWindow;

			MapResultSet results = tasks.getResult();


			if (controller.data().getDataSet().hasPhysicalSize())
			{

				mapController.mapsController.setMapData(
						results,
						controller.data().getDataSet().getScanData().datasetName(),
						controller.data().getDataSet().getDataSize().getDataDimensions(),
						controller.data().getDataSet().getPhysicalSize().getPhysicalDimensions(),
						controller.data().getDataSet().getPhysicalSize().getPhysicalUnit(),
						controller.data().getDiscards().list()
					);
				
			} else {
								
				mapController.mapsController.setMapData(
						results,
						controller.data().getDataSet().getScanData().datasetName(),
						controller.data().getDiscards().list()
					);
				
			}
			
			mapController.mapsController.setInterpolation(0);

			
			mapperWindow = new PeakabooMapperSwing(container.getContainer(), mapController, controller);

			mapperWindow.showDialog();

		}

	}


	private void actionSaveSession()
	{

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			controller.savePreferences(baos);
			savedSessionFileName = SwidgetIO.saveFile(
					container.getWindow(),
					"Save Session Data",
					"peakaboo",
					"Peakaboo Session File",
					savedSessionFileName,
					baos);
			baos.close();
		}
		catch (IOException e)
		{
			TaskDialogs.showException(e);
			e.printStackTrace();
		}

	}


	private void actionSavePicture()
	{
		if (saveFilesFolder == null) {
			saveFilesFolder = controller.data().getDataSet().getDataSourcePath();
		}
		SavePicture sp = new SavePicture(container.getWindow(), canvas, saveFilesFolder);
		saveFilesFolder = sp.getStartingFolder(); 
	}


	private void actionSaveFittedDataInformation()
	{
		if (saveFilesFolder == null) {
			saveFilesFolder = controller.data().getDataSet().getDataSourcePath();
		}
		
		
		//Spectrum data = filters.filterDataUnsynchronized(new ISpectrum(datasetProvider.getScan(ordinal)), false);
		final FilterSet filters = controller.filtering().getActiveFilters();
		

		try
		{

			final File tempfile = File.createTempFile("Peakaboo - ", " export");
			tempfile.deleteOnExit();
			
			// get an output stream to write the data to
			final DummyExecutor exec = new DummyExecutor(controller.data().getDataSet().getScanData().scanCount());
			ExecutorSet<Exception> execset = new ExecutorSet<Exception>("Exporting Data") {
				
				@Override
				protected Exception execute()
				{
					try {
						
						exec.advanceState();
						
						OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tempfile));
						Iterator<ReadOnlySpectrum> iter = controller.data().getScanIterator();
						while (iter.hasNext())
						{
							ReadOnlySpectrum s = iter.next();
							s = filters.applyFiltersUnsynchronized(s, false);
							osw.write(s.toString() + "\n");
							exec.workUnitCompleted();
						}
						osw.close();
						
						exec.advanceState();
						
						return null;
					} catch (Exception e) { return e; }
				}
			};
			execset.addExecutor(exec, "Applying Filters");
			
			
			new ExecutorSetView(container.getWindow(), execset);
			
			Exception e = execset.getResult();
			if (e != null)
			{
				e.printStackTrace();
				TaskDialogs.showException(e);
				tempfile.delete();
				return;
			}
			
			InputStream fis = new FileInputStream(tempfile);

			// save the contents of the output stream to a file.
			saveFilesFolder = SwidgetIO.saveFile(
					container.getContainer(),
					"Save Fitted Data to Text File",
					"txt",
					"Text File",
					saveFilesFolder,
					fis);

			fis.close();
			tempfile.delete();


		}
		catch (IOException e)
		{
			TaskDialogs.showException(e);
			e.printStackTrace();
		}
		
	}
	
	private void actionSaveFittingInformation()
	{

		if (saveFilesFolder == null) {
			saveFilesFolder = controller.data().getDataSet().getDataSourcePath();
		}

		List<TransitionSeries> tss = controller.fitting().getFittedTransitionSeries();
		float intensity;

		try
		{

			// get an output stream to write the data to
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(baos);

			// write out the data
			for (TransitionSeries ts : tss)
			{

				if (ts.visible)
				{
					intensity = controller.fitting().getTransitionSeriesIntensity(ts);
					osw.write(ts.toString() + ", " + SigDigits.roundFloatTo(intensity, 2) + "\n");
				}
			}
			osw.close();

			// save the contents of the output stream to a file.
			saveFilesFolder = SwidgetIO.saveFile(
					container.getContainer(),
					"Save Fitting Information to Text File",
					"txt",
					"Text File",
					saveFilesFolder,
					baos);



		}
		catch (IOException e)
		{
			TaskDialogs.showException(e);
			e.printStackTrace();
		}

	}


	public void actionLoadSession()
	{

		try
		{
			File f = SwidgetIO.openFile(
					container.getContainer(),
					"Load Session Data",
					new String[][] {{"peakaboo"}},
					new String[] {"Peakaboo Session Data"},
					savedSessionFileName);
			
			if (f != null) {
				controller.loadPreferences(new FileInputStream(f), false);
			}
		}
		catch (IOException e)
		{
			TaskDialogs.showException(e);
			e.printStackTrace();
		}

	}


	public void actionShowInfo()
	{
		
		Map<String, String> properties = new LinkedHashMap<String, String>();

		Metadata metadata = controller.data().getDataSet().getMetadata();
		
		properties.put("Date of Creation", metadata.getCreationTime());
		properties.put("Created By", metadata.getCreator());
		
		properties.put("Project Name", metadata.getProjectName());
		properties.put("Session Name", metadata.getSessionName());
		properties.put("Experiment Name", metadata.getExperimentName());
		properties.put("Sample Name", metadata.getSampleName());
		properties.put("Scan Name", metadata.getScanName());
		
		properties.put("Facility", metadata.getFacilityName());
		properties.put("Laboratory", metadata.getLaboratoryName());
		properties.put("Instrument", metadata.getInstrumentName());
		properties.put("Technique", metadata.getTechniqueName());
		
		new PropertyDialogue("Dataset Information", "Extended Information", container.getWindow(), properties);

	}

}
