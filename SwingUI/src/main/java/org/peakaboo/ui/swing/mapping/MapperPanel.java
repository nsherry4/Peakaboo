package org.peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.Mapper;
import org.peakaboo.display.map.modes.MapDisplayMode;
import org.peakaboo.ui.swing.plotting.ExportPanel;

import cyclops.Coord;
import cyclops.util.Mutable;
import cyclops.visualization.SaveableSurface;
import cyclops.visualization.SurfaceType;
import cyclops.visualization.backend.awt.AwtSurfaceFactory;
import cyclops.visualization.backend.awt.SavePicture;
import eventful.EventfulTypeListener;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.widgets.ClearPanel;
import swidget.widgets.DraggingScrollPaneListener;
import swidget.widgets.DraggingScrollPaneListener.Buttons;
import swidget.widgets.Spacing;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedLayerPanel;



public class MapperPanel extends TabbedLayerPanel {

	private MapCanvas				canvas;
	
	protected MappingController		controller;
	protected TabbedInterface<TabbedLayerPanel> parentPlotter;
	
	private JLabel					warnOnTooSmallDataset;
	private MapStatusBar			statusBar;
	
	private MapperToolbar			toolbar;

	public MapperPanel(MappingController controller, TabbedInterface<TabbedLayerPanel> parentPlotter, TabbedInterface<TabbedLayerPanel> owner) {
		super(owner);
		
		this.controller = controller;
		this.parentPlotter = parentPlotter;

		this.controller.addListener(s -> {
			owner.setTabTitle(this, getTabTitle());
		});
		
		this.addComponentListener(new ComponentAdapter() {
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


	private void init()
	{

		JComponent contentLayer = this.getContentLayer();
		contentLayer.setLayout(new BorderLayout());
		
		MapperSidebar sidebar = new MapperSidebar(this, controller);
		JPanel mapCanvas = createCanvasPanel();
		
		
		Color dividerColour = UIManager.getColor("stratus-widget-border");
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		sidebar.setBorder(new MatteBorder(0, 0, 0, 1, dividerColour));
		ClearPanel split = new ClearPanel(new BorderLayout());
		sidebar.setPreferredSize(new Dimension(225, sidebar.getPreferredSize().height));
		split.add(sidebar, BorderLayout.WEST);
		split.add(mapCanvas, BorderLayout.CENTER);
		contentLayer.add(split, BorderLayout.CENTER);
		
		toolbar = new MapperToolbar(this, controller);
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
		

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String ss)
			{

				if (controller.getUserDimensions().getUserDataHeight() * controller.getUserDimensions().getUserDataWidth() == controller.rawDataController.getMapSize())
				{
					warnOnTooSmallDataset.setVisible(false);
				}
				else
				{
					warnOnTooSmallDataset.setVisible(true);
				}

				fullRedraw();
			}
		});

		controller.updateListeners("");

	}
	

	private JPanel createCanvasPanel()
	{
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
		
		if (controller.getUserDimensions().getUserDataHeight() * controller.getUserDimensions().getUserDataWidth() == controller.rawDataController.getMapSize())
		{
			warnOnTooSmallDataset.setVisible(false);
		}
		else
		{
			warnOnTooSmallDataset.setVisible(true);
		}

		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		canvasContainer.add(warnOnTooSmallDataset, c);

		statusBar = new MapStatusBar(this, controller);

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

	private void fullRedraw()
	{

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{

				MapperPanel.this.validate();
				repaint();

			}
		});

	}

	
	
	
	
	
	/////////////////////////////////////////////
	// UI ACTIONS
	/////////////////////////////////////////////
	
	
	
	void actionSavePicture()
	{
		if (controller.getSettings().savePictureFolder == null) controller.getSettings().savePictureFolder = controller.getSettings().dataSourceFolder;
		SavePicture sp = new SavePicture(this, canvas, controller.getSettings().savePictureFolder, file -> {
			if (file.isPresent()) {
				controller.getSettings().savePictureFolder = file.get().getParentFile();
			}
		});
		sp.show();
	}
	
	void actionSaveCSV()
	{
		if (controller.getSettings().savePictureFolder == null) {
			controller.getSettings().savePictureFolder = controller.getSettings().dataSourceFolder;
		}
		
		SimpleFileExtension txt = new SimpleFileExtension("Comma Separated Values", "csv");
		SwidgetFilePanels.saveFile(this, "Save Map(s) as CSV", controller.getSettings().savePictureFolder, txt, file -> {
			if (!file.isPresent()) { return; }
			controller.getSettings().savePictureFolder = file.get().getParentFile();
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
	

	
	void actionSaveArchive() {
		Mutable<ExportPanel> export = new Mutable<>(null);
		
		export.set(new ExportPanel(this, canvas, () -> {
			
			SwidgetFilePanels.saveFile(this, "Save Archive", controller.getSettings().savePictureFolder, new SimpleFileExtension("Zip Archive", "zip"), file -> {
				if (!file.isPresent()) {
					return;
				}
				
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
		
		controller.writeArchive(fos, format, width, height, surfaceFactory);
	}

}
