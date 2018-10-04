package peakaboo.ui.swing.plotting;



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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import org.apache.commons.io.output.ByteArrayOutputStream;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.Pair;
import cyclops.SISize;
import cyclops.SigDigits;
import cyclops.util.Mutable;
import cyclops.util.StringInput;
import cyclops.visualization.backend.awt.SavePicture;
import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.view.swing.SwingAutoPanel;
import net.sciencestudio.bolt.plugin.core.AlphaNumericComparitor;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import peakaboo.common.Env;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.controller.mapper.data.MapSetController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.plotter.data.DataLoader;
import peakaboo.controller.plotter.fitting.AutoEnergyCalibration;
import peakaboo.controller.settings.SavedSession;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.datasink.model.DataSink;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.plugin.DataSourceLookup;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import peakaboo.filter.model.FilterSet;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.swing.environment.DesktopApp;
import peakaboo.ui.swing.mapping.MapperFrame;
import peakaboo.ui.swing.plotting.datasource.DataSourceSelection;
import peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.plotting.statusbar.PlotStatusBar;
import peakaboo.ui.swing.plotting.toolbar.PlotToolbar;
import peakaboo.ui.swing.plugins.PluginsOverview;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutor.Event;
import plural.streams.StreamExecutorSet;
import plural.streams.swing.StreamExecutorPanel;
import plural.streams.swing.StreamExecutorView;
import plural.swing.ExecutorSetView;
import swidget.dialogues.AboutDialogue;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.icons.IconFactory;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ClearPanel;
import swidget.widgets.DraggingScrollPaneListener;
import swidget.widgets.DraggingScrollPaneListener.Buttons;
import swidget.widgets.HeaderBox;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.ButtonSize;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.layerpanel.ToastLayer;
import swidget.widgets.layerpanel.LayerDialog;
import swidget.widgets.layerpanel.LayerDialog.MessageType;
import swidget.widgets.properties.PropertyViewPanel;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedLayerPanel;



public class PlotPanel extends TabbedLayerPanel
{

	//Non-UI
	private PlotController				controller;
	private PlotCanvas					canvas;
	private File						saveFilesFolder;
	private File						savedSessionFileName;
	private File						exportedDataFileName;
	private File						datasetFolder;
	private String                      programTitle;





	//===TOOLBAR WIDGETS===
	private PlotToolbar                 toolBar;
	private PlotStatusBar				statusBar;
	private JScrollPane					scrolledCanvas;


	private static boolean newVersionNotified = false;
	
