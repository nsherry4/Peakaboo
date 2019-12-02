package org.peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.cyclops.visualization.SaveableSurface;
import org.peakaboo.framework.cyclops.visualization.SurfaceType;
import org.peakaboo.framework.cyclops.visualization.backend.awt.AwtSurfaceFactory;
import org.peakaboo.framework.cyclops.visualization.backend.awt.SavePicture;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.swing.ExecutorSetViewLayer;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.dialogues.fileio.SimpleFileExtension;
import org.peakaboo.framework.swidget.dialogues.fileio.SwidgetFilePanels;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.DraggingScrollPaneListener;
import org.peakaboo.framework.swidget.widgets.DraggingScrollPaneListener.Buttons;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.tabbedinterface.TabbedInterface;
import org.peakaboo.framework.swidget.widgets.tabbedinterface.TabbedLayerPanel;
import org.peakaboo.ui.swing.mapping.components.MapSelectionListener;
import org.peakaboo.ui.swing.mapping.components.MapStatusBar;
import org.peakaboo.ui.swing.mapping.components.MapperToolbar;
import org.peakaboo.ui.swing.mapping.sidebar.MapperSidebar;
import org.peakaboo.ui.swing.plotting.ExportPanel;



public class MapperPanel extends TabbedLayerPanel {

	private MapCanvas				canvas;
	
	protected MappingController		controller;
	protected TabbedInterface<TabbedLayerPanel> parentPlotter;
	
	private JLabel					warnOnTooSmallDataset;
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
		
		sidebar.setBorder(new MatteBorder(0, 0, 0, 1, Swidget.dividerColor()));
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
		

		controller.addListener(t -> {
			if (controller.getUserDimensions().getUserDataHeight() * controller.getUserDimensions().getUserDataWidth() == controller.rawDataController.getMapSize()) {
				warnOnTooSmallDataset.setVisible(false);
			} else {
				warnOnTooSmallDataset.setVisible(true);
			}

			fullRedraw();
		});


		//TODO: Why is this here?
		controller.updateListeners(MapUpdateType.UI_OPTIONS);

	}
	

	private JPanel createCanvasPanel() {
		canvas = new MapCanvas(controller, true);
		JScrollPane canvasScroller = new JScrollPane(canvas);
		canvasScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		canvasScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		canvasScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		new DraggingScrollPaneListener(canvasScroller.getViewport(), canvas, Buttons.MIDDLE, Buttons.RIGHT);
		
		JPanel canvasContainer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		canvasContainer.add(canvasScroller, c);

		warnOnTooSmallDataset = new JLabel("Warning: Map dimensions are smaller than data set ("
				+ controller.rawDataController.getMapSize() + ")");
		warnOnTooSmallDataset.setBorder(Spacing.bSmall());
		warnOnTooSmallDataset.setBackground(new Color(0.64f, 0.0f, 0.0f));
		warnOnTooSmallDataset.setForeground(new Color(1.0f, 1.0f, 1.0f));
		warnOnTooSmallDataset.setOpaque(true);
		warnOnTooSmallDataset.setHorizontalAlignment(SwingConstants.CENTER);
		
		int userHeight = controller.getUserDimensions().getUserDataHeight();
		int userWidth = controller.getUserDimensions().getUserDataWidth();
		if (userHeight * userWidth == controller.rawDataController.getMapSize()) {
			warnOnTooSmallDataset.setVisible(false);
		} else {
			warnOnTooSmallDataset.setVisible(true);
		}

		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		canvasContainer.add(warnOnTooSmallDataset, c);

		statusBar = new MapStatusBar(controller);

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
		SwidgetFilePanels.saveFile(this, "Save Map(s) as CSV", controller.getSettings().lastFolder, txt, file -> {
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
			
			SwidgetFilePanels.saveFile(this, "Save Archive", controller.getSettings().lastFolder, new SimpleFileExtension("Zip Archive", "zip"), file -> {
				if (!file.isPresent()) {
					return;
				}
				controller.getSettings().lastFolder = file.get().getParentFile();
				
				SurfaceType format = export.get().getPlotFormat();
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

	void actionSaveArchive(File file, SurfaceType format, int width, int height) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		
		Supplier<SaveableSurface> surfaceFactory = () -> {
			switch (format) {
			case PDF: return AwtSurfaceFactory.createSaveableSurface(SurfaceType.PDF, width, height);
			case RASTER: return AwtSurfaceFactory.createSaveableSurface(SurfaceType.RASTER, width, height);
			case VECTOR:return AwtSurfaceFactory.createSaveableSurface(SurfaceType.VECTOR, width, height);
			}
			return null;
		};
		
		ExecutorSet<Void> executorset = controller.writeArchive(fos, format, width, height, surfaceFactory);
		
		ExecutorSetViewLayer layer = new ExecutorSetViewLayer(this, executorset);
		this.pushLayer(layer);
		
		executorset.startWorking();
		
	}

	@Override
	public void titleDoubleClicked() {
		// NOOP
		
	}

}
