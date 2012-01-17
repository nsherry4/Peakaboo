package peakaboo.ui.swing.plotting;



import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import commonenvironment.AbstractFile;
import commonenvironment.Apps;
import commonenvironment.IOOperations;

import eventful.EventfulEnumListener;
import eventful.EventfulListener;
import eventful.EventfulTypeListener;
import fava.datatypes.Maybe;
import fava.datatypes.Pair;
import fava.functionable.FList;

import peakaboo.common.Version;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataController;
import peakaboo.controller.plotter.settings.ChannelCompositeMode;
import peakaboo.controller.plotter.settings.SettingsController;
import peakaboo.controller.plotter.undo.UndoController;
import peakaboo.curvefit.fitting.EscapePeakType;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.datasource.DataFormat;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.swing.PeakabooMapperSwing;
import peakaboo.ui.swing.PlotterFrame;
import peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;
import scidraw.swing.SavePicture;
import scitypes.SigDigits;
import swidget.dialogues.PropertyDialogue;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.DropdownImageButton;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.ZoomSlider;
import swidget.widgets.DropdownImageButton.Actions;
import swidget.widgets.toggle.ComplexToggle;



public class PlotPanel extends ClearPanel
{

	private PlotterFrame		container;



	PlotController				controller;
	SettingsController			settingsController;
	DataController				dataController;
	UndoController				undoController;
	
	PlotCanvas					canvas;

	//FILE
	JMenuItem					snapshotMenuItem;
	JMenuItem					exportTextMenuItem;
	JMenuItem					saveSession;
	

	
	//EDIT
	JMenuItem					undo, redo;
	
	
	JComboBox					titleCombo;

	JSpinner					scanNo;
	JLabel						scanLabel;
	JToggleButton				scanBlock;
	JLabel						channelLabel;


	JSpinner					energy;
	ChangeListener				energyListener;
	
	ImageButton					toolbarSnapshot;
	DropdownImageButton			toolbarMap;
	ImageButton					toolbarInfo;
	ZoomSlider					zoomSlider;
	JPanel						bottomPanel;
	JPanel						scanSelector;
	JScrollPane					scrolledCanvas;

	String						savePictureFolder;
	String						savedSessionFileName;


