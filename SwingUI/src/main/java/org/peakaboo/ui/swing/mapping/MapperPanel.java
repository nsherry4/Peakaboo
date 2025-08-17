package org.peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Mutable;
import org.peakaboo.framework.cyclops.visualization.ExportableSurface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.SavePicture;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorLayer;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.hookins.DraggingScrollPaneListener;
import org.peakaboo.framework.stratus.api.hookins.DraggingScrollPaneListener.Buttons;
import org.peakaboo.framework.stratus.components.Banner;
import org.peakaboo.framework.stratus.components.dialogs.fileio.SimpleFileExtension;
import org.peakaboo.framework.stratus.components.dialogs.fileio.StratusFilePanels;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedLayerPanel;
import org.peakaboo.ui.swing.mapping.components.MapSelectionListener;
import org.peakaboo.ui.swing.mapping.components.MapStatusBar;
import org.peakaboo.ui.swing.mapping.components.MapperToolbar;
import org.peakaboo.ui.swing.mapping.sidebar.MapperSidebar;
import org.peakaboo.ui.swing.plotting.ExportPanel;



public class MapperPanel extends TabbedLayerPanel {

	private MapCanvas				canvas;
	private JScrollPane 			canvasScroller;
	
	protected MappingController		controller;
	protected TabbedInterface<TabbedLayerPanel> parentPlotter;
	
	private Banner					warnOnTooSmallDataset;
	private MapStatusBar			statusBar;
	