	public PlotPanel(TabbedInterface<TabbedLayerPanel> container) {
		super(container);
		
		this.programTitle = " - " + Version.title;
		
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
							
				String thisVersion = Version.longVersionNo;
				String otherVersion = "";
				boolean hasNewVersion = false;
				
				try {
					
					URL website = new URL("https://raw.githubusercontent.com/nsherry4/Peakaboo/master/version");
					URLConnection conn = website.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
			        while ((inputLine = in.readLine()) != null) {
			        	otherVersion += inputLine + "\n";
			        }
			        otherVersion = otherVersion.trim();
			        in.close();
			        
					AlphaNumericComparitor cmp = new AlphaNumericComparitor(false);
					hasNewVersion = cmp.compare(thisVersion, otherVersion) < 0;
					
				} catch (IOException e) {
					PeakabooLog.get().log(Level.WARNING, "Could not check for new version", e);
				}
				
				if (hasNewVersion) {
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
	
	public String getProgramTitle()
	{
		return programTitle;
	}
	
	public void setProgramTitle(String title)
	{
		programTitle = title;
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
			titleString.append(controller.data().getDataSet().getScanData().datasetName());
			titleString.append(programTitle);
		} else {
			titleString.append("No Data");
			titleString.append(programTitle);
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
		Mutable<ModalLayer> loadingLayer = new Mutable<>(null);
		
		DataLoader loader = new DataLoader(controller, files.stream().map(File::toPath).collect(Collectors.toList())) {

			@Override
			public void onLoading(ExecutorSet<DatasetReadResult> job) {
				ExecutorSetView execPanel = new ExecutorSetView(job); 
				loadingLayer.set(new ModalLayer(PlotPanel.this, execPanel));
				PlotPanel.this.pushLayer(loadingLayer.get());
			}
			
			@Override
			public void onSuccess(List<Path> paths) {
				// set some controls based on the fact that we have just loaded a
				// new data set
				controller.data().setDataPaths(paths);
				//TODO: Should this be cleared even when we load a session?
				savedSessionFileName = null;
				canvas.updateCanvasSize();
				removeLayer(loadingLayer.get());
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
				JPanel paramPanel = new JPanel(new BorderLayout());
				ModalLayer layer = new ModalLayer(PlotPanel.this, paramPanel);
				
				TitlePaintedPanel title = new TitlePaintedPanel("Additional Information Required", false);
				title.setBorder(Spacing.bMedium());
				
				
				SwingAutoPanel sap = new SwingAutoPanel(parameters);
				sap.setBorder(Spacing.bMedium());
				
				ButtonBox bbox = new ButtonBox();
				ImageButton ok = new ImageButton("OK", StockIcon.CHOOSE_OK);
				ok.addActionListener(e -> {
					PlotPanel.this.removeLayer(layer);
					finished.accept(true);
				});
				
				ImageButton cancel = new ImageButton("Cancel", StockIcon.CHOOSE_CANCEL);
				cancel.addActionListener(e -> {
					PlotPanel.this.removeLayer(layer);
					finished.accept(false);
				});
				
				bbox.addRight(0, cancel);
				bbox.addRight(0, ok);
				
				paramPanel.add(title, BorderLayout.NORTH);
				paramPanel.add(sap, BorderLayout.CENTER);
				paramPanel.add(bbox, BorderLayout.SOUTH);
				
				PlotPanel.this.pushLayer(layer);
			}




			@Override
			public void onSelection(List<DataSource> datasources, Consumer<DataSource> selected) {
				DataSourceSelection selection = new DataSourceSelection();
				selection.pickDSP(PlotPanel.this, datasources, selected);
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
		contents.linkAction = () -> DesktopApp.browser("https://github.com/nsherry4/Peakaboo");
		contents.linktext = "Website";
		contents.copyright = "2009-2018 by The University of Western Ontario and The Canadian Light Source Inc.";
		contents.licence = StringInput.contents(getClass().getResourceAsStream("/peakaboo/licence.txt"));
		contents.credits = StringInput.contents(getClass().getResourceAsStream("/peakaboo/credits.txt"));
		contents.logo = logo;
		contents.version = Version.versionNoMajor + "." + Version.versionNoMinor;
		contents.longVersion = Version.longVersionNo;
		contents.releaseDescription = Version.releaseDescription;
		contents.date = Version.buildDate;
		
		new AboutDialogue(getTabbedInterface().getWindow(), contents);
	}
	
	public void actionHelp()
	{
		DesktopApp.browser("https://github.com/nsherry4/Peakaboo/releases/download/v5.0.0/Peakaboo.5.Manual.pdf");
	}
	
	public void actionOpenData()
	{		
		
		List<SimpleFileExtension> exts = new ArrayList<>();
		BoltPluginSet<DataSourcePlugin> plugins = DataSourcePluginManager.SYSTEM.getPlugins();
		for (DataSourcePlugin p : plugins.newInstances()) {
			FileFormat f = p.getFileFormat();
			SimpleFileExtension ext = new SimpleFileExtension(f.getFormatName(), f.getFileExtensions());
			exts.add(ext);
		}
		
		//Add session file ext.
		SimpleFileExtension session = new SimpleFileExtension("Peakaboo Session Files", "peakaboo");
		exts.add(session);
		
		openNewDataset(exts);
		
		
	}
	


	
	
	public void loadExistingDataSource(DataSource ds, String settings) {
		
		DummyExecutor progress = new DummyExecutor(ds.getScanData().scanCount());
		progress.advanceState();
		ExecutorSet<Boolean> exec = new ExecutorSet<Boolean>("Loading Data Set") {

			@Override
			protected Boolean execute() {
				getController().data().setDataSource(ds, progress, this::isAborted);
				getController().loadSettings(settings, false);
				popLayer();
				return true;
			}}; 
			
		
		exec.addExecutor(progress, "Calculating Values");
			
		ExecutorSetView view = new ExecutorSetView(exec);
		pushLayer(new ModalLayer(this, view));
		exec.startWorking();
		
	}

	
	

	public void actionExportData(DataSink sink) {
		DataSource source = controller.data().getDataSet().getDataSource();

		SimpleFileExtension ext = new SimpleFileExtension(sink.getFormatName(), sink.getFormatExtension());
		SwidgetFilePanels.saveFile(this, "Export Scan Data", exportedDataFileName, ext, file -> {
			if (!file.isPresent()) {
				return;
			}
			try {
				sink.write(source, file.get().toPath());
			} catch (IOException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to export data", e);
			}
			
		});

	}

	public void actionMap()
	{

		if (!controller.data().hasDataSet()) return;


		StreamExecutor<MapResultSet> mapTask = controller.getMapTask();
		if (mapTask == null) return;

		StreamExecutorView taskView = new StreamExecutorView(mapTask);
		StreamExecutorPanel taskPanel = new StreamExecutorPanel("Generating Maps", taskView);
		
		mapTask.addListener(event -> {
			
			//if this is just a progress event, exit early
			if (event == Event.PROGRESS) { return; }
			
			//hide the task panel since this is either COMPLETED or ABORTED
			popLayer();
			
			//If this task was aborted instead of completed, exit early
			if (event == Event.ABORTED) { return; }
			
			//If there is no result, exit early
			if (!mapTask.getResult().isPresent()) { return; }
			
			
			MapperFrame mapperWindow;
			MapResultSet results = mapTask.getResult().get();
			MapSetController mapData = new MapSetController();
			

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
					controller.data().getDataSet().getScanData().datasetName(),
					controller.data().getDiscards().list(),
					dataDimensions,
					physicalDimensions,
					physicalUnit
				);
			
			
			mapperWindow = new MapperFrame(getTabbedInterface(), mapData, null, controller);

			mapperWindow.setVisible(true);

		});
		
		
		pushLayer(new ModalLayer(this, taskPanel));
		mapTask.start();


	}


	public void actionSaveSession()
	{

		SimpleFileExtension peakaboo = new SimpleFileExtension("Peakaboo Session File", "peakaboo");
		SwidgetFilePanels.saveFile(this, "Save Session Data", savedSessionFileName, peakaboo, file -> {
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


	public void actionSaveFilteredData()
	{
		if (saveFilesFolder == null) {
			saveFilesFolder = datasetFolder;
		}
		
		
		//Spectrum data = filters.filterDataUnsynchronized(new ISpectrum(datasetProvider.getScan(ordinal)), false);
		final FilterSet filters = controller.filtering().getActiveFilters();

		SimpleFileExtension text = new SimpleFileExtension("Text File", "txt");
		SwidgetFilePanels.saveFile(this, "Save Fitted Data to Text File", saveFilesFolder, text, saveFile -> {
			if (!saveFile.isPresent()) {
				return;
			}
			
			saveFilesFolder = saveFile.get().getParentFile();
			
			StreamExecutor<Throwable> streamexec = new StreamExecutor<>("Exporting Data");
			streamexec.setParallel(false);
			streamexec.setTask(controller.data().getDataSet().getScanData(), stream -> {
				
				try {
										
					Mutable<Boolean> errored = new Mutable<>(false);
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(saveFile.get()));
					stream.forEach(spectrum -> {
						spectrum = filters.applyFiltersUnsynchronized(spectrum, false);
						try {
							osw.write(spectrum.toString() + "\n");
						} catch (Exception e) { 
							if (!errored.get()) {
								PeakabooLog.get().log(Level.SEVERE, "Failed to save fitted data", e);
								streamexec.abort();
								errored.set(true);
							}
						}
					});

					osw.close();
										
					return null;
				} catch (Exception e) { 
					PeakabooLog.get().log(Level.SEVERE, "Failed to save fitted data", e);
				}
				
				return null;
			});
			
			StreamExecutorView view = new StreamExecutorView(streamexec);
			StreamExecutorPanel panel = new StreamExecutorPanel("Exporting Data", view);
			
			streamexec.addListener(event -> {
				//if not just a progress event, hide the modal panel
				if (event != Event.PROGRESS) {
					popLayer();
				}
				//remove the output file if the task was aborted
				if (event == Event.ABORTED) {
					saveFile.get().delete();
				}
			});
			
			pushLayer(new ModalLayer(this, panel));
			streamexec.start();
			
			
		});
		
	}
	
	public void actionSaveFittingInformation()
	{

		if (saveFilesFolder == null) {
			saveFilesFolder = datasetFolder;
		}

		List<TransitionSeries> tss = controller.fitting().getFittedTransitionSeries();
		

		
		SimpleFileExtension ext = new SimpleFileExtension("Text File", "txt");
		SwidgetFilePanels.saveFile(this, "Save Fitting Information to Text File", saveFilesFolder, ext, file -> {
			if (!file.isPresent()) {
				return;
			}
			try {
				// get an output stream to write the data to
				FileOutputStream os = new FileOutputStream(file.get());
				OutputStreamWriter osw = new OutputStreamWriter(os);
								
				// write out the data
				float intensity;
				for (TransitionSeries ts : tss)
				{

					if (ts.visible)
					{
						intensity = controller.fitting().getTransitionSeriesIntensity(ts);
						osw.write(ts.toString() + ", " + SigDigits.roundFloatTo(intensity, 2) + "\n");
					}
				}
				osw.close();
				os.close();
			}
			catch (IOException e)
			{
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
		
		
		
		JPanel panel = new JPanel(new BorderLayout());
		PropertyViewPanel propPanel = new PropertyViewPanel(properties);
		propPanel.setBorder(Spacing.bHuge());
		panel.add(propPanel, BorderLayout.CENTER);
		
		
		ImageButton close = HeaderBox.closeButton().withAction(this::popLayer);
		
		HeaderBox header = new HeaderBox(null, "Dataset Information", close);
		
		
		panel.add(header, BorderLayout.NORTH);
		
		this.pushLayer(new ModalLayer(this, panel));
		

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
				
		energyTask.last().addListener(event -> {
			//if event is not progress, then its either COMPLETED or ABORTED, so hide the panel
			if (event != Event.PROGRESS) {
				popLayer();
			}
			
			//if the last executor completed successfully, then set the calibration
			if (event == Event.COMPLETED) {
				EnergyCalibration energy = energyTask.last().getResult().orElse(null);
				if (energy != null) {
					controller.fitting().setMinEnergy(energy.getMinEnergy());
					controller.fitting().setMaxEnergy(energy.getMaxEnergy());
				}
			}
		});
		
		pushLayer(new ModalLayer(this, panel));
		energyTask.start();

		
	}
	
	public void actionShowPlugins() {
		pushLayer(new ModalLayer(this, new PluginsOverview(this)));
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
		this.pushLayer(new ModalLayer(this, advancedPanel));
	}

}
