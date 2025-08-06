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
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import org.apache.commons.io.IOUtils;
import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Version;
import org.peakaboo.controller.mapper.SavedMapSession;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.ViewController;
import org.peakaboo.controller.plotter.data.DataLoader;
import org.peakaboo.controller.plotter.fitting.AutoEnergyCalibration;
import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.io.PathDataInputAdapter;
import org.peakaboo.dataset.io.PathDataOutputAdapter;
import org.peakaboo.dataset.sink.model.DataSink;
import org.peakaboo.dataset.sink.model.DataSink.DataSinkContext;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.components.fileformat.FileFormat;
import org.peakaboo.dataset.source.model.components.metadata.Metadata;
import org.peakaboo.dataset.source.model.internal.SubsetDataSource;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Mutable;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.backend.awt.SavePicture;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.executor.PluralExecutor;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorLayer;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorView;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.plural.streams.StreamExecutorSet;
import org.peakaboo.framework.plural.swing.ExecutorSetView;
import org.peakaboo.framework.plural.swing.ExecutorSetViewLayer;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.DraggingScrollPaneListener;
import org.peakaboo.framework.stratus.api.hookins.DraggingScrollPaneListener.Buttons;
import org.peakaboo.framework.stratus.api.hookins.FileDrop;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.dialogs.fileio.SimpleFileExtension;
import org.peakaboo.framework.stratus.components.dialogs.fileio.StratusFilePanels;
import org.peakaboo.framework.stratus.components.panels.BlankMessagePanel;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.panels.PropertyPanel;
import org.peakaboo.framework.stratus.components.panels.TitledPanel;
import org.peakaboo.framework.stratus.components.ui.colour.ColourChooser;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.layers.AboutLayer;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;
import org.peakaboo.framework.stratus.components.ui.layers.ToastLayer;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedLayerPanel;
import org.peakaboo.framework.stratus.laf.painters.scrollbar.ScrollBarTrackPainter;
import org.peakaboo.mapping.Mapping;
import org.peakaboo.mapping.rawmap.RawMapSet;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.DesktopApp;
import org.peakaboo.ui.swing.app.DesktopSettings;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.app.widgets.PeakabooTabTitle;
import org.peakaboo.ui.swing.mapping.MapperFrame;
import org.peakaboo.ui.swing.mapping.QuickMapPanel;
import org.peakaboo.ui.swing.options.AdvancedOptionsPanel;
import org.peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import org.peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import org.peakaboo.ui.swing.plotting.guides.FirstRun;
import org.peakaboo.ui.swing.plotting.statusbar.PlotStatusBar;
import org.peakaboo.ui.swing.plotting.toolbar.PlotToolbar;
import org.peakaboo.ui.swing.plugins.PluginPanel;

public class PlotPanel extends TabbedLayerPanel implements AutoCloseable {

	// Non-UI
	private PlotController controller;

	private PlotCanvas canvas;

	// ===TOOLBAR WIDGETS===
	private PlotToolbar toolBar;
	private PlotStatusBar statusBar;

	TabbedInterface<TabbedLayerPanel> tabs;

	private static boolean newVersionNotified = false;

	private Mutable<SavedMapSession> mapSession = new Mutable<>();

	public PlotPanel(TabbedInterface<TabbedLayerPanel> container) {
		super(container);
		this.tabs = container;

		controller = new PlotController(DesktopApp.appDir());
		controller.view().setDarkMode(DesktopSettings.isDarkMode());

		initGUI();

		controller.addListener(msg -> setWidgetsState());
		controller.notifications().addListener(notice -> notifyUser(notice.message(), notice.action()));
		setWidgetsState();

		doVersionCheck();

		doFirstRun();

	}

	private void doVersionCheck() {
		if (!newVersionNotified) {
			newVersionNotified = true;

			Thread versionCheck = new Thread(() -> {

				if (Version.hasNewVersion()) {
					notifyUser("A new version of Peakaboo is available",
							() -> DesktopApp.browser("https://github.com/nsherry4/Peakaboo/releases"));
				}

			}); // thread
			versionCheck.setDaemon(true);
			versionCheck.start();
		}
	}

