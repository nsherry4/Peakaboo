package peakaboo.ui.swing.plotting;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
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
import commonenvironment.Env;
import commonenvironment.IOOperations;

import eventful.EventfulEnumListener;
import eventful.EventfulTypeListener;
import fava.datatypes.Pair;
import fava.lists.FList;

import peakaboo.common.Version;
import peakaboo.controller.mapper.AllMapsModel;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.plotter.ChannelCompositeMode;
import peakaboo.controller.plotter.PlotController;
import peakaboo.curvefit.fitting.EscapePeakType;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.mapping.FittingTransform;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.swing.PeakabooMapperSwing;
import peakaboo.ui.swing.PlotterFrame;
import peakaboo.ui.swing.dialogues.PropertyDialogue;
import peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.widgets.pictures.SavePicture;
import plural.swing.PluralSetView;
import plural.workers.PluralSet;
import scitypes.Coord;
import scitypes.SigDigits;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.DropdownImageButton;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.DropdownImageButton.Actions;
import swidget.widgets.ImageButton.Layout;
import swidget.widgets.toggle.ComplexToggle;



public class PlotPanel extends ClearPanel
{

	private PlotterFrame		container;



	PlotController				controller;
	PlotCanvas					canvas;

	//FILE
	JMenuItem					snapshotMenuItem;
	JMenuItem					saveSession;

	//EDIT
	JMenuItem					undo, redo;
	
	//MAP
	List<JComponent>			mapButtons;


	JComboBox					titleCombo;

	JSpinner					scanNo;
	JLabel						scanLabel;
	JToggleButton				scanBlock;
	JLabel						channelLabel;


	JSpinner					energy;
	ChangeListener				energyListener;
	
	JSlider						zoomSlider;
	ChangeListener 				zoomSliderListener;

	ImageButton					toolbarSnapshot;
	DropdownImageButton			toolbarMap;
	ImageButton					toolbarInfo;
	JPanel						zoomPanel;
	JPanel						bottomPanel;
	JPanel						scanSelector;
	JScrollPane					scrolledCanvas;

	String						savePictureFolder;
	String						savedSessionFileName;


