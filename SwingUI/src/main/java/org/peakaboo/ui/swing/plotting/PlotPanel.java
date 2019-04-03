package org.peakaboo.ui.swing.plotting;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.Concentrations;
import org.peakaboo.common.Env;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.common.Version;
import org.peakaboo.controller.mapper.SavedMapSession;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.data.DataLoader;
import org.peakaboo.controller.plotter.fitting.AutoEnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.datasink.model.DataSink;
import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.datasource.plugin.DataSourcePlugin;
import org.peakaboo.datasource.plugin.DataSourcePluginManager;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginSet;
import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SISize;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.cyclops.util.StringInput;
import org.peakaboo.framework.cyclops.visualization.SurfaceType;
import org.peakaboo.framework.cyclops.visualization.backend.awt.SavePicture;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.executor.DummyExecutor;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.plural.streams.StreamExecutorSet;
import org.peakaboo.framework.plural.streams.StreamExecutor.Event;
import org.peakaboo.framework.plural.streams.swing.StreamExecutorPanel;
import org.peakaboo.framework.plural.streams.swing.StreamExecutorView;
import org.peakaboo.framework.plural.swing.ExecutorSetView;
import org.peakaboo.framework.plural.swing.ExecutorSetViewLayer;
import org.peakaboo.framework.swidget.dialogues.AboutDialogue;
import org.peakaboo.framework.swidget.dialogues.fileio.SimpleFileExtension;
import org.peakaboo.framework.swidget.dialogues.fileio.SwidgetFilePanels;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.DraggingScrollPaneListener;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.DraggingScrollPaneListener.Buttons;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.gradientpanel.TitlePaintedPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog;
import org.peakaboo.framework.swidget.widgets.layerpanel.ModalLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.ToastLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerDialog.MessageType;
import org.peakaboo.framework.swidget.widgets.layerpanel.widgets.AboutLayer;
import org.peakaboo.framework.swidget.widgets.layout.ButtonBox;
import org.peakaboo.framework.swidget.widgets.layout.HeaderPanel;
import org.peakaboo.framework.swidget.widgets.layout.PropertyPanel;
import org.peakaboo.framework.swidget.widgets.layout.TitledPanel;
import org.peakaboo.framework.swidget.widgets.tabbedinterface.TabbedInterface;
import org.peakaboo.framework.swidget.widgets.tabbedinterface.TabbedLayerPanel;
import org.peakaboo.mapping.Mapping;
import org.peakaboo.mapping.rawmap.RawMapSet;
import org.peakaboo.ui.swing.calibration.concentration.ConcentrationView;
import org.peakaboo.ui.swing.calibration.profileplot.ProfileManager;
import org.peakaboo.ui.swing.console.DebugConsole;
import org.peakaboo.ui.swing.environment.DesktopApp;
import org.peakaboo.ui.swing.mapping.MapperFrame;
import org.peakaboo.ui.swing.mapping.QuickMapPanel;
import org.peakaboo.ui.swing.plotting.datasource.DataSourceSelection;
import org.peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import org.peakaboo.ui.swing.plotting.statusbar.PlotStatusBar;
import org.peakaboo.ui.swing.plotting.toolbar.PlotToolbar;
import org.peakaboo.ui.swing.plugins.PluginsOverview;



public class PlotPanel extends TabbedLayerPanel
{

	//Non-UI
	private PlotController				controller;
	private PlotCanvas					canvas;
	public File							saveFilesFolder;
	private File						savedSessionFileName;
	private File						exportedDataFileName;
	private File						datasetFolder;


	//===TOOLBAR WIDGETS===
	private PlotToolbar                 toolBar;
	private PlotStatusBar				statusBar;
	private JScrollPane					scrolledCanvas;

	
	TabbedInterface<TabbedLayerPanel> 	tabs;

	private static boolean newVersionNotified = false;
	
	private Mutable<SavedMapSession> 	mapSession = new Mutable<>();
	