	private void notifyUser(String message, Runnable action) {
		Runnable onclick = action == null ? () -> {
		} : action;
		SwingUtilities.invokeLater(() -> this.pushLayer(new ToastLayer(this, message, onclick)));
	}

	private void doFirstRun() {
		if (DesktopSettings.isFirstrun()) {
			DesktopSettings.setFirstrun(false);
			FirstRun fr = new FirstRun(this);
			this.pushLayer(fr);
		}
	}

	public PlotController getController() {
		return controller;
	}

	// This is public to allow the plugins UI to advice of updates.
	public void setWidgetsState() {

		boolean hasData = controller.data().hasDataSet();

		setTitleBar();

		toolBar.setWidgetState(hasData);
		statusBar.setWidgetState(hasData);

		getTabbedInterface().validate();
		getTabbedInterface().repaint();

		setNeedsRedraw();

	}

	private void initGUI() {

		canvas = new PlotCanvas(controller, this);
		canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		canvas.setMouseMoveCallback((channel, coords) -> mouseMoveCanvasEvent(coords.x));

		canvas.setRightClickCallback((channel, coords) -> {
			// if the click is in the bounds of the data/plot
			if (channel > -1 && channel < controller.data().getDataSet().getAnalysis().channelsPerScan()) {
				CanvasPopupMenu menu = new CanvasPopupMenu(this, controller, channel);
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

		JScrollPane scrolledCanvas = new JScrollPane(canvas);
		scrolledCanvas.setAutoscrolls(true);
		scrolledCanvas.setBorder(Spacing.bNone());

		// Set scrollbar track color to match plot background
		Color plotBg = canvas.getPlotBackgroundColor();
		scrolledCanvas.getHorizontalScrollBar().putClientProperty(ScrollBarTrackPainter.KEY_BACKGROUND, plotBg);

		scrolledCanvas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrolledCanvas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		new DraggingScrollPaneListener(scrolledCanvas.getViewport(), canvas, Buttons.LEFT, Buttons.MIDDLE);

		// Disable default mouse wheel scrolling on the JScrollPane to prevent conflicts
		scrolledCanvas.setWheelScrollingEnabled(false);

		// Add mouse wheel listener for Ctrl+Scroll zoom and regular scrolling
		scrolledCanvas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown()) {
					// Handle zoom - consume event to prevent default scrolling
					onMouseWheelMoved(e);
				} else {
					// Handle regular scrolling manually to ensure proper behavior
					handleRegularScroll(e);
				}
			}
		});

		statusBar = new PlotStatusBar(controller, this);

		JPanel canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.add(scrolledCanvas, BorderLayout.CENTER);
		canvasPanel.add(statusBar, BorderLayout.SOUTH);
		canvasPanel.setPreferredSize(new Dimension(600, 300));