	public PlotPanel(PlotterFrame container)
	{
		this.container = container;

		savedSessionFileName = null;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D toy = bi.createGraphics();

		controller = new PlotController(toy);

		mapButtons = new FList<JComponent>();
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


	public void setWidgetsState()
	{

		snapshotMenuItem.setEnabled(false);
		toolbarSnapshot.setEnabled(false);

		// anything to do with the dataset
		if (controller.hasDataSet())
		{

			canvas.setHasData(true);

			bottomPanel.setEnabled(true);
			snapshotMenuItem.setEnabled(true);
			toolbarSnapshot.setEnabled(true);

			if (controller.getFittedTransitionSeries().size() == 0 || controller.datasetScanCount() == 0)
			{
				for (JComponent c : mapButtons) { c.setEnabled(false); }
				toolbarMap.setEnabled(false);
			}
			else
			{
				for (JComponent c : mapButtons) { c.setEnabled(true); }
				toolbarMap.setEnabled(true);
			}

			if (controller.getScanHasExtendedInformation())
			{
				toolbarInfo.setEnabled(true);
			}
			else
			{
				toolbarInfo.setEnabled(false);
			}

			setEnergySpinner((double) controller.getMaxEnergy());


			if (controller.getChannelCompositeType() == ChannelCompositeMode.NONE)
			{

				scanNo.setValue(controller.getScanNumber() + 1);
				scanBlock.setSelected(controller.getScanDiscarded());
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
			canvas.setHasData(false);
		}

		undo.setEnabled(controller.canUndo());
		redo.setEnabled(controller.canRedo());
		undo.setText("Undo " + controller.getNextUndo());
		redo.setText("Redo " + controller.getNextRedo());

		setSliderWithoutListener(zoomSlider, zoomSliderListener, (int)(controller.getZoom()*100));
		//zoomSlider.setValue((int) (controller.getZoom() * 100.0));
		setTitleBar();

		fullRedraw();

	}


	private void setEnergySpinner(double value)
	{
		//dont let the listeners get wind of this change		
		energy.removeChangeListener(energyListener);
		energy.setValue((double) controller.getMaxEnergy());
		energy.addChangeListener(energyListener);
	}

	private void setSliderWithoutListener(JSlider slider, ChangeListener listener, int value)
	{
		slider.removeChangeListener(listener);
		slider.setValue(value);
		slider.addChangeListener(listener);
	}


	private void initGUI()
	{

		canvas = new PlotCanvas(controller, this);
		canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		

		channelLabel = new JLabel("");
		channelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		channelLabel.setFont(channelLabel.getFont().deriveFont(Font.PLAIN));

		canvas.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e)
			{
			}


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


			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
			}


			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub
			}


			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub
			}


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
			
			public void componentShown(ComponentEvent e)
			{}
					
			public void componentResized(ComponentEvent e)
			{
				canvas.setCanvasSize();
			}
			
			public void componentMoved(ComponentEvent e)
			{}
			
			public void componentHidden(ComponentEvent e)
			{}
		});
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.add(new CurveFittingView(controller, canvas), 0);
		tabs.add(new FiltersetViewer(controller, container), 1);

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
		if (controller.hasDataSet())
		{

			String dataSetName = controller.getDatasetName();
			titleString.append(" (" + dataSetName + ")");

			if (controller.getChannelCompositeType() == ChannelCompositeMode.NONE)
			{
				titleString.append(" - " + controller.getCurrentScanName());
			}
			else
			{
				titleString.append(" - " + controller.getChannelCompositeType());
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

		// controls.addSeparator();

		c.gridx += 1;
		c.weightx = 1.0;
		toolbar.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;

		// toolbar.addSeparator();

		// toolbar.addSeparator();

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
				controller.setMaxEnergy(value);

			}
		};
		energy.addChangeListener(energyListener);
		c.gridx += 1;
		toolbar.add(energyControls, c);

		ibutton = new ToolbarImageButton(StockIcon.MISC_ABOUT, "About");
		ibutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				//new AboutDialogue(container);
				new swidget.dialogues.AboutDialogue(
					container,
					"Peakaboo",
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
		});
		c.gridx += 1;
		toolbar.add(ibutton, c);

	}

	
	public void populateFittingMenu(JComponent menu)
	{
		JMenuItem mapArea = new JMenuItem("Map Fitting Area");
		mapArea.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				actionMap(FittingTransform.AREA);
			}
		});
		mapArea.setEnabled(false);
		mapButtons.add(mapArea);
		menu.add(mapArea);
		
		
		JMenuItem mapHeights = new JMenuItem("Map Fitting Heights");
		mapHeights.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				actionMap(FittingTransform.HEIGHT);
			}
		});
		mapHeights.setEnabled(false);
		mapButtons.add(mapHeights);
		menu.add(mapHeights);
	}

	public void createMenu()
	{

		// Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		ActionListener fileMenuListener = new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{

				String command = e.getActionCommand();

				if (command == "Open Data...")
				{

					actionOpenData();

				}
				else if (command == "Export Data as Compressed File")
				{

				}
				else if (command == "Export Plot as Image")
				{

					actionSavePicture();

				}
				else if (command == "Export Fittings as Text")
				{

					actionSaveFittingInformation();

				}
				else if (command == "Save Session...")
				{

					actionSaveSession();

				}
				else if (command == "Load Session...")
				{

					actionLoadSession();

				}
				else if (command == "Exit")
				{

					System.exit(0);

				}

			}

		};

		// FILE Menu
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("Read and Write Data Sets");

		menuItem = new JMenuItem("Open Data...", IconFactory.getMenuIcon("document-open"));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.ActionEvent.CTRL_MASK));
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription("Opens new data sets.");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Save Session...", IconFactory.getMenuIcon("document-save-as"));
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		menuItem = new JMenuItem("Load Session...");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);


		// SEPARATOR
		menu.addSeparator();

		snapshotMenuItem = new JMenuItem("Export Plot as Image", StockIcon.DEVICE_CAMERA.toMenuIcon());
		snapshotMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, java.awt.event.ActionEvent.CTRL_MASK));
		snapshotMenuItem.setMnemonic(KeyEvent.VK_P);
		snapshotMenuItem.getAccessibleContext().setAccessibleDescription("Saves the current plot as an image");
		snapshotMenuItem.addActionListener(fileMenuListener);
		menu.add(snapshotMenuItem);

		menuItem = new JMenuItem("Export Fittings as Text", StockIcon.DOCUMENT_EXPORT.toMenuIcon());
		menuItem.getAccessibleContext().setAccessibleDescription("Saves the current fitting data to a text file");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		// SEPARATOR
		menu.addSeparator();

		menuItem = new JMenuItem("Exit", StockIcon.WINDOW_CLOSE.toMenuIcon());
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.getAccessibleContext().setAccessibleDescription("Exits the Program");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		menuBar.add(menu);









		// EDIT Menu
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription("Edit this data set");


		undo = new JMenuItem("Undo", StockIcon.EDIT_UNDO.toMenuIcon());
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.event.ActionEvent.CTRL_MASK));
		undo.setMnemonic(KeyEvent.VK_U);
		undo.getAccessibleContext().setAccessibleDescription("Undoes a previous action");
		undo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.undo();
			}
		});
		menu.add(undo);


		redo = new JMenuItem("Redo", StockIcon.EDIT_REDO.toMenuIcon());
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.event.ActionEvent.CTRL_MASK));
		redo.setMnemonic(KeyEvent.VK_R);
		redo.getAccessibleContext().setAccessibleDescription("Redoes a previously undone action");
		redo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.redo();
			}
		});
		menu.add(redo);

		menuBar.add(menu);






		// VIEW Menu
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("Change the way the plot is viewed");
		menuBar.add(menu);

		ActionListener toggleOptionListener = new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{

				String command = e.getActionCommand();
				JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();

				if (command == "Logarithmic Scale")
				{
					controller.setViewLog(menuitem.isSelected());
				}
				else if (command == "Axes")
				{
					controller.setShowAxes(menuitem.isSelected());
				}
				else if (command == "Title")
				{
					controller.setShowTitle(menuitem.isSelected());
				}
				else if (command == "Monochrome")
				{
					controller.setMonochrome(menuitem.isSelected());
				}

			}

		};

		ActionListener viewStyleListener = new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{

				String command = e.getActionCommand();

				if (command == "Individual Spectrum")
				{

					controller.setShowChannelSingle();

				}
				else if (command == "Mean Spectrum")
				{

					controller.setShowChannelAverage();

				}
				else if (command == "Strongest Signal per Channel")
				{

					controller.setShowChannelMaximum();

				}

			}

		};

		final JMenuItem logPlot, axes, monochrome, title;

		// a group of JMenuItems
		logPlot = new JCheckBoxMenuItem("Logarithmic Scale");
		logPlot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.event.ActionEvent.CTRL_MASK));
		logPlot.setMnemonic(KeyEvent.VK_L);
		logPlot.getAccessibleContext().setAccessibleDescription("Toggles the plot between a linear and logarithmic scale");
		logPlot.addActionListener(toggleOptionListener);
		menu.add(logPlot);

		axes = new JCheckBoxMenuItem("Axes");
		axes.addActionListener(toggleOptionListener);
		menu.add(axes);

		title = new JCheckBoxMenuItem("Title");
		title.addActionListener(toggleOptionListener);
		menu.add(title);

		monochrome = new JCheckBoxMenuItem("Monochrome");
		monochrome.setMnemonic(KeyEvent.VK_O);
		monochrome.addActionListener(toggleOptionListener);
		menu.add(monochrome);

		final JMenuItem raw, fittings;

		raw = new JCheckBoxMenuItem("Raw Data Outline");
		raw.setMnemonic(KeyEvent.VK_O);
		raw.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{

				JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
				controller.setShowRawData(menuitem.isSelected());
			}

		});
		menu.add(raw);

		fittings = new JCheckBoxMenuItem("Individual Fittings");
		fittings.setSelected(controller.getShowIndividualSelections());
		fittings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{

				JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
				controller.setShowIndividualSelections(menuitem.isSelected());
			}

		});
		menu.add(fittings);
		

		// SEPARATOR
		menu.addSeparator();

		final JRadioButtonMenuItem individual, average, maximum;

		// a group of radio button menu items
		ButtonGroup viewGroup = new ButtonGroup();

		individual = new JRadioButtonMenuItem("Individual Spectrum");
		individual.setSelected(true);
		individual.setMnemonic(KeyEvent.VK_I);
		individual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, java.awt.event.ActionEvent.CTRL_MASK));
		individual.addActionListener(viewStyleListener);
		viewGroup.add(individual);
		menu.add(individual);

		average = new JRadioButtonMenuItem("Mean Spectrum");
		average.setMnemonic(KeyEvent.VK_M);
		average.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, java.awt.event.ActionEvent.CTRL_MASK));
		average.addActionListener(viewStyleListener);
		viewGroup.add(average);
		menu.add(average);

		maximum = new JRadioButtonMenuItem("Strongest Signal per Channel");
		maximum.setMnemonic(KeyEvent.VK_T);
		maximum.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, java.awt.event.ActionEvent.CTRL_MASK));
		maximum.addActionListener(viewStyleListener);
		viewGroup.add(maximum);
		menu.add(maximum);

		// SEPARATOR
		menu.addSeparator();


		// Element Drawing submenu
		JMenu elementDrawing = new JMenu("Curve Fit");
		final JCheckBoxMenuItem etitles, emarkings, eintensities;

		// element name option
		etitles = new JCheckBoxMenuItem("Names");
		etitles.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowElementTitles(etitles.isSelected());
			}
		});
		elementDrawing.add(etitles);

		// element markings option
		emarkings = new JCheckBoxMenuItem("Markings");
		emarkings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowElementMarkers(emarkings.isSelected());
			}
		});
		elementDrawing.add(emarkings);

		// element intensities option
		eintensities = new JCheckBoxMenuItem("Heights");
		eintensities.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setShowElementIntensities(eintensities.isSelected());
			}
		});
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
					controller.setEscapeType(finalt);
				}
			});
			
			
			
		}
		
		/*
		final JRadioButtonMenuItem escNone, escSilicon, escGermanium;
		
		// element name option
		escNone 		= new JRadioButtonMenuItem("None");
		escSilicon 		= new JRadioButtonMenuItem("Silicon");
		escGermanium 	= new JRadioButtonMenuItem("Germanium");
		
		if (controller.getEscapeType() == EscapePeakType.NONE) escNone.setSelected(true);
		if (controller.getEscapeType() == EscapePeakType.SILICON) escSilicon.setSelected(true);
		if (controller.getEscapeType() == EscapePeakType.GERMANIUM) escGermanium.setSelected(true);
		
		escNone.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if (escNone.isSelected()) {
					controller.setEscapeType(EscapePeakType.NONE);
					escSilicon.setSelected(false);
					escGermanium.setSelected(false);
				}
			}
		});
		escSilicon.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if (escSilicon.isSelected()) {
					controller.setEscapeType(EscapePeakType.SILICON);
					escNone.setSelected(false);
					escGermanium.setSelected(false);
				}
			}
		});
		escGermanium.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if (escGermanium.isSelected()) {
					controller.setEscapeType(EscapePeakType.GERMANIUM);
					escSilicon.setSelected(false);
					escNone.setSelected(false);
				}
			}
		});
		
		escapePeaks.add(escNone);
		escapePeaks.add(escSilicon);
		escapePeaks.add(escGermanium);
		*/
		
		menu.add(escapePeaks);
		
		
		//Mapping Menu
		menu = new JMenu("Mapping");
		menu.setMnemonic(KeyEvent.VK_S);

		/*
		mapMenuItem = new JMenuItem("Map Visible Fittings");
		mapMenuItem.setMnemonic(KeyEvent.VK_M);
		mapMenuItem.addActionListener(selectMenuListener);
		mapMenuItem.setEnabled(false); // not until we have data and fittings
		menu.add(mapMenuItem);
		*/
		
		populateFittingMenu(menu);

		menuBar.add(menu);

		container.setJMenuBar(menuBar);

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{

				logPlot.setSelected(controller.getViewLog());
				axes.setSelected(controller.getShowAxes());
				title.setSelected(controller.getShowTitle());
				monochrome.setSelected(controller.getMonochrome());

				//etitles, emarkings, eintensities
				etitles.setSelected(controller.getShowElementTitles());
				emarkings.setSelected(controller.getShowElementMarkers());
				eintensities.setSelected(controller.getShowElementIntensities());

				switch (controller.getChannelCompositeType())
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

				raw.setSelected(controller.getShowRawData());
				fittings.setSelected(controller.getShowIndividualSelections());

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

		zoomPanel = createZoomPanel();
		bottomPanel.add(zoomPanel, BorderLayout.EAST);

		scanSelector = new ClearPanel();
		scanSelector.setLayout(new BoxLayout(scanSelector, BoxLayout.X_AXIS));

		scanNo = new JSpinner();
		scanNo.getEditor().setPreferredSize(new Dimension(50, 0));
		scanLabel = new JLabel("Scan");
		scanLabel.setBorder(Spacing.bSmall());
		scanBlock = new ComplexToggle(
			StockIcon.CHOOSE_CANCEL,
			"",
			"Flag this scan as bad to exclude it from the data set");

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
				controller.setScanNumber(value - 1);
			}
		});
		bottomPanel.add(scanSelector, BorderLayout.WEST);

		scanBlock.setFocusable(false);
		scanBlock.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				controller.setScanDiscarded(scanBlock.isSelected());
			}
		});

		return bottomPanel;

	}


	private JPanel createZoomPanel()
	{

		JPanel panel = new ClearPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		ImageButton out = new ImageButton(StockIcon.ZOOM_OUT, "Zoom Out", Layout.IMAGE);
		ImageButton in = new ImageButton(StockIcon.ZOOM_IN, "Zoom In", Layout.IMAGE);


		zoomSlider = new JSlider(10, 500);
		zoomSlider.setPaintLabels(false);
		zoomSlider.setPaintTicks(false);
		zoomSlider.setValue(100);
		Dimension prefSize = zoomSlider.getPreferredSize();
		prefSize.width /= 2;
		zoomSlider.setPreferredSize(prefSize);

		zoomSliderListener = new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setZoom(zoomSlider.getValue() / 100.0f);
			}
		};
		
		zoomSlider.addChangeListener(zoomSliderListener);
		out.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				//int current = zoomSlider.getValue();
				controller.setZoom(controller.getZoom()  - 0.1f);
				//zoomSlider.setValue(current - 10);
				//setSliderWithoutListener(zoomSlider, zoomSliderListener, current - 10);
			}
		});
		in.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				//int current = zoomSlider.getValue();
				controller.setZoom(controller.getZoom()  + 0.1f);
				//zoomSlider.setValue(current + 10);
			}
		});

		panel.add(out);
		panel.add(zoomSlider);
		panel.add(in);

		return panel;

	}


	// prompts the user with a file selection dialogue
	// reads the returned file list, loads the related
	// data set, and returns it to the caller
	public List<AbstractFile> openNewDataset(String[] exts, String desc)
	{
		return SwidgetIO.openFiles(container, "Select Data Files to Open", exts, desc, controller.getDataSourceFolder());
	}


	/*
	 * ===================================================================
	 * METHODS FOR HANDLING CANVAS EVENTS
	 * ===================================================================
	 */
	public void paintCanvasEvent(Graphics g)
	{

		controller.setImageWidth(canvas.getWidth());
		controller.setImageHeight(canvas.getHeight());

		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.fillRect(0, 0, (int) controller.getImageWidth(), (int) controller.getImageHeight());

		controller.draw(g);

	}


	private void fullRedraw()
	{
		container.validate();
		container.repaint();
	}


	public void mouseMoveCanvasEvent(int x)
	{

		int channel = controller.channelFromCoordinate(x);
		float energy = controller.getEnergyForChannel(channel);
		Pair<Float, Float> values = controller.getValueForChannel(channel);

		StringBuilder sb = new StringBuilder();
		String sep = ",  ";

		if (values != null)
		{

			DecimalFormat fmtObj = new DecimalFormat("#######0.00");
			
			sb.append("View Type: ");
			sb.append(controller.getChannelCompositeType().toString());
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
			
			
					
					/*"View Type: "
					+ controller.getChannelCompositeType().toString()
					+ sep
					+ "Channel: "
					+ String.valueOf(channel)
					+ sep
					+ "Energy: "
					+ fmtObj.format(energy)
					+ sep
					+ "Value: "
					+ fmtObj.format(values.first)
					+ ((values.first.equals(values.second)) ? "" : sep + "Unfiltered Value: "
							+ fmtObj.format(values.second)));*/

		}
		else
		{
			
			sb.append("View Type: ");
			sb.append(controller.getChannelCompositeType().toString());
			sb.append(sep);
			sb.append("Channel: ");
			sb.append("-");
			
			
			//channelLabel.setText("View Type: " + controller.getChannelCompositeType().toString() + sep + "Channel: " + "-");
		}
		
		channelLabel.setText(sb.toString());
		
	}










	// ////////////////////////////////////////////////////////
	// UI ACTIONS
	// ////////////////////////////////////////////////////////

	private void actionOpenData()
	{

		List<AbstractFile> files;

		String[] exts;
		if (Env.isWebStart())
		{
			exts = new String[] { "xml" };
		}
		else
		{
			exts = new String[] { "xml", "zip" };
		}

		files = openNewDataset(exts, "XRF Data Sets");

		loadFiles(files);

	}


	public void loadFiles(List<AbstractFile> files)
	{
		if (files != null)
		{

			PluralSet<Boolean> reading = controller.TASK_readFileListAsDataset(files);
			new PluralSetView(container, reading);

			// TaskListView was blocking.. it is now closed
			System.gc();

			// set some controls based on the fact that we have just loaded a
			// new data set
			savedSessionFileName = null;

		}
	}


	private void actionMap(FittingTransform type)
	{

		if (!controller.hasDataSet()) return;



		final PluralSet<MapResultSet> tasks = controller.TASK_getDataForMapFromSelectedRegions(type);
		if (tasks == null) return;

		new PluralSetView(container, tasks);


		if (tasks.getCompleted())
		{

			MapController mapController = controller.getMapController();
			PeakabooMapperSwing mapperWindow;

			Coord<Integer> dataDimensions = controller.getDataDimensions();

			MapResultSet results = tasks.getResult();


			AllMapsModel datamodel = new AllMapsModel(results);

			datamodel.dataDimensions = dataDimensions;
			datamodel.dimensionsProvided = controller.hasDimensions();
			datamodel.badPoints = controller.getDiscardedScanList();
			datamodel.realDimensions = controller.getRealDimensions();
			datamodel.realDimensionsUnits = controller.getRealDimensionsUnits();

			if (mapController == null)
			{

				mapperWindow = new PeakabooMapperSwing(
					container,
					datamodel,
					controller.getDatasetName(),
					controller.getDataSourceFolder(),
					savePictureFolder,
					dataDimensions,
					results,
					getController()
				);

			}
			else
			{

				int dy = mapController.getDataHeight();
				int dx = mapController.getDataWidth();

				// if the data does not match size, discard it. either it was from
				// a funny data set that isn't square or (more likely) they reloaded the
				// data set
				// TODO: invistigate -- is it easier to null the mapController pointer when
				// loading new data and assume the data is the same size otherwise?
				if (dataDimensions.x * dataDimensions.y == dx * dy)
				{
					dataDimensions.x = dx;
					dataDimensions.y = dy;
				}

				mapperWindow = new PeakabooMapperSwing(
					container,
					datamodel,
					controller.getDatasetName(),
					controller.getDataSourceFolder(),
					savePictureFolder,
					dataDimensions,
					results,
					getController()
				);
			}

			mapperWindow.showDialog();
			// controller.setMapController(mapperWindow.showDialog());
			System.gc();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void actionSavePicture()
	{
		if (savePictureFolder == null) savePictureFolder = controller.getDataSourceFolder();
		savePictureFolder = new SavePicture(container, controller, savePictureFolder).getStartingFolder();
	}


	private void actionSaveFittingInformation()
	{

		if (savePictureFolder == null) savePictureFolder = controller.getDataSourceFolder();

		List<TransitionSeries> tss = controller.getFittedTransitionSeries();
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
					intensity = controller.getTransitionSeriesIntensity(ts);
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
			// TODO Auto-generated catch block
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
					new String[] { "peakaboo" },
					"Peakaboo Session Data",
					savedSessionFileName);
			if (af != null) controller.loadPreferences(af.getInputStream(), false);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void actionShowInfo()
	{
		
		Map<String, String> properties = DataTypeFactory.<String, String>map();

		properties.put("Date of Creation:", controller.getScanCreationTime());
		properties.put("Created By:", controller.getScanCreator());
		
		properties.put("Project Name: ", controller.getScanProjectName());
		properties.put("Session Name: ", controller.getScanSessionName());
		properties.put("Experiment Name: ", controller.getScanExperimentName());
		properties.put("Sample Name: ", controller.getScanSampleName());
		properties.put("Scan Name: ", controller.getScanScanName());
		
		properties.put("Facility: ", controller.getScanFacilityName());
		properties.put("Laboratory: ", controller.getScanLaboratoryName());
		properties.put("Instrument: ", controller.getScanInstrumentName());
		properties.put("Technique: ", controller.getScanTechniqueName());
		
		new PropertyDialogue("Dataset Information", container, properties);

	}

}
