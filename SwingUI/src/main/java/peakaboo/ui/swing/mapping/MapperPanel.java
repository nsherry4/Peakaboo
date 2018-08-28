package peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import eventful.EventfulTypeListener;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.display.map.MapDisplayMode;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterManager;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.widgets.DraggingScrollPaneListener;
import swidget.widgets.DraggingScrollPaneListener.Buttons;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.Spacing;
import swidget.widgets.tabbedinterface.TabbedInterface;



public class MapperPanel extends LayerPanel
{

	private MapCanvas				canvas;
	
	protected MappingController		controller;
	protected TabbedPlotterManager 	parentPlotter;
	
	private JLabel					warnOnTooSmallDataset;
	private MapStatusBar			statusBar;
	
	private MapperToolbar			toolbar;

	MapperPanel(MappingController controller, TabbedPlotterManager parentPlotter, TabbedInterface<MapperPanel> owner)
	{

		this.controller = controller;
		this.parentPlotter = parentPlotter;

		this.controller.addListener(s -> {
			if (! s.equals(MappingController.UpdateType.AREA_SELECTION.toString())) setNeedsRedraw();
			if (! s.equals(MappingController.UpdateType.POINT_SELECTION.toString())) setNeedsRedraw();
				
			owner.setTabTitle(this, getTitle());
			
			canvas.updateCanvasSize();
			repaint();
		});
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				canvas.updateCanvasSize();
			}
		});

		owner.setTabTitle(this, getTitle());
		

		init();

	}
	
	public String getTitle() {
		return controller.getSettings().getMapFittings().mapLongTitle();
	}


	private void init()
	{

		JPanel contentLayer = this.getContentLayer();
		contentLayer.setLayout(new BorderLayout());
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new MapperSidebar(this, controller), createCanvasPanel());
		split.setResizeWeight(0);
		split.setOneTouchExpandable(true);
		split.setBorder(Spacing.bNone());
		contentLayer.add(split, BorderLayout.CENTER);	
		
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
			if (!file.isPresent()) {
				return;
			}
			try
			{
				controller.getSettings().getView().savePictureFolder = file.get().getParentFile();
				FileOutputStream os = new FileOutputStream(file.get());
				os.write(controller.getSettings().getMapFittings().mapAsCSV().getBytes());
				os.close();
			}
			catch (IOException e)
			{
				PeakabooLog.get().log(Level.SEVERE, "Error saving plot as csv", e);
			}
		});

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