	public PlotPanel(TabbedInterface<TabbedLayerPanel> container) {
		super(container);
		this.tabs = container;
		
		savedSessionFileName = null;
		exportedDataFileName = null;
		
		datasetFolder = Env.homeDirectory();

		controller = new PlotController(DesktopApp.appDir());
				

		initGUI();

		controller.addListener(msg -> setWidgetsState());
		setWidgetsState();
		
		doVersionCheck();

	}
	
	private void doVersionCheck() {
		if (!newVersionNotified) {
			newVersionNotified = true;
			
			Thread versionCheck = new Thread(() -> {
				if (Version.hasNewVersion()) {
					SwingUtilities.invokeLater(() -> {
						this.pushLayer(new ToastLayer(this, "A new version of Peakaboo is available", () -> {
							DesktopApp.browser("https://github.com/nsherry4/Peakaboo/releases");
						}));	
					});
				}
				
			}); //thread
			versionCheck.setDaemon(true);
			versionCheck.start();
		}
	}
	
	public PlotController getController()
	{
		return controller;
	}


	private void setWidgetsState()
	{

		boolean hasData = controller.data().hasDataSet();
		
		setTitleBar();

		toolBar.setWidgetState(hasData);
		statusBar.setWidgetState(hasData);
		
		getTabbedInterface().validate();
		getTabbedInterface().repaint();
		
		setNeedsRedraw();

	}


	private void initGUI()
	{

		canvas = new PlotCanvas(controller, this);
		canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		canvas.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e){}