	public MapperPanel(MappingController controller, TabbedInterface<TabbedLayerPanel> parentPlotter, TabbedInterface<TabbedLayerPanel> owner) {
		super(owner);
		
		this.controller = controller;
		this.parentPlotter = parentPlotter;

		this.controller.addListener(t -> owner.setTabTitle(this, getTabTitle()));
		
		this.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				canvas.updateCanvasSize();
			}
		});

		owner.setTabTitle(this, getTabTitle());
		

		init();

	}
	
	public String getTabTitle() {
		return controller.getFitting().mapLongTitle();
	}


	private void init() {

		JComponent contentLayer = this.getContentLayer();
		contentLayer.setLayout(new BorderLayout());
		
		MapperSidebar sidebar = new MapperSidebar(this, controller);
		JPanel mapCanvas = createCanvasPanel();
		
		sidebar.setBorder(new MatteBorder(0, 0, 0, 1, Stratus.getTheme().getWidgetBorder()));
		ClearPanel split = new ClearPanel(new BorderLayout());
		sidebar.setPreferredSize(new Dimension(225, sidebar.getPreferredSize().height));
		split.add(sidebar, BorderLayout.WEST);
		split.add(mapCanvas, BorderLayout.CENTER);
		contentLayer.add(split, BorderLayout.CENTER);
		
		MapperToolbar toolbar = new MapperToolbar(this, controller);
		contentLayer.add(toolbar, BorderLayout.NORTH);
		
		
		MapSelectionListener selectionListener = new MapSelectionListener(canvas, controller);
		
		MouseMotionListener movementListener = new MouseMotionAdapter() {
		
			@Override
			public void mouseMoved(MouseEvent e) {
				statusBar.showValueAtCoord(canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), false));
				sidebar.showValueAtCoord(canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), false));
			}

		};
		
		canvas.addMouseMotionListener(movementListener);
		canvas.addMouseMotionListener(selectionListener);
		canvas.addMouseListener(selectionListener);
		canvas.addMouseWheelListener(selectionListener);
		

		controller.addListener(t -> {
			if (controller.getUserDimensions().getUserDataHeight() * controller.getUserDimensions().getUserDataWidth() == controller.rawDataController.getMapSize()) {
				warnOnTooSmallDataset.hideBanner();
			} else {
				warnOnTooSmallDataset.showBanner();
			}

			if (controller.getSettings().getZoom() > 1f) {
				canvasScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				canvasScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			} else {
				canvasScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				canvasScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			}

			fullRedraw();
		});


		//TODO: Why is this here?
		controller.updateListeners(MapUpdateType.UI_OPTIONS);

	}
	

	private JPanel createCanvasPanel() {
		canvas = new MapCanvas(controller, true);
		canvasScroller = new JScrollPane(canvas);
		canvasScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		canvasScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		canvasScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		canvasScroller.setWheelScrollingEnabled(false);
		new DraggingScrollPaneListener(canvasScroller.getViewport(), canvas, Buttons.MIDDLE, Buttons.RIGHT);
		
		JPanel canvasContainer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		canvasContainer.add(canvasScroller, c);

		warnOnTooSmallDataset = new Banner(
				"Map dimensions are smaller than data set (" + controller.rawDataController.getMapSize() + ")", 
				Banner.STYLE_WARN, 
				true
			);
		
		int userHeight = controller.getUserDimensions().getUserDataHeight();
		int userWidth = controller.getUserDimensions().getUserDataWidth();
		if (userHeight * userWidth == controller.rawDataController.getMapSize()) {
			warnOnTooSmallDataset.hideBanner();
		} else {
			warnOnTooSmallDataset.showBanner();
		}

		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		canvasContainer.add(warnOnTooSmallDataset, c);

		statusBar = new MapStatusBar(controller, this);

		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		canvasContainer.add(statusBar, c);

		return canvasContainer;
	}
	
	public TabbedInterface<TabbedLayerPanel> getParentPlotter() {
		return parentPlotter;
	}

	private void fullRedraw() {
		javax.swing.SwingUtilities.invokeLater(() -> {
			MapperPanel.this.validate();
			repaint();
		});
	}

	
	
	
	
	
	/////////////////////////////////////////////
	// UI ACTIONS
	/////////////////////////////////////////////
	
	
	
	public void actionSavePicture()
	{
		SavePicture sp = new SavePicture(this, canvas, controller.getSettings().lastFolder, file -> {
			if (file.isPresent()) {
				controller.getSettings().lastFolder = file.get().getParentFile();
			}
		});
		sp.show();
	}
	
	public void actionSaveCSV()
	{		
		SimpleFileExtension txt = new SimpleFileExtension("Comma Separated Values", "csv");
		StratusFilePanels.saveFile(this, "Save Map(s) as CSV", controller.getSettings().lastFolder, txt, file -> {
			if (!file.isPresent()) { return; }
			controller.getSettings().lastFolder = file.get().getParentFile();
			actionSaveCSV(file.get());
		});

	}
	
	void actionSaveCSV(File file) {
		try	{
			FileOutputStream os = new FileOutputStream(file);
			controller.writeCSV(os);
			os.close();
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Error saving plot as csv", e);
		}
	}
	

	
	public void actionSaveArchive() {
		Mutable<ExportPanel> export = new Mutable<>(null);
		
		export.set(new ExportPanel(this, canvas, () -> {
			
			StratusFilePanels.saveFile(this, "Save Archive", controller.getSettings().lastFolder, new SimpleFileExtension("Zip Archive", "zip"), file -> {
				if (!file.isPresent()) {
					return;
				}
				controller.getSettings().lastFolder = file.get().getParentFile();
				
				SurfaceDescriptor format = export.get().getPlotFormat();
				int width = export.get().getImageWidth();
				int height = export.get().getImageHeight();
				
				try {
					actionSaveArchive(file.get(), format, width, height);
				} catch (IOException e) {
					PeakabooLog.get().log(Level.SEVERE, "Error saving maps as archive", e);
				}
				
				
			});
		}));
	}

	void actionSaveArchive(File file, SurfaceDescriptor format, int width, int height) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		
		Supplier<ExportableSurface> surfaceFactory = () -> (ExportableSurface)format.create(new Coord<>(width, height));
		StreamExecutor<Void> archiver = controller.writeArchive(fos, format, width, height, surfaceFactory);
		
		TaskMonitorLayer layer = new TaskMonitorLayer(this, "Generating Archive", archiver);
		archiver.addListener(event -> {
			switch(event) {
			case ABORTED, COMPLETED:
				removeLayer(layer);
				break;
				
			case PROGRESS:
			default:
				break;
			
			}
		});
		
		//ExecutorSetViewLayer layer = new ExecutorSetViewLayer(this, executorset);
		this.pushLayer(layer);
		
		//executorset.startWorking();
		archiver.start();
		
	}

	@Override
	public void titleDoubleClicked() {
		// NOOP
		
	}

}
