package peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
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

import cyclops.Coord;
import cyclops.util.Mutable;
import cyclops.visualization.SaveableSurface;
import cyclops.visualization.SurfaceType;
import cyclops.visualization.backend.awt.AwtSurfaceFactory;
import cyclops.visualization.backend.awt.SavePicture;
import eventful.EventfulTypeListener;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.display.map.MapRenderData;
import peakaboo.display.map.MapRenderSettings;
import peakaboo.display.map.Mapper;
import peakaboo.display.map.modes.MapDisplayMode;
import peakaboo.ui.swing.plotting.ExportPanel;
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
	TabbedInterface<TabbedLayerPanel> parentPlotter;
	
	private JLabel					warnOnTooSmallDataset;
	private MapStatusBar			statusBar;
	
	private MapperToolbar			toolbar;

	public MapperPanel(MappingController controller, TabbedInterface<TabbedLayerPanel> parentPlotter, TabbedInterface<TabbedLayerPanel> owner) {
		super(owner);
		
		this.controller = controller;
		this.parentPlotter = parentPlotter;

		this.controller.addListener(s -> {
			boolean needsRedraw = true;
			if (s.equals(UpdateType.AREA_SELECTION.toString())) {
				needsRedraw = false;
			}
			if (s.equals(UpdateType.POINT_SELECTION.toString())) {
				needsRedraw = false;
			}
			if (needsRedraw) {
				setNeedsRedraw();
			}

			owner.setTabTitle(this, getTabTitle());
			
			canvas.updateCanvasSize();
			repaint();
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
		return controller.getSettings().getMapFittings().mapLongTitle();
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
		
		
//		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, mapCanvas);
//		split.setResizeWeight(0);
//		split.setOneTouchExpandable(true);
//		split.setBorder(Spacing.bNone());
//		contentLayer.add(split, BorderLayout.CENTER);
		
		toolbar = new MapperToolbar(this, controller);
		contentLayer.add(toolbar, BorderLayout.NORTH);
		
		
		class CompleteMouseListener implements MouseMotionListener, MouseListener
		{
			
			public void mouseDragged(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e)) {
					controller.getSettings().getPointsSelection().clearSelection();
					
					AreaSelection selection = controller.getSettings().getAreaSelection();
					selection.setEnd( canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true) );
					selection.setHasBoundingRegion( true );
				}
			}

			public void mouseMoved(MouseEvent e)
			{
				statusBar.showValueAtCoord(canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), false));
				sidebar.showValueAtCoord(canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), false));
			}

			public void mouseClicked(MouseEvent e){
				
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
					MapDisplayMode displayMode = controller.getSettings().getMapFittings().getMapDisplayMode();
					//Double-click selects points with similar intensity
					if ((displayMode == MapDisplayMode.COMPOSITE || displayMode == MapDisplayMode.RATIO) && controller.getSettings().getView().getInterpolation() == 0) {
						
						controller.getSettings().getAreaSelection().clearSelection();
						
						Coord<Integer> clickedAt = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
						if (e.isControlDown()) {
							if (e.getClickCount() == 2) {
								controller.getSettings().getPointsSelection().makeSelection(clickedAt, true, true);
							} else if (e.getClickCount() == 3) {
								//Triple clicks only get run after a double click gets run. If CTRL is down, that means we need to
								//undo the action caused by the previous (improper) double-click, so we re-run the contiguous
								//selection modification to perform the reverse modification.
								controller.getSettings().getPointsSelection().makeSelection(clickedAt, true, true);
								controller.getSettings().getPointsSelection().makeSelection(clickedAt, false, true);
							}
						} else {
							controller.getSettings().getPointsSelection().makeSelection(clickedAt, e.getClickCount() == 2, false);
						}

						
					}
				}
			}

			public void mouseEntered(MouseEvent e){}

			public void mouseExited(MouseEvent e){}

			public void mousePressed(MouseEvent e)
			{			
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 && !e.isControlDown()) {
					controller.getSettings().getPointsSelection().clearSelection();
					
					AreaSelection selection = controller.getSettings().getAreaSelection();
					selection.setStart( canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true) );
					selection.setEnd( null );
					selection.setHasBoundingRegion( false );
				}
			}

			public void mouseReleased(MouseEvent e){}
			
		}
		
		canvas.addMouseMotionListener(new CompleteMouseListener());
		canvas.addMouseListener(new CompleteMouseListener());
		

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String ss)
			{

				if (controller.getSettings().getView().getDataHeight() * controller.getSettings().getView().getDataWidth() == controller.mapsController.getMapSize())
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
	
	
	
	void actionSavePicture()
	{
		if (controller.getSettings().getView().savePictureFolder == null) controller.getSettings().getView().savePictureFolder = controller.getSettings().getView().dataSourceFolder;
		SavePicture sp = new SavePicture(this, canvas, controller.getSettings().getView().savePictureFolder, file -> {
			if (file.isPresent()) {
				controller.getSettings().getView().savePictureFolder = file.get().getParentFile();
			}
		});
		sp.show();
	}
	
	void actionSaveCSV()
	{
		if (controller.getSettings().getView().savePictureFolder == null) {
			controller.getSettings().getView().savePictureFolder = controller.getSettings().getView().dataSourceFolder;
		}
		
		SimpleFileExtension txt = new SimpleFileExtension("Comma Separated Values", "csv");
		SwidgetFilePanels.saveFile(this, "Save Map(s) as CSV", controller.getSettings().getView().savePictureFolder, txt, file -> {
			if (!file.isPresent()) { return; }
			controller.getSettings().getView().savePictureFolder = file.get().getParentFile();
			actionSaveCSV(file.get());
		});

	}
	
	void actionSaveCSV(File file) {
		try	{
			FileOutputStream os = new FileOutputStream(file);
			actionSaveCSV(os);
			os.close();
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Error saving plot as csv", e);
		}
	}
	
	void actionSaveCSV(OutputStream os) throws IOException {
		os.write(controller.getSettings().getMapFittings().mapAsCSV().getBytes());
	}
	
	void actionSaveArchive() {
		Mutable<ExportPanel> export = new Mutable<>(null);
		
		export.set(new ExportPanel(this, canvas, () -> {
			
			SwidgetFilePanels.saveFile(this, "Save Archive", controller.getSettings().getView().savePictureFolder, new SimpleFileExtension("Zip Archive", "zip"), file -> {
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
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
		ZipEntry e;
		
		List<ITransitionSeries> tss = controller.getSettings().getMapFittings().getVisibleTransitionSeries();
		
		
		/*
		 * Little different here than in most places. Saving an image usually just
		 * re-uses the GUI component and has it render to a different backend. This
		 * time, we do it manually so that we can avoid spamming the UI with
		 * events/redraws. We also only draw composite maps here, since drawing
		 * per-element overlays/ratios doesn't make a lot of sense.
		 */
		Coord<Integer> size = new Coord<>(width, height);
		SaveableSurface context;		
		MapRenderSettings settings = controller.getRenderSettings();
				
		for (ITransitionSeries ts : tss) {
		
			Mapper mapper = new Mapper();
			MapRenderData data = new MapRenderData();
			data.compositeData = controller.getSettings().getMapFittings().getCompositeMapData(Optional.of(ts));
			data.maxIntensity = controller.getSettings().getMapFittings().sumAllTransitionSeriesMaps().max();
			
			//image
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
			e = new ZipEntry(ts.toString() + "." + ext);
			zos.putNextEntry(e);
			switch (format) {
			case PDF:
				context = AwtSurfaceFactory.createSaveableSurface(SurfaceType.PDF, width, height);
				mapper.draw(data, settings, context, size);
				context.write(zos);
				break;
			case RASTER:
				context = AwtSurfaceFactory.createSaveableSurface(SurfaceType.RASTER, width, height);
				mapper.draw(data, settings, context, size);
				context.write(zos);
				break;
			case VECTOR:
				context = AwtSurfaceFactory.createSaveableSurface(SurfaceType.VECTOR, width, height);
				mapper.draw(data, settings, context, size);
				context.write(zos);
				break;					
			}
			zos.closeEntry();
			
			//csv
			e = new ZipEntry(ts.toString() + ".csv");
			zos.putNextEntry(e);
			actionSaveCSV(zos);
			zos.closeEntry();
			
		}
		
		e = new ZipEntry("session.peakaboo");
		zos.putNextEntry(e);
		zos.write(controller.getSavedSettings().serialize().getBytes());
		zos.closeEntry();
		
		
		zos.close();
	}


	private JPanel createCanvasPanel()
	{
		canvas = new MapCanvas(controller);
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
				+ controller.mapsController.getMapSize() + ")");
		warnOnTooSmallDataset.setBorder(Spacing.bSmall());
		warnOnTooSmallDataset.setBackground(new Color(0.64f, 0.0f, 0.0f));
		warnOnTooSmallDataset.setForeground(new Color(1.0f, 1.0f, 1.0f));
		warnOnTooSmallDataset.setOpaque(true);
		warnOnTooSmallDataset.setHorizontalAlignment(SwingConstants.CENTER);
		
		if (controller.getSettings().getView().getDataHeight() * controller.getSettings().getView().getDataWidth() == controller.mapsController.getMapSize())
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


	private void setNeedsRedraw()
	{
		canvas.setNeedsRedraw();
	}


	


}