			public void mouseMoved(MouseEvent e)
			{
				mouseMoveCanvasEvent(e.getX());
			}

		});

		canvas.setRightClickCallback((channel, coords) -> {
			//if the click is in the bounds of the data/plot
			if (channel > -1 && channel < controller.data().getDataSet().getScanData().scanCount()) {
				CanvasPopupMenu menu = new CanvasPopupMenu(canvas, this, controller, channel);
				menu.show(canvas, coords.x, coords.y);
			}
		});


		
		Container pane = this.getContentLayer();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		pane.setLayout(layout);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		toolBar = new PlotToolbar(this, controller);
		pane.add(toolBar, c);

		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(1000, 100));
		
		scrolledCanvas = new JScrollPane(canvas);
		scrolledCanvas.setAutoscrolls(true);
		scrolledCanvas.setBorder(Spacing.bNone());
		
		
		scrolledCanvas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrolledCanvas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		new DraggingScrollPaneListener(scrolledCanvas.getViewport(), canvas, Buttons.LEFT, Buttons.MIDDLE);

		
		statusBar = new PlotStatusBar(controller);
		
		JPanel canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.add(scrolledCanvas, BorderLayout.CENTER);
		canvasPanel.add(statusBar, BorderLayout.SOUTH);
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
		tabs.add(new CurveFittingView(controller.fitting(), controller, this, canvas), 0);
		tabs.add(new FiltersetViewer(controller.filtering(), getTabbedInterface().getWindow()), 1);
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		
		
		Color dividerColour = UIManager.getColor("stratus-widget-border");
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		tabs.setBorder(new MatteBorder(0, 0, 0, 1, dividerColour));
		ClearPanel split = new ClearPanel(new BorderLayout());
		tabs.setPreferredSize(new Dimension(225, tabs.getPreferredSize().height));
		split.add(tabs, BorderLayout.WEST);
		split.add(canvasPanel, BorderLayout.CENTER);
				
		split.setBorder(Spacing.bNone());
		pane.add(split, c);


	}


	private void setTitleBar()
	{
		String title = getTabTitle();
		if (title.trim().length() == 0) title = "No Data";
		getTabbedInterface().setTabTitle(this, title);
	}


	@Override
	public String getTabTitle()
	{
		StringBuffer titleString;
		titleString = new StringBuffer();
		
		if (controller.data().hasDataSet())
		{
			titleString.append(controller.data().getTitle());
		} else {
			titleString.append("No Data");
		}

		
		return titleString.toString();
	}


	
	
	

	// prompts the user with a file selection dialogue
	// reads the returned file list, loads the related
	// data set, and returns it to the caller
	private void openNewDataset(List<SimpleFileExtension> extensions)
	{
		SwidgetFilePanels.openFiles(this, "Select Data Files to Open", datasetFolder, extensions, files -> {
			if (!files.isPresent()) return;
			datasetFolder = files.get().get(0).getParentFile();
			
			load(files.get());
			
		});
	}
	
	void load(List<File> files) {
		
		DataLoader loader = new DataLoader(controller, files.stream().map(File::toPath).collect(Collectors.toList())) {

			@Override
			public void onLoading(ExecutorSet<DatasetReadResult> job) {
				PlotPanel.this.pushLayer(new ExecutorSetViewLayer(PlotPanel.this, job));
			}
			
			@Override
			public void onSuccess(List<Path> paths) {
				// set some controls based on the fact that we have just loaded a
				// new data set
				controller.data().setDataPaths(paths);
				//TODO: Should this be cleared even when we load a session?
				savedSessionFileName = null;
				canvas.updateCanvasSize();
			}

			@Override
			public void onFail(List<Path> paths, String message) {
				new LayerDialog(
						"Open Failed", 
						message, 
						MessageType.ERROR
					).showIn(PlotPanel.this);
			}

			@Override
			public void onParameters(Group parameters, Consumer<Boolean> finished) {
				HeaderPanel paramPanel = new HeaderPanel();
				ModalLayer layer = new ModalLayer(PlotPanel.this, paramPanel);
				
				
				paramPanel.getHeader().setCentre("Options");
				paramPanel.getHeader().setShowClose(false);
				
				ImageButton ok = new ImageButton("OK").withStateDefault();
				ok.addActionListener(e -> {
					PlotPanel.this.removeLayer(layer);
					finished.accept(true);
				});
				ImageButton cancel = new ImageButton("Cancel");
				cancel.addActionListener(e -> {
					PlotPanel.this.removeLayer(layer);
					finished.accept(false);
				});
				
				paramPanel.getHeader().setLeft(cancel);
				paramPanel.getHeader().setRight(ok);
				
				
				SwingAutoPanel sap = new SwingAutoPanel(parameters);
				sap.setBorder(Spacing.bHuge());
				paramPanel.setBody(sap);
				
				
				PlotPanel.this.pushLayer(layer);
			}




			@Override
			public void onSelection(List<DataSourcePlugin> datasources, Consumer<DataSourcePlugin> selected) {
				DataSourceSelection selection = new DataSourceSelection(PlotPanel.this, datasources, selected);
				PlotPanel.this.pushLayer(selection);
			}



			@Override
			public void onSessionNewer() {
				ToastLayer warning = new ToastLayer(PlotPanel.this, "Session is from a newer version of Peakaboo.\nSome settings may not load correctly.");
				PlotPanel.this.pushLayer(warning);
			}
			
			@Override
			public void onSessionHasData(File sessionFile, Consumer<Boolean> load) {
				ImageButton buttonYes = new ImageButton("Yes")
						.withStateDefault()
						.withAction(() -> {
							savedSessionFileName = sessionFile;
							load.accept(true);
						});
				
				ImageButton buttonNo = new ImageButton("No")
						.withAction(() -> {
							load.accept(false);
						});
				
				new LayerDialog(
						"Open Associated Data Set?", 
						"This session is associated with another data set.\nDo you want to open that data set now?", 
						MessageType.QUESTION)
					.addRight(buttonYes)
					.addLeft(buttonNo)
					.showIn(PlotPanel.this);
				
				buttonYes.grabFocus();
			}

			@Override
			public void onSessionFailure() {
				new LayerDialog("Loading Session Failed", "The selected session file could not be read.\nIt may be corrupted, or from too old a version of Peakaboo.", MessageType.ERROR).showIn(PlotPanel.this);
			}
			
		};
		
		
		loader.load();
	}


	private void mouseMoveCanvasEvent(int x)
	{

		int channel = canvas.channelFromCoordinate(x);
		float energy = controller.view().getEnergyForChannel(channel);
		
		Pair<Float, Float> values;
		if (channel < 0 || channel >= controller.data().getDataSet().getAnalysis().channelsPerScan()) {
			//out of bounds
			values = null;
		} else {
			values = controller.view().getValueForChannel(channel);
		}

		StringBuilder sb = new StringBuilder();
		String sep = ",  ";

		if (values != null)
		{

			DecimalFormat fmtObj = new DecimalFormat("#######0.00");
			
			sb.append("View: ");
			sb.append(controller.view().getChannelCompositeMode().show());
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
			
			sb.append("View: ");
			sb.append(controller.view().getChannelCompositeMode().show());
			sb.append(sep);
			sb.append("Channel: ");
			sb.append("-");
			
		}
		
		statusBar.setChannelText(sb.toString());
		
	}

	public void setNeedsRedraw() {
		canvas.setNeedsRedraw();
	}








	// ////////////////////////////////////////////////////////
	// UI ACTIONS
	// ////////////////////////////////////////////////////////

	public void actionAbout()
	{
		ImageIcon logo = IconFactory.getImageIcon( Version.logo );
		logo = new ImageIcon(logo.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
		
		
		AboutDialogue.Contents contents = new AboutDialogue.Contents();
		contents.name = Version.program_name;
		contents.description = "XRF Analysis Software";
		contents.linkAction = () -> DesktopApp.browser("http://peakaboo.org");
		contents.linktext = "Website";
		contents.copyright = "2009-2018 by The University of Western Ontario and The Canadian Light Source Inc.";
		contents.licence = StringInput.contents(getClass().getResourceAsStream("/org/peakaboo/licence.txt"));
		contents.credits = StringInput.contents(getClass().getResourceAsStream("/org/peakaboo/credits.txt"));
		contents.logo = logo;
		contents.version = Version.versionNoMajor + "." + Version.versionNoMinor;
		contents.longVersion = Version.longVersionNo;
		contents.releaseDescription = Version.releaseDescription;
		contents.date = Version.buildDate;
		
		AboutLayer about = new AboutLayer(this, contents);
		this.pushLayer(about);
		
		//new AboutDialogue(getTabbedInterface().getWindow(), contents);
	}
	
	public void actionHelp()
	{
		DesktopApp.browser("https://github.com/nsherry4/Peakaboo/releases/download/v5.0.0/Peakaboo.5.Manual.pdf");
	}
	
	public void actionOpenData()
	{	
		List<SimpleFileExtension> exts = new ArrayList<>();
		for (DataSourcePlugin p : DataSourcePluginManager.SYSTEM.newInstances()) {
			FileFormat f = p.getFileFormat();
			SimpleFileExtension ext = new SimpleFileExtension(f.getFormatName(), f.getFileExtensions());
			exts.add(ext);
		}
		
		//Add session file ext.
		SimpleFileExtension session = new SimpleFileExtension("Peakaboo Session Files", "peakaboo");
		exts.add(session);
		
		openNewDataset(exts);
		
		
	}
	
	public void actionDebugConsole() {
		DebugConsole console = new DebugConsole(tabs);
		
		tabs.addTab(console);
		tabs.setTabTitle(console, "Debug Console");
	}


	
	
	public void actionLoadSubsetDataSource(SubsetDataSource sds, String settings) {
		
		Mutable<ModalLayer> layer = new Mutable<>();
		
		ExecutorSet<Boolean> loader = Plural.build("Loading Data Set", "Calculating Values", (execset, exec) -> {
			getController().data().setDataSource(sds, exec, execset::isAborted);
			getController().loadSettings(settings, false);
			removeLayer(layer.get());
			return true;
		});
		
		ExecutorSetView view = new ExecutorSetView(loader);
		layer.set(new ModalLayer(this, view));
		pushLayer(layer.get());
		loader.startWorking();
		
	}

	
	

	public void actionExportData(DataSink sink) {
		DataSource source = controller.data().getDataSet().getDataSource();

		SimpleFileExtension ext = new SimpleFileExtension(sink.getFormatName(), sink.getFormatExtension());
		SwidgetFilePanels.saveFile(this, "Export Scan Data", exportedDataFileName, ext, file -> {
			if (!file.isPresent()) {
				return;
			}
			actionExportData(source, sink, file.get());
			
		});

	}

	public void actionExportData(DataSource source, DataSink sink, File file) {

		ExecutorSet<Void> writer = DataSink.write(source, sink, file.toPath());
		ExecutorSetViewLayer layer = new ExecutorSetViewLayer(this, writer);
		pushLayer(layer);
		writer.startWorking();

	}
	
	public void actionMap()
	{

		if (!controller.data().hasDataSet()) return;


		StreamExecutor<RawMapSet> mapTask = controller.getMapTask();
		if (mapTask == null) return;

		StreamExecutorView taskView = new StreamExecutorView(mapTask);
		StreamExecutorPanel taskPanel = new StreamExecutorPanel("Generating Maps", taskView);
		ModalLayer layer = new ModalLayer(this, taskPanel);
		
		mapTask.addListener(event -> {
			
			//if this is just a progress event, exit early
			if (event == Event.PROGRESS) { return; }
			
			//hide the task panel since this is either COMPLETED or ABORTED
			removeLayer(layer);
			
			//If this task was aborted instead of completed, exit early
			if (event == Event.ABORTED) { return; }
			
			//If there is no result, exit early
			if (!mapTask.getResult().isPresent()) { return; }
			
			
			MapperFrame mapperWindow;
			RawMapSet results = mapTask.getResult().get();
			RawDataController mapData = new RawDataController();
			

			Coord<Integer> dataDimensions = null;
			Coord<Bounds<Number>> physicalDimensions = null;
			SISize physicalUnit = null;
			
			Optional<PhysicalSize> physical = controller.data().getDataSet().getPhysicalSize();
			if (physical.isPresent()) {
				physicalDimensions = physical.get().getPhysicalDimensions();
				physicalUnit = physical.get().getPhysicalUnit();
			}
			
			if (controller.data().getDataSet().hasGenuineDataSize()) {
				dataDimensions = controller.data().getDataSet().getDataSize().getDataDimensions();
			}
			
			mapData.setMapData(
					results,
					controller.data().getTitle(),
					controller.data().getDiscards().list(),
					dataDimensions,
					physicalDimensions,
					physicalUnit,
					controller.calibration().getCalibrationProfile()
				);
			
			
			mapperWindow = new MapperFrame(getTabbedInterface(), mapData, mapSession, controller);

			mapperWindow.setVisible(true);

		});
		
		
		pushLayer(layer);
		mapTask.start();


	}


	public void actionSaveSession()
	{

		SimpleFileExtension peakaboo = new SimpleFileExtension("Peakaboo Session File", "peakaboo");
		SwidgetFilePanels.saveFile(this, "Save Session", savedSessionFileName, peakaboo, file -> {
			if (!file.isPresent()) {
				return;
			}
			try {
				FileOutputStream os = new FileOutputStream(file.get());
				os.write(controller.getSavedSettings().serialize().getBytes());
				os.close();
				savedSessionFileName = file.get().getParentFile();
			}
			catch (IOException e)
			{
				PeakabooLog.get().log(Level.SEVERE, "Failed to save session", e);
			}
			
		});
	}


	public void actionSavePicture()
	{
		if (saveFilesFolder == null) {
			saveFilesFolder = datasetFolder;
		}
		SavePicture sp = new SavePicture(this, canvas, saveFilesFolder, file -> {
			if (file.isPresent()) {
				saveFilesFolder = file.get().getParentFile();
			}
		});
		sp.show();
		 
	}
	
	public void actionExportArchive() {
		Mutable<ExportPanel> export = new Mutable<>(null);
		
		export.set(new ExportPanel(this, canvas, () -> {
			
			SwidgetFilePanels.saveFile(this, "Save Archive", saveFilesFolder, new SimpleFileExtension("Zip Archive", "zip"), file -> {
				if (!file.isPresent()) {
					return;
				}
				
				SurfaceType format = export.get().getPlotFormat();
				int width = export.get().getImageWidth();
				int height = export.get().getImageHeight();
				
				actionExportArchiveToZip(file.get(), format, width, height);
				
				
			});
		}));
	}
	
	private void actionExportArchiveToZip(File file, SurfaceType format, int width, int height) {
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
			
			
			//Save Plot
			String ext = "";
			switch (format) {
			case PDF: 
				ext = "pdf";
				break;
			case RASTER:
				ext = "png";
				break;
			case VECTOR:
				ext = "svg";
				break;			
			}
			
			ZipEntry e = new ZipEntry("plot." + ext);
			zos.putNextEntry(e);

			switch (format) {
			case PDF:
				canvas.writePDF(zos, new Coord<Integer>(width, height));
				break;
			case RASTER:
				canvas.writePNG(zos, new Coord<Integer>(width, height));
				break;
			case VECTOR:
				canvas.writeSVG(zos, new Coord<Integer>(width, height));	
				break;					
			}
			zos.closeEntry();
			
			
			//save fittings as text
			e = new ZipEntry("fittings.txt");
			zos.putNextEntry(e);
			controller.writeFittingInformation(zos);
			zos.closeEntry();
			
			if (controller.calibration().hasCalibrationProfile()) {
				e = new ZipEntry("z-calibration-profile.pbcp");
				zos.putNextEntry(e);
				String profileYaml = CalibrationProfile.save(controller.calibration().getCalibrationProfile());
				zos.write(profileYaml.getBytes());
				zos.closeEntry();
			}
			
			
			e = new ZipEntry("session.peakaboo");
			zos.putNextEntry(e);
			zos.write(controller.getSavedSettings().serialize().getBytes());
			zos.closeEntry();
			
			
			zos.close();
			
			
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Could not save archive", e);
		}
	}


	public void actionSaveFilteredData()
	{
		if (saveFilesFolder == null) {
			saveFilesFolder = datasetFolder;
		}
		
		
		//Spectrum data = filters.filterDataUnsynchronized(new ISpectrum(datasetProvider.getScan(ordinal)), false);
		

		SimpleFileExtension text = new SimpleFileExtension("Text File", "txt");
		SwidgetFilePanels.saveFile(this, "Save Fitted Data to Text File", saveFilesFolder, text, saveFile -> {
			if (!saveFile.isPresent()) {
				return;
			}
			
			saveFilesFolder = saveFile.get().getParentFile();
			
			ExecutorSet<Object> execset = controller.writeFitleredDataToText(saveFile.get());
			
			ExecutorSetViewLayer layer = new ExecutorSetViewLayer(this, execset);
			
			execset.addListener(() -> {
				if (execset.isAborted()) {
					saveFile.get().delete();
				}
			});
			
			pushLayer(layer);
			execset.startWorking();
			
			
		});
		
	}
	
	
	public void actionSaveFittingInformation()
	{

		if (saveFilesFolder == null) {
			saveFilesFolder = datasetFolder;
		}

		SimpleFileExtension ext = new SimpleFileExtension("Text File", "txt");
		SwidgetFilePanels.saveFile(this, "Save Fitting Information to Text File", saveFilesFolder, ext, file -> {
			if (!file.isPresent()) {
				return;
			}
			
			try {
				FileOutputStream os = new FileOutputStream(file.get());
				controller.writeFittingInformation(os);
				os.close();
			} catch (IOException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to save fitting information", e);
			}
			
		});

	}
		
	public void actionLoadSession() {

		SimpleFileExtension peakaboo = new SimpleFileExtension("Peakaboo Session File", "peakaboo");
		SwidgetFilePanels.openFile(this, "Load Session Data", savedSessionFileName, peakaboo, file -> {
			if (!file.isPresent()) {
				return;
			}
			load(Collections.singletonList(file.get()));
		});

	}

	
	public void actionShowInfo()
	{
		
		Map<String, String> properties;
		
		properties = new LinkedHashMap<String, String>();
		properties.put("Data Format", "" + controller.data().getDataSet().getDataSource().getFileFormat().getFormatName());
		properties.put("Dataset Title", "" + controller.data().getTitle());
		properties.put("Scan Count", "" + controller.data().getDataSet().getScanData().scanCount());
		properties.put("Channels per Scan", "" + controller.data().getDataSet().getAnalysis().channelsPerScan());
		properties.put("Maximum Intensity", "" + controller.data().getDataSet().getAnalysis().maximumIntensity());

		//Extended attributes
		if (controller.data().getDataSet().getMetadata().isPresent()) {
			Metadata metadata = controller.data().getDataSet().getMetadata().get();
			
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
			
		}
		
		TitledPanel propPanel = new TitledPanel(new PropertyPanel(properties));
		propPanel.setBorder(Spacing.bHuge());

		HeaderLayer layer = new HeaderLayer(this, true);
		layer.setBody(propPanel);
		layer.getHeader().setCentre("Dataset Information");
		this.pushLayer(layer);
		
	}
	
	public void actionGuessMaxEnergy() {
		
		if (controller == null) return;
		if (controller.fitting().getVisibleTransitionSeries().size() < 2) {
			new LayerDialog(
					"Cannot Detect Energy Calibration", 
					"Detecting energy calibration requires that at least two elements be fitted.\nTry using 'Elemental Lookup', as 'Guided Fitting' will not work without energy calibration set.", 
					MessageType.WARNING
				).showIn(this);
			return;
		}
		
		
		StreamExecutorSet<EnergyCalibration> energyTask = AutoEnergyCalibration.propose(
				controller.data().getDataSet().getAnalysis().averagePlot(), 
				controller.fitting().getVisibleTransitionSeries(), 
				controller.fitting(),
				controller.data().getDataSet().getAnalysis().channelsPerScan());
		
		
		List<StreamExecutorView> views = energyTask.getExecutors().stream().map(StreamExecutorView::new).collect(Collectors.toList());
		StreamExecutorPanel panel = new StreamExecutorPanel("Detecting Energy Level", views);
		ModalLayer layer = new ModalLayer(this, panel);
		
		energyTask.last().addListener(event -> {
			//if event is not progress, then its either COMPLETED or ABORTED, so hide the panel
			if (event != Event.PROGRESS) {
				removeLayer(layer);
			}
			
			//if the last executor completed successfully, then set the calibration
			if (event == Event.COMPLETED) {
				EnergyCalibration energy = energyTask.last().getResult().orElse(null);
				if (energy != null) {
					controller.fitting().setMinMaxEnergy(energy.getMinEnergy(), energy.getMaxEnergy());
				}
			}
		});
		
		pushLayer(layer);
		energyTask.start();

		
	}
	
	public void actionShowPlugins() {
		pushLayer(new PluginsOverview(this));
	}


	
	
	public void actionShowLogs() {
		File appDataDir = DesktopApp.appDir("Logging");
		appDataDir.mkdirs();
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(appDataDir);
		} catch (IOException e1) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to open logging folder", e1);
		}
	}
	
	public void actionReportBug() {
		DesktopApp.browser("https://github.com/nsherry4/Peakaboo/issues/new/choose");
	}

	public void actionShowAdvancedOptions() {
		AdvancedOptionsPanel advancedPanel = new AdvancedOptionsPanel(this, controller);
		this.pushLayer(advancedPanel);
	}

	public void actionAddAnnotation(ITransitionSeries selected) {
		if (!controller.fitting().getFittingSelections().getFittedTransitionSeries().contains(selected)) {
			return;
		}
		
		Mutable<LayerDialog> dialogbox = new Mutable<>();
		
		JTextField textfield = new JTextField(20);
		textfield.addActionListener(ae -> {
			controller.fitting().setAnnotation(selected, textfield.getText());
			dialogbox.get().hide();
		});
		textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					dialogbox.get().hide();
				}
			}
		});
		textfield.setText(controller.fitting().getAnnotation(selected));
		LayerDialog dialog = new LayerDialog("Annotation for " + selected.toString(), textfield, MessageType.QUESTION);
		dialogbox.set(dialog);
		dialog.addLeft(new ImageButton("Cancel").withAction(() -> {
			dialog.hide();
		}));
		dialog.addRight(new ImageButton("OK").withStateDefault().withAction(() -> {
			controller.fitting().setAnnotation(selected, textfield.getText());
			dialog.hide();
		}));
		dialog.showIn(this);
		textfield.grabFocus();
	}

	public void actionShowConcentrations() {
		CalibrationProfile p = controller.calibration().getCalibrationProfile();
		List<ITransitionSeries> tss = controller.fitting().getVisibleTransitionSeries();
		Concentrations ppm = Concentrations.calculate(tss, p, ts -> {
			FittingResult result = controller.fitting().getFittingResultForTransitionSeries(ts);
			float intensity = 0;
			if (result == null) { return 0f; }
			intensity = p.calibrate(result.getFitSum(), ts);
			if (Float.isNaN(intensity)) {
				return 0f;
			}
			return intensity;
		});
		
		
		ConcentrationView concentrations = new ConcentrationView(ppm, this);
		this.pushLayer(concentrations);

	}

	public void actionShowCalibrationProfileManager() {
		this.pushLayer(new ProfileManager(this, controller));
	}

	public void actionQuickMap(int channel) {
		ExecutorSet<RawMapSet> execset = Mapping.quickMapTask(controller.data(), channel);
		ExecutorSetViewLayer layer = new ExecutorSetViewLayer(this, execset);

		Mutable<Boolean> done = new Mutable<>(false);
		execset.addListener(() -> {
			if (execset.getCompleted() && execset.getResult() != null) {
				if (!done.get()) {
					done.set(true);
					QuickMapPanel maplayer = new QuickMapPanel(this, this.tabs, channel, execset.getResult(), mapSession, controller);
					this.pushLayer(maplayer);
				}
			}
		});
		
		pushLayer(layer);
		execset.startWorking();
		

	}

	@Override
	public void titleDoubleClicked() {
		if (!controller.data().hasDataSet()) {
			return;
		}
		
		Mutable<LayerDialog> dialogbox = new Mutable<>();
		
		JTextField textfield = new JTextField(20);
		textfield.addActionListener(ae -> {
			controller.data().setTitle(textfield.getText());
			dialogbox.get().hide();
		});
		textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					dialogbox.get().hide();
				}
			}
		});
		textfield.setText(controller.data().getTitle());
		LayerDialog dialog = new LayerDialog("Change Dataset Title", textfield, MessageType.QUESTION);
		dialogbox.set(dialog);
		dialog.addLeft(new ImageButton("Cancel").withAction(() -> {
			dialog.hide();
		}));
		dialog.addRight(new ImageButton("OK").withStateDefault().withAction(() -> {
			controller.data().setTitle(textfield.getText());
			dialog.hide();
		}));
		dialog.showIn(this);
		textfield.grabFocus();
		
	}

	public boolean hasUnsavedWork() {
		return false;
	}
	

}