	public PlotPanel(PlotterFrame container)
	{
		this.container = container;

		savedSessionFileName = null;

		controller = new PlotController();
		dataController = controller.dataController;
		settingsController = controller.settingsController;
		undoController = controller.undoController;
		
		

		initGUI();

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String message)
			{
				setWidgetsState();
			}
		});

		setWidgetsState();



	}


	public PlotController getController()
	{
		return controller;
	}


	private void setWidgetsState()
	{

		snapshotMenuItem.setEnabled(false);
		exportTextMenuItem.setEnabled(false);
		toolbarSnapshot.setEnabled(false);

		if (dataController.hasDataSet())
		{

			bottomPanel.setEnabled(true);
			snapshotMenuItem.setEnabled(true);
			exportTextMenuItem.setEnabled(true);
			toolbarSnapshot.setEnabled(true);


			toolbarMap.setEnabled(controller.fittingController.canMap());


			if (dataController.getScanHasExtendedInformation())
			{
				toolbarInfo.setEnabled(true);
			}
			else
			{
				toolbarInfo.setEnabled(false);
			}

			setEnergySpinner((double) settingsController.getMaxEnergy());


			if (settingsController.getChannelCompositeType() == ChannelCompositeMode.NONE)
			{

				scanNo.setValue(settingsController.getScanNumber() + 1);
				scanBlock.setSelected(dataController.getScanDiscarded());
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

		undo.setEnabled(undoController.canUndo());
		redo.setEnabled(undoController.canRedo());
		undo.setText("Undo " + undoController.getNextUndo());
		redo.setText("Redo " + undoController.getNextRedo());

		zoomSlider.setValueEventless((int)(settingsController.getZoom()*100));
		setTitleBar();

		container.validate();
		container.repaint();

	}


	private void setEnergySpinner(double value)
	{
		//dont let the listeners get wind of this change		
		energy.removeChangeListener(energyListener);
		energy.setValue((double) settingsController.getMaxEnergy());
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
		JToolBar toolBar = new JToolBar();
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
		
		class MouseHandler implements MouseMotionListener, MouseListener
		{

			private Point	p0;
			private boolean	dragging;

			JViewport		viewPort		= scrolledCanvas.getViewport();
			Point			scrollPosition	= viewPort.getViewPosition();

			Cursor oldCursor;

			private Point getPoint(MouseEvent e)
			{
				Point p = e.getPoint();
				p.x += canvas.getLocationOnScreen().x;

				return p;
			}


			private void update(MouseEvent e)
			{

				if (p0 == null) return;
				
				Point p1 = getPoint(e);
				int dx = p1.x - p0.x;
				p0 = getPoint(e);
				scrollPosition.x -= dx;

				if (scrollPosition.x < 0) scrollPosition.x = 0;
				if (scrollPosition.x > canvas.getWidth() - viewPort.getWidth()) scrollPosition.x = canvas.getWidth()
						- viewPort.getWidth();

				viewPort.setViewPosition(scrollPosition);

			}


			public void mouseDragged(MouseEvent e)
			{
				if (dragging)
				{
					update(e);
				}
			}


			public void mouseMoved(MouseEvent e)
			{
				if (dragging)
				{
					update(e);
				}
			}


			public void mouseClicked(MouseEvent e){}


			public void mouseEntered(MouseEvent e){}


			public void mouseExited(MouseEvent e){}


			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() != MouseEvent.BUTTON1) return;
				if (dragging) return;
				
				p0 = getPoint(e);
				scrollPosition = viewPort.getViewPosition();
				dragging = true;
				oldCursor = canvas.getCursor();
				canvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}


			public void mouseReleased(MouseEvent e)
			{
				
				if (e.getButton() != MouseEvent.BUTTON1) return;
				
				update(e);
				dragging = false;
				p0 = null;
				canvas.setCursor(oldCursor);
			}

		}
		MouseHandler mh = new MouseHandler();

		canvas.addMouseMotionListener(mh);
		canvas.addMouseListener(mh);


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
		tabs.add(new CurveFittingView(controller.fittingController, canvas), 0);
		tabs.add(new FiltersetViewer(controller.filteringController, container), 1);

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
		titleString = new StringBuffer(Version.title);
		if (dataController.hasDataSet())
		{

			String dataSetName = dataController.getDatasetName();
			titleString.append(" (" + dataSetName + ")");

			if (settingsController.getChannelCompositeType() == ChannelCompositeMode.NONE)
			{
				titleString.append(" - " + dataController.getCurrentScanName());
			}
			else
			{
				titleString.append(" - " + settingsController.getChannelCompositeType().show());
			}

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
				settingsController.setMaxEnergy(value);

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
				mapHeights.setEnabled(controller.fittingController.canMap());
				mapArea.setEnabled(controller.fittingController.canMap());
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
		
		configureMenuItem(menuItem, title, icon, description, listener, key, mnemonic);
		
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
		
		configureMenuItem(menuItem, title, icon, description, listener, key, mnemonic);
		
		return menuItem;
		
	}
	
	private void configureMenuItem(JMenuItem menuItem, String title, ImageIcon icon, String description, ActionListener listener, KeyStroke key, Integer mnemonic)
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
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionOpenData();
					}
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.ActionEvent.CTRL_MASK), KeyEvent.VK_O
		));
		
		menu.add(createMenuItem(
				"Open Sample Data", null, "Open a sample data set for learning or demonstrating Peakaboo",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionOpenSampleData();
					}
				},
				null, null
		));
		
		
		menu.addSeparator();

		
		menu.add(createMenuItem(
				"Save Session", StockIcon.DOCUMENT_SAVE.toMenuIcon(), null, 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionSaveSession();
					}
				}, 
				null, null
		));
		
		menu.add(createMenuItem(
				"Load Session", null, null,
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionLoadSession();
					}
				}, 
				null, null
		));
		


		menu.addSeparator();

		
		snapshotMenuItem = createMenuItem(
				"Export Plot as Image\u2026", StockIcon.DEVICE_CAMERA.toMenuIcon(), "Saves the current plot as an image",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionSavePicture();
					}
				}, 
				KeyStroke.getKeyStroke(KeyEvent.VK_P, java.awt.event.ActionEvent.CTRL_MASK), KeyEvent.VK_P
		);
		menu.add(snapshotMenuItem);

		
		exportTextMenuItem = createMenuItem(
				"Export Fittings as Text", StockIcon.DOCUMENT_EXPORT.toMenuIcon(), "Saves the current fitting data to a text file",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						actionSaveFittingInformation();
					}
				}, 
				null, null
		);
		menu.add(exportTextMenuItem);


		menu.addSeparator();

		
		menu.add(createMenuItem(
				"Exit", StockIcon.WINDOW_CLOSE.toMenuIcon(), "Exits the Program",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				}, 
				null, KeyEvent.VK_X
		));

		menuBar.add(menu);









		// EDIT Menu
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription("Edit this data set");


		
		undo = createMenuItem(
				"Undo", StockIcon.EDIT_UNDO.toMenuIcon(), "Undoes a previous action",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						undoController.undo();
					}
				}, 
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.event.ActionEvent.CTRL_MASK), KeyEvent.VK_U
		);
		menu.add(undo);

		redo = createMenuItem(
				"Redo", StockIcon.EDIT_REDO.toMenuIcon(), "Redoes a previously undone action",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						undoController.redo();
					}
				}, 
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.event.ActionEvent.CTRL_MASK), KeyEvent.VK_R
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
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setViewLog(menuitem.isSelected());
					}
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.event.ActionEvent.CTRL_MASK), KeyEvent.VK_L
		);
		
		axes = createMenuCheckItem(
				"Axes", null, "Toggles display of axes and grid lines",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowAxes(menuitem.isSelected());
					}
				},
				null, null
		);

		title = createMenuCheckItem(
				"Title", null, "Toggles display of the current data set's title",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowTitle(menuitem.isSelected());
					}
				},
				null, null
		);

		monochrome = createMenuCheckItem(
				"Monochrome", null, "Toggles the monochrome colour palette",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setMonochrome(menuitem.isSelected());
					}
				},
				null, KeyEvent.VK_M
		);
		
		

		raw = createMenuCheckItem(
				"Raw Data Outline", null, "Toggles an outline of the original raw data",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowRawData(menuitem.isSelected());
					}
				},
				null, KeyEvent.VK_O
		);
		
		fittings = createMenuCheckItem(
				"Individual Fittings", null, "Switches between showing all fittings as a single curve and showing all fittings individually",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowIndividualSelections(menuitem.isSelected());
					}
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
				"Names", null, "Label fittings with their names",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowElementTitles(menuitem.isSelected());
					}
				},
				null, null
		);
		elementDrawing.add(etitles);

		
		emarkings = createMenuCheckItem(
				"Markings", null, "Label fittings with lines denoting their energies",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowElementMarkers(menuitem.isSelected());
					}
				},
				null, null
		);
		elementDrawing.add(emarkings);

		
		eintensities = createMenuCheckItem(
				"Heights", null, "Label fittings with their heights",
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e)
					{
						JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
						settingsController.setShowElementIntensities(menuitem.isSelected());
					}
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
					settingsController.setEscapePeakType(finalt);
				}
			});
			
			controller.addListener(new EventfulTypeListener<String>() {

				public void change(String message)
				{
					escapeItem.setSelected( settingsController.getEscapePeakType() == finalt );
				}});

		}
		
		menu.add(escapePeaks);
		

		menu.addSeparator();
		


		final JRadioButtonMenuItem individual, average, maximum;

		ButtonGroup viewGroup = new ButtonGroup();

		individual = new JRadioButtonMenuItem(ChannelCompositeMode.NONE.show());
		individual.setSelected(true);
		individual.setMnemonic(KeyEvent.VK_I);
		individual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, java.awt.event.ActionEvent.CTRL_MASK));
		individual.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				settingsController.setShowChannelMode(ChannelCompositeMode.NONE);
			}});
		viewGroup.add(individual);
		menu.add(individual);

		average = new JRadioButtonMenuItem(ChannelCompositeMode.AVERAGE.show());
		average.setMnemonic(KeyEvent.VK_M);
		average.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, java.awt.event.ActionEvent.CTRL_MASK));
		average.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				settingsController.setShowChannelMode(ChannelCompositeMode.AVERAGE);
			}});
		viewGroup.add(average);
		menu.add(average);

		maximum = new JRadioButtonMenuItem(ChannelCompositeMode.MAXIMUM.show());
		maximum.setMnemonic(KeyEvent.VK_T);
		maximum.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, java.awt.event.ActionEvent.CTRL_MASK));
		maximum.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				settingsController.setShowChannelMode(ChannelCompositeMode.MAXIMUM);
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

				logPlot.setSelected(settingsController.getViewLog());
				axes.setSelected(settingsController.getShowAxes());
				title.setSelected(settingsController.getShowTitle());
				monochrome.setSelected(settingsController.getMonochrome());

				etitles.setSelected(settingsController.getShowElementTitles());
				emarkings.setSelected(settingsController.getShowElementMarkers());
				eintensities.setSelected(settingsController.getShowElementIntensities());

				switch (settingsController.getChannelCompositeType())
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

				raw.setSelected(settingsController.getShowRawData());
				fittings.setSelected(settingsController.getShowIndividualSelections());

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
			"Flag this scan as bad to exclude it from the data set", "");

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
				settingsController.setScanNumber(value - 1);
			}
		});
		bottomPanel.add(scanSelector, BorderLayout.WEST);

		scanBlock.setFocusable(false);
		scanBlock.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				dataController.setScanDiscarded(scanBlock.isSelected());
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
				settingsController.setZoom(slider.getValue() / 100f);
			}
		});
		return slider;
	}
	

	// prompts the user with a file selection dialogue
	// reads the returned file list, loads the related
	// data set, and returns it to the caller
	public List<AbstractFile> openNewDataset(String[][] exts, String[] desc)
	{
		return SwidgetIO.openFiles(container, "Select Data Files to Open", exts, desc, dataController.getDataSourceFolder());
	}


	private void mouseMoveCanvasEvent(int x)
	{

		int channel = canvas.channelFromCoordinate(x);
		float energy = settingsController.getEnergyForChannel(channel);
		Pair<Float, Float> values = settingsController.getValueForChannel(channel);

		StringBuilder sb = new StringBuilder();
		String sep = ",  ";

		if (values != null)
		{

			DecimalFormat fmtObj = new DecimalFormat("#######0.00");
			
			sb.append("View Type: ");
			sb.append(settingsController.getChannelCompositeType().show());
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
			sb.append(settingsController.getChannelCompositeType().show());
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
				container,
				Version.program_name,
				"XRF Analysis Software",
				"www.sciencestudioproject.com",
				"Copyright &copy; 2009-2010 by <br> The University of Western Ontario and <br> The Canadian Light Source Inc.",
				IOOperations.readTextFromJar("/peakaboo/licence.txt"),
				IOOperations.readTextFromJar("/peakaboo/credits.txt"),
				Version.logo,
				Integer.toString(Version.versionNo),
				Version.longVersionNo,
				Version.buildDate,
				Version.release);
	}
	
	private void actionHelp()
	{
		Apps.browser("http://sciencestudioproject.com/Peakaboo/help.php");
	}
	
	private void actionOpenData()
	{

		List<AbstractFile> files;

		List<DataFormat> formats =  new ArrayList<DataFormat>(dataController.getDataFormats());
			
		String[][] exts = new String[formats.size()][];
		String[] descs = new String[formats.size()];
		for (int i = 0; i < formats.size(); i++)
		{
			exts[i] = formats.get(i).extensions.toArray(new String[]{});
			descs[i] = formats.get(i).name;
		}

		files = openNewDataset(exts, descs);
		
		loadFiles(files);

	}
	
	private void actionOpenSampleData()
	{
		loadFiles(  new FList<AbstractFile>(IOOperations.getFileFromJar("/peakaboo/datasource/SampleData.xml"))  );
	}


	public void loadFiles(List<AbstractFile> files)
	{
		if (files != null)
		{

			ExecutorSet<Maybe<Boolean>> reading = dataController.TASK_readFileListAsDataset(files);
			ExecutorSetView view = new ExecutorSetView(container, reading);
			
			//handle some race condition where the window gets told to close too early on failure
			//I don't think its in my code, but I don't know for sure
			view.setVisible(false);

			if (! reading.getResult().is()) {
				JOptionPane.showMessageDialog(this, "Failed to open dataset", "Read Failed", JOptionPane.OK_OPTION, StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON));
			}
			
			// TaskListView was blocking.. it is now closed
			System.gc();

			// set some controls based on the fact that we have just loaded a
			// new data set
			savedSessionFileName = null;

		}
	}


	private void actionMap(FittingTransform type)
	{

		if (!dataController.hasDataSet()) return;


		final ExecutorSet<MapResultSet> tasks = controller.TASK_getDataForMapFromSelectedRegions(type);
		if (tasks == null) return;

		new ExecutorSetView(container, tasks);


		if (tasks.getCompleted())
		{

			MappingController mapController = controller.checkoutMapController();
			PeakabooMapperSwing mapperWindow;

			MapResultSet results = tasks.getResult();


			if (dataController.hasDimensions())
			{

				mapController.mapsController.setMapData(
						results,
						dataController.getDatasetName(),
						dataController.getDataDimensions(),
						dataController.getRealDimensions(),
						dataController.getRealDimensionsUnits(),
						dataController.getDiscardedScanList()
					);
				
			} else {
								
				mapController.mapsController.setMapData(
						results,
						dataController.getDatasetName(),
						dataController.getDiscardedScanList()
					);
				
			}
			
			mapController.mapsController.setInterpolation(0);

			
			mapperWindow = new PeakabooMapperSwing(container, mapController, controller);

			mapperWindow.showDialog();

		}

	}


	private void actionSaveSession()
	{

		try
		{
			ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();
			controller.savePreferences(baos);
			savedSessionFileName = SwidgetIO.saveFile(
					container,
					"Save Session Data",
					"peakaboo",
					"Peakaboo Session File",
					savedSessionFileName,
					baos);
			baos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}


	private void actionSavePicture()
	{
		if (savePictureFolder == null) savePictureFolder = dataController.getDataSourceFolder();
		SavePicture sp = new SavePicture(container, canvas, savePictureFolder);
		savePictureFolder = sp.getStartingFolder(); 
	}


	private void actionSaveFittingInformation()
	{

		if (savePictureFolder == null) savePictureFolder = dataController.getDataSourceFolder();

		List<TransitionSeries> tss = controller.fittingController.getFittedTransitionSeries();
		float intensity;

		try
		{

			// get an output stream to write the data to
			ByteArrayOutputStream baos = SwidgetIO.getSaveFileBuffer();
			OutputStreamWriter osw = new OutputStreamWriter(baos);

			// write out the data
			for (TransitionSeries ts : tss)
			{

				if (ts.visible)
				{
					intensity = controller.fittingController.getTransitionSeriesIntensity(ts);
					osw.write(ts.toString() + ", " + SigDigits.roundFloatTo(intensity, 2) + "\n");
				}
			}
			osw.close();

			// save the contents of the output stream to a file.
			savePictureFolder = SwidgetIO.saveFile(
					container,
					"Save Fitting Data to Text File",
					"txt",
					"Text File",
					savePictureFolder,
					baos);



		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}


	public void actionLoadSession()
	{

		try
		{
			AbstractFile af = SwidgetIO.openFile(
					container,
					"Load Session Data",
					new String[][] {{"peakaboo"}},
					new String[] {"Peakaboo Session Data"},
					savedSessionFileName);
			if (af != null) controller.loadPreferences(af.getInputStream(), false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}


	public void actionShowInfo()
	{
		
		Map<String, String> properties = new HashMap<String, String>();

		properties.put("Date of Creation:", dataController.getScanCreationTime());
		properties.put("Created By:", dataController.getScanCreator());
		
		properties.put("Project Name: ", dataController.getScanProjectName());
		properties.put("Session Name: ", dataController.getScanSessionName());
		properties.put("Experiment Name: ", dataController.getScanExperimentName());
		properties.put("Sample Name: ", dataController.getScanSampleName());
		properties.put("Scan Name: ", dataController.getScanScanName());
		
		properties.put("Facility: ", dataController.getScanFacilityName());
		properties.put("Laboratory: ", dataController.getScanLaboratoryName());
		properties.put("Instrument: ", dataController.getScanInstrumentName());
		properties.put("Technique: ", dataController.getScanTechniqueName());
		
		new PropertyDialogue("Dataset Information", container, properties);

	}

}