		canvasPanel.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				canvas.updateCanvasSize();
			}

		});

		ImageIcon peakabooLogo;
		if (DesktopSettings.isDarkMode()) {
			peakabooLogo = IconFactory.getImageIcon(PeakabooIcons.ASSET_PATH, "icon-symbolic",
					new Color((0x00ffffff & Stratus.getTheme().getControlText().getRGB()) | 0x20000000, true));
		} else {
			peakabooLogo = IconFactory.getImageIcon(PeakabooIcons.ASSET_PATH, "icon-symbolic");
		}

		BlankMessagePanel blankCanvas = new BlankMessagePanel("No Data",
				"You can open a dataset by dragging it here or by clicking the 'Open' button in the toolbar.",
				peakabooLogo);
		new FileDrop(blankCanvas, canvas.getFileDropListener());

		JTabbedPane sidebarTabs = new JTabbedPane();
		sidebarTabs.add(new CurveFittingView(controller.fitting(), controller, this, canvas), 0);
		sidebarTabs.add(new FiltersetViewer(controller.filtering(), getTabbedInterface().getWindow()), 1);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;

		sidebarTabs.setBorder(new MatteBorder(0, 0, 0, 1, Stratus.getTheme().getWidgetBorder()));
		ClearPanel split = new ClearPanel(new BorderLayout());
		sidebarTabs.setPreferredSize(new Dimension(250, sidebarTabs.getPreferredSize().height));
		split.add(blankCanvas, BorderLayout.CENTER);

		split.setBorder(Spacing.bNone());
		pane.add(split, c);

		controller.addListener(e -> {
			if (controller.data().hasDataSet() && Arrays.asList(split.getComponents()).contains(blankCanvas)) {
				split.remove(blankCanvas);
				split.add(canvasPanel, BorderLayout.CENTER);
				split.add(sidebarTabs, BorderLayout.WEST);
			}
		});

	}

	private void setTitleBar() {
		String title = getTabTitle();
		if (title.trim().length() == 0)
			title = "No Data";
		getTabbedInterface().setTabTitle(this, title);
	}

	@Override
	public String getTabTitle() {
		StringBuilder titleString = new StringBuilder();

		if (controller.data().hasDataSet()) {
			titleString.append(controller.data().getTitle());
		} else {
			titleString.append("No Data");
		}

		return titleString.toString();
	}

	void load(List<DataInputAdapter> files) {
		DataLoader loader = new PlotDataLoader(this, controller);
		loader.loadFiles(files);
	}

	private void mouseMoveCanvasEvent(int x) {

		int channel = canvas.channelFromCoordinate(x);
		float energy = controller.view().getEnergyForChannel(channel);

		Pair<Float, Float> values;
		if (channel < 0 || channel >= controller.data().getDataSet().getAnalysis().channelsPerScan()) {
			// out of bounds
			values = null;
		} else {
			values = controller.view().getValueForChannel(channel);
		}

		if (values != null) {
			statusBar.setData(controller.view().getChannelViewMode(), channel, energy, values.first, values.second);
		} else {
			statusBar.setData(controller.view().getChannelViewMode());
		}

	}

	public void setNeedsRedraw() {
		canvas.setNeedsRedraw();
	}

	private void onMouseWheelMoved(MouseWheelEvent e) {
		if (!controller.data().hasDataSet()) {
			return;
		}

		// Consume the event immediately to prevent default scrolling behavior
		e.consume();

		// Get current zoom level and calculate new zoom
		float currentZoom = controller.view().getZoom();
		int wheelRotation = e.getWheelRotation();

		// Adaptive zoom factor: larger changes when zoomed out, smaller when zoomed in
		// Uses inverse scaling across full zoom range (0.1 to 10.0)
		float baseZoomFactor = 1.025f;
		// Scale from ~4.0 at zoom=0.1 down to 1 at zoom=10.0
		float zoomScale = 4.0f / (1.0f + currentZoom * 0.3f);
		float adaptiveFactor = 1.0f + (baseZoomFactor - 1.0f) * zoomScale;

		float newZoom;
		if (wheelRotation < 0) {
			// Scroll up = zoom in
			newZoom = currentZoom * adaptiveFactor;
		} else {
			// Scroll down = zoom out
			newZoom = currentZoom / adaptiveFactor;
		}

		// Clamp zoom to valid range using ViewController constants
		newZoom = Math.max(ViewController.ZOOM_MIN, Math.min(ViewController.ZOOM_MAX, newZoom));

		// Only proceed if zoom actually changed
		if (Math.abs(newZoom - currentZoom) < 0.001f) {
			return; // No change needed
		}

		// Get current state before zoom
		Rectangle visibleRect = canvas.getVisibleRect();
		Dimension currentSize = canvas.getPreferredSize();
		int mouseViewportX = e.getX();

		// Calculate what fraction of the total canvas width the mouse is pointing to
		double mouseFraction = (double)(visibleRect.x + mouseViewportX) / currentSize.width;

		// Apply the new zoom
		controller.view().setZoom(newZoom);

		// Force canvas to update its size immediately
		canvas.updateCanvasSize();
		canvas.revalidate();

		// Get the new canvas size after zoom
		Dimension newSize = canvas.getPreferredSize();

		// Calculate where that same data point (fraction) should be in the new canvas
		int newMouseCanvasX = (int)(mouseFraction * newSize.width);

		// Calculate new scroll position to keep the mouse pointing to the same data point
		int newScrollX = newMouseCanvasX - mouseViewportX;

		// Ensure we don't scroll beyond bounds
		int maxScrollX = Math.max(0, newSize.width - visibleRect.width);
		newScrollX = Math.max(0, Math.min(newScrollX, maxScrollX));

		// Update scroll position
		Rectangle targetRect = new Rectangle(newScrollX, visibleRect.y,
				visibleRect.width, visibleRect.height);
		canvas.scrollRectToVisible(targetRect);
	}

	private void handleRegularScroll(MouseWheelEvent e) {
		// Manually handle regular mouse wheel scrolling to avoid conflicts with zoom
		e.consume(); // Consume to prevent default handler

		if (!controller.data().hasDataSet()) {
			return;
		}

		// Get current scroll position
		Rectangle visibleRect = canvas.getVisibleRect();
		int scrollAmount = e.getWheelRotation() * canvas.getScrollableUnitIncrement(visibleRect, SwingConstants.HORIZONTAL, e.getWheelRotation());

		// Calculate new scroll position
		int newScrollX = visibleRect.x + scrollAmount;

		// Ensure we don't scroll beyond bounds
		Dimension canvasSize = canvas.getPreferredSize();
		int maxScrollX = Math.max(0, canvasSize.width - visibleRect.width);
		newScrollX = Math.max(0, Math.min(newScrollX, maxScrollX));

		// Update scroll position
		Rectangle targetRect = new Rectangle(newScrollX, visibleRect.y, visibleRect.width, visibleRect.height);
		canvas.scrollRectToVisible(targetRect);
	}

	// ////////////////////////////////////////////////////////
	// UI ACTIONS
	// ////////////////////////////////////////////////////////

	public void actionAbout() {
		ImageIcon logo = IconFactory.getImageIcon(Tier.provider().iconPath(), Version.LOGO);
		logo = new ImageIcon(logo.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));

		AboutLayer.Contents contents = new AboutLayer.Contents();
		contents.name = Tier.provider().appName();
		contents.description = "XRF Analysis Software";
		contents.linkAction = () -> DesktopApp.browser("http://peakaboo.org");
		contents.linktext = "Website";
		contents.copyright = "2009-2024 by The University of Western Ontario and The Canadian Light Source Inc.";

		try {
			contents.licence = IOUtils.toString( getClass().getResourceAsStream("/org/peakaboo/licence.txt"), StandardCharsets.UTF_8 );
			contents.credits = IOUtils.toString( getClass().getResourceAsStream("/org/peakaboo/credits.txt"), StandardCharsets.UTF_8 );
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Failed to load asset from classloader", e);
		}

		contents.logo = logo;
		contents.version = Version.VERSION_MAJOR + "." + Version.VERSION_MINOR;
		contents.longVersion = Version.LONG_VERSION;
		contents.releaseDescription = Version.RELEASE_DESCRIPTION;
		contents.date = Version.buildDate;
		contents.titleStyle = "font-family: Springsteel-Light; font-size: 350%;";

		AboutLayer about = new AboutLayer(this, contents);
		this.pushLayer(about);

	}

	public void actionHelp() {
		DesktopApp.browser("https://github.com/nsherry4/Peakaboo/wiki");
	}

	public void actionOpenData() {
		List<SimpleFileExtension> exts = new ArrayList<>();
		for (DataSourcePlugin p : DataSourceRegistry.system().newInstances()) {
			FileFormat f = p.getFileFormat();
			SimpleFileExtension ext = new SimpleFileExtension(f.getFormatName(), f.getFileExtensions());
			exts.add(ext);
		}

		// Add session file ext.
		exts.add(new SessionFileExtension());

		StratusFilePanels.openFiles(this, "Select Data Files to Open", controller.io().getLastFolder(), exts, files -> {
			if (!files.isPresent())
				return;
			controller.io().setLastFolder(files.get().get(0).getParentFile());

			List<File> filelist = files.get();
			load(filelist.stream().map(PathDataInputAdapter::new).collect(Collectors.toList()));

		});

	}

	public void actionLoadSubsetDataSource(SubsetDataSource sds, SavedSession settings) {

		Mutable<ModalLayer> layer = new Mutable<>();

		ExecutorSet<Boolean> loader = PluralExecutor.build("Loading Data Set", "Calculating Values", (execset, exec) -> {
			getController().data().setDataSource(sds, exec, execset::isAborted);
			getController().load(settings, false);
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
		StratusFilePanels.saveFile(this, "Export Scan Data", controller.io().getLastFolder(), ext, file -> {
			if (!file.isPresent()) {
				return;
			}
			controller.io().setLastFolder(file.get().getParentFile());
			actionExportData(source, sink, file.get());

		});

	}

	public void actionExportData(DataSource source, DataSink sink, File file) {
		var output = new PathDataOutputAdapter(file.toPath());
		ExecutorSet<Void> writer = DataSink.write(sink, new DataSinkContext(source, output));
		output.close();
		ExecutorSetViewLayer layer = new ExecutorSetViewLayer(this, writer);
		pushLayer(layer);
		writer.startWorking();
	}

	public void actionMap() {

		if (!controller.data().hasDataSet())
			return;

		StreamExecutor<RawMapSet> mapTask = controller.getMapTask();
		if (mapTask == null)
			return;

		TaskMonitorView taskView = new TaskMonitorView(mapTask);
		TaskMonitorLayer layer = new TaskMonitorLayer(this, "Generating Maps", taskView);

		mapTask.addListener(event -> {

			// if this is just a progress event, exit early
			if (event == Event.PROGRESS) {
				return;
			}

			// hide the task panel since this is either COMPLETED or ABORTED
			removeLayer(layer);

			// If this task was aborted instead of completed, exit early
			if (event == Event.ABORTED) {
				return;
			}

			// If there is no result, exit early
			if (!mapTask.getResult().isPresent()) {
				return;
			}

			MapperFrame mapperWindow;
			RawMapSet results = mapTask.getResult().get();
			RawDataController mapData = new RawDataController();

			DataSet sourceDataset = controller.data().getDataSet();

			mapData.setMapData(results, sourceDataset, controller.data().getTitle(),
					controller.data().getDiscards().list(), controller.calibration().getDetectorProfile());

			mapperWindow = new MapperFrame(getTabbedInterface(), mapData, mapSession, controller);
			mapperWindow.setVisible(true);

		});

		pushLayer(layer);
		mapTask.start();

	}

	public void actionSaveSession() {
		if (controller.io().getSessionFile() == null) {
			actionSaveSessionAs();
			return;
		}

		actionSaveSession(controller.io().getSessionFile());

	}

	public void actionSaveSessionAs() {

		StratusFilePanels.saveFile(this, "Save Session", controller.io().getSessionFolder(), new SessionFileExtension(),
				file -> {
					if (!file.isPresent()) {
						return;
					}
					controller.io().setFromSession(file.get());
					actionSaveSession(file.get());
				});
	}

	public void actionSaveSession(File file) {
		try (FileOutputStream os = new FileOutputStream(file)) {
			os.write(controller.save().serialize().getBytes());
			controller.history().setSavePoint();
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to save session", e);
		}
	}

	public void actionSavePicture() {
		SavePicture sp = new SavePicture(this, canvas, controller.io().getLastFolder(), file -> {
			if (file.isPresent()) {
				controller.io().setLastFolder(file.get().getParentFile());
			}
		});
		sp.show();

	}

	public void actionExportArchive() {
		Mutable<ExportPanel> export = new Mutable<>(null);

		export.set(new ExportPanel(this, canvas, () ->

				StratusFilePanels.saveFile(this, "Save Archive", controller.io().getLastFolder(),
						new SimpleFileExtension("Zip Archive", "zip"), file -> {
							if (!file.isPresent()) {
								return;
							}
							controller.io().setLastFolder(file.get().getParentFile());

							SurfaceDescriptor format = export.get().getPlotFormat();
							int width = export.get().getImageWidth();
							int height = export.get().getImageHeight();

							actionExportArchiveToZip(file.get(), format, width, height);

						})));
	}

	private void actionExportArchiveToZip(File file, SurfaceDescriptor format, int width, int height) {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {

			// Save Plot
			String ext = format.extension().toLowerCase();
			ZipEntry e = new ZipEntry("plot." + ext);
			zos.putNextEntry(e);

			canvas.write(format, zos, new Coord<Integer>(width, height));
			zos.closeEntry();

			// save fittings as text
			e = new ZipEntry("fittings.txt");
			zos.putNextEntry(e);
			controller.writeFittingInformation(zos);
			zos.closeEntry();

			if (controller.calibration().hasDetectorProfile()) {
				e = new ZipEntry("detector-profile.pbdp");
				zos.putNextEntry(e);
				String profileYaml = controller.calibration().getDetectorProfile().save();
				zos.write(profileYaml.getBytes());
				zos.closeEntry();
			}

			e = new ZipEntry("session.peakaboo");
			zos.putNextEntry(e);
			zos.write(controller.save().serialize().getBytes());
			zos.closeEntry();

		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Could not save archive", e);
		}
	}

	public void actionSaveFilteredDataSet() {
		SimpleFileExtension text = new CSVFileExtension();
		StratusFilePanels.saveFile(this, "Save Fitted Data to CSV File", controller.io().getLastFolder(), text,
				saveFile -> {
					if (!saveFile.isPresent()) {
						return;
					}
					controller.io().setLastFolder(saveFile.get().getParentFile());

					ExecutorSet<Object> execset = controller.writeFitleredDataSetToCSV(saveFile.get());

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

	public void actionSaveFilteredSpectrum() {
		SimpleFileExtension text = new CSVFileExtension();
		StratusFilePanels.saveFile(this, "Save Spectrum to CSV File", controller.io().getLastFolder(), text,
				saveFile -> {
					if (!saveFile.isPresent()) {
						return;
					}
					controller.io().setLastFolder(saveFile.get().getParentFile());
					controller.writeFitleredSpectrumToCSV(saveFile.get());
				});
	}

	public void actionSaveFittingInformation() {
		SimpleFileExtension ext = new SimpleFileExtension("Text File", "txt");
		StratusFilePanels.saveFile(this, "Save Fitting Information to Text File", controller.io().getLastFolder(), ext,
				file -> {
					if (!file.isPresent()) {
						return;
					}
					controller.io().setLastFolder(file.get().getParentFile());

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

		StratusFilePanels.openFile(this, "Load Session Data", controller.io().getSessionFile(),
				new SessionFileExtension(), file -> {
					if (!file.isPresent()) {
						return;
					}
					actionLoadSession(file.get());
				});

	}

	public void actionLoadSession(File session) {
		controller.io().setFromSession(session);
		load(List.of(new PathDataInputAdapter(session)));
	}

	public void actionShowInfo() {

		Map<String, String> properties;

		properties = new LinkedHashMap<>();
		properties.put("Data Format",
				"" + controller.data().getDataSet().getDataSource().getFileFormat().getFormatName());
		properties.put("Dataset Title", "" + controller.data().getTitle());
		properties.put("Scan Count", "" + controller.data().getDataSet().getScanData().scanCount());
		properties.put("Channels per Scan", "" + controller.data().getDataSet().getAnalysis().channelsPerScan());
		properties.put("Maximum Intensity", "" + controller.data().getDataSet().getAnalysis().maximumIntensity());

		// Only load those attributes which have values
		BiConsumer<String, String> populator = (k, v) -> {
			if (v != null && !"".equals(v)) {
				properties.put(k, v);
			}
		};

		// Extended attributes
		if (controller.data().getDataSet().getMetadata().isPresent()) {
			Metadata metadata = controller.data().getDataSet().getMetadata().get();

			populator.accept("Date of Creation", metadata.getCreationTime());
			populator.accept("Created By", metadata.getCreator());

			populator.accept("Project Name", metadata.getProjectName());
			populator.accept("Session Name", metadata.getSessionName());
			populator.accept("Experiment Name", metadata.getExperimentName());
			populator.accept("Sample Name", metadata.getSampleName());
			populator.accept("Scan Name", metadata.getScanName());

			populator.accept("Facility", metadata.getFacilityName());
			populator.accept("Laboratory", metadata.getLaboratoryName());
			populator.accept("Instrument", metadata.getInstrumentName());
			populator.accept("Technique", metadata.getTechniqueName());

		}

		TitledPanel propPanel = new TitledPanel(new PropertyPanel(properties), false);
		propPanel.setBorder(Spacing.bHuge());

		HeaderLayer layer = new HeaderLayer(this, true);
		layer.setBody(propPanel);
		layer.getHeader().setCentre("Dataset Information");
		this.pushLayer(layer);

	}

	public void actionGuessMaxEnergy() {

		if (controller == null)
			return;
		if (controller.fitting().getVisibleTransitionSeries().isEmpty()) {
			new LayerDialog("Cannot Detect Energy Calibration",
					"Detecting energy calibration requires that at least one element be fitted.\nTry using 'Elemental Lookup', as 'Guided Fitting' will not work without energy calibration set.",
					StockIcon.BADGE_WARNING).showIn(this);
			return;
		}

		StreamExecutorSet<EnergyCalibration> energyTask = AutoEnergyCalibration.propose(
				controller.data().getDataSet().getAnalysis().averagePlot(),
				controller.fitting().getVisibleTransitionSeries(), controller.fitting(),
				controller.data().getDataSet().getAnalysis().channelsPerScan());

		List<TaskMonitorView> views = energyTask.getExecutors().stream().map(TaskMonitorView::new).toList();
		TaskMonitorLayer layer = new TaskMonitorLayer(this, "Detecting Energy Level", views);

		energyTask.last().addListener(event -> {
			// if event is not progress, then its either COMPLETED or ABORTED, so hide the
			// panel
			if (event != Event.PROGRESS) {
				removeLayer(layer);
			}

			// if the last executor completed successfully, then set the calibration
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
		pushLayer(new PluginPanel(this));
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
		if (!controller.fitting().getFittingSelections().hasTransitionSeries(selected)) {
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
		LayerDialog dialog = new LayerDialog("Annotation for " + selected.toString(), textfield);
		dialogbox.set(dialog);
		dialog.addLeft(new FluentButton("Cancel").withAction(dialog::hide));
		dialog.addRight(new FluentButton("OK").withStateDefault().withAction(() -> {
			controller.fitting().setAnnotation(selected, textfield.getText());
			dialog.hide();
		}));
		dialog.showIn(this);
		textfield.grabFocus();
	}

	public void actionQuickMap(int channel) {
		StreamExecutor<RawMapSet> quickmapper = Mapping.quickMapTask(controller.data(), channel);
		var layer = new TaskMonitorLayer(this, "Generating Quick Map", quickmapper);
		pushLayer(layer);

		quickmapper.addListener(event -> {

			switch(event) {
				case ABORTED:
					removeLayer(layer);
					break;
				case COMPLETED:
					removeLayer(layer);
					var result = quickmapper.getResult();
					if (result.isPresent()) {
						QuickMapPanel maplayer = new QuickMapPanel(this, this.tabs, channel, result.get(), mapSession, controller);
						this.pushLayer(maplayer);
					}
					break;
				case PROGRESS:
					break;
				default:
					break;
			}

		});

		quickmapper.start();

	}

	@Override
	public void titleDoubleClicked() {
		if (!controller.data().hasDataSet()) {
			return;
		}

		var dialogbox = new Mutable<LayerDialog>();
		var textfield = new JTextField(20);
		var title = (PeakabooTabTitle) getTabbedInterface().getTabTitleComponent(this);
		var colours = new ArrayList<Color>(Stratus.getTheme().getPalette().getShadeColours("4").values());
		var chooser = new ColourChooser(colours, title.getColour());

		Runnable onClose = () -> {
			controller.data().setTitle(textfield.getText());
			title.setColour(chooser.getSelected());
			dialogbox.get().hide();

		};

		textfield.addActionListener(e -> onClose.run());
		textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					dialogbox.get().hide();
				}
			}
		});
		textfield.setText(controller.data().getTitle());

		JPanel body = new JPanel(new BorderLayout());
		body.add(textfield, BorderLayout.CENTER);
		body.add(chooser, BorderLayout.SOUTH);

		LayerDialog dialog = new LayerDialog("Dataset Title", body);
		dialogbox.set(dialog);
		dialog.addLeft(new FluentButton("Cancel").withAction(dialog::hide));
		dialog.addRight(new FluentButton("OK").withStateDefault().withAction(onClose));
		dialog.showIn(this);
		textfield.grabFocus();

	}

	public boolean hasUnsavedWork() {
		return controller.history().hasUnsavedWork() && controller.data().hasDataSet();
	}

	PlotCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void close() throws Exception {
		this.controller.close();
	}

	public static class SessionFileExtension extends SimpleFileExtension {
		public SessionFileExtension() {
			super("Peakaboo Session Files", "peakaboo");
		}
	}

	public static class CSVFileExtension extends SimpleFileExtension {
		public CSVFileExtension() {
			super("CSV File", "csv");
		}
	}

}
