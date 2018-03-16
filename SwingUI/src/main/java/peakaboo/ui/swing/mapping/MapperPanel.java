package peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import peakaboo.controller.mapper.settings.MapDisplayMode;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterManager;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import swidget.dialogues.fileio.SimpleFileExtension;
import swidget.dialogues.fileio.SwidgetFilePanels;
import swidget.widgets.DraggingScrollPaneListener;
import swidget.widgets.DraggingScrollPaneListener.Buttons;
import swidget.widgets.Spacing;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedInterfacePanel;



public class MapperPanel extends TabbedInterfacePanel
{

	private MapCanvas			canvas;
	
	protected MappingController	controller;
	protected TabbedPlotterManager parentPlotter;
	
	private JLabel			warnOnTooSmallDataset;
	private MapStatusBar			statusBar;
	
	TabbedInterface<MapperPanel>	owner;
	MapperFrame						frame;

	private MapperToolbar		toolbar;

	public MapperPanel(MappingController controller, TabbedPlotterManager parentPlotter, TabbedInterface<MapperPanel> owner, MapperFrame frame)
	{

		this.controller = controller;
		this.parentPlotter = parentPlotter;
		this.frame = frame;
		this.owner = owner;

		this.controller.addListener(s -> {
			if (! s.equals(MappingController.UpdateType.AREA_SELECTION.toString())) setNeedsRedraw();
			if (! s.equals(MappingController.UpdateType.POINT_SELECTION.toString())) setNeedsRedraw();
			
			toolbar.monochrome.setSelected(controller.getSettings().getView().getMonochrome());
			toolbar.spectrum.setSelected(controller.getSettings().getView().getShowSpectrum());
			toolbar.coords.setSelected(controller.getSettings().getView().getShowCoords());
			
			if (controller.getSettings().getAreaSelection().hasSelection() || controller.getSettings().getPointsSelection().hasSelection())
			{
				toolbar.readIntensities.setEnabled(true);
				toolbar.examineSubset.setEnabled(true);
			} else {
				toolbar.readIntensities.setEnabled(false);
				toolbar.examineSubset.setEnabled(false);
			}
						
			owner.setTabTitle(this, getTitle());
			//if (s.equals(MappingController.UpdateType.UI_OPTIONS.toString())) {
				canvas.updateCanvasSize();
			//}
			repaint();
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
					//Double-click selects points with similar intensity
					if (controller.getSettings().getMapFittings().getMapDisplayMode() == MapDisplayMode.COMPOSITE &&
						controller.getSettings().getView().getInterpolation() == 0	
						) {
						
						controller.getSettings().getAreaSelection().clearSelection();
						
						Coord<Integer> clickedAt = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
						controller.getSettings().getPointsSelection().makeSelection(clickedAt, e.getClickCount() == 2);
					}
				}
			}

			public void mouseEntered(MouseEvent e){}

			public void mouseExited(MouseEvent e){}

			public void mousePressed(MouseEvent e)
			{			
				if (SwingUtilities.isLeftMouseButton(e)) {
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
	
	
	
	public void actionSavePicture()
	{
		if (controller.getSettings().getView().savePictureFolder == null) controller.getSettings().getView().savePictureFolder = controller.getSettings().getView().dataSourceFolder;
		SavePicture sp = new SavePicture(this, canvas, controller.getSettings().getView().savePictureFolder, file -> {
			if (file.isPresent()) {
				controller.getSettings().getView().savePictureFolder = file.get().getParentFile();
			}
		});
		sp.show();
	}
	
	public void actionSaveCSV()
	{
		if (controller.getSettings().getView().savePictureFolder == null) {
			controller.getSettings().getView().savePictureFolder = controller.getSettings().getView().dataSourceFolder;
		}
		
		String csv = controller.getSettings().getMapFittings().mapAsCSV();

		SimpleFileExtension txt = new SimpleFileExtension("Comma Separated Values", "csv");
		SwidgetFilePanels.saveFile(this, "Save Map(s) as CSV", controller.getSettings().getView().savePictureFolder, txt, file -> {
			if (!file.isPresent()) {
				return;
			}
			try
			{
				controller.getSettings().getView().savePictureFolder = file.get().getParentFile();
				FileOutputStream os = new FileOutputStream(file.get());
				os.write(csv.getBytes());
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


	public void fullRedraw()
	{

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{

				MapperPanel.this.validate();
				repaint();

			}
		});

	}


	public void setNeedsRedraw()
	{
		canvas.setNeedsRedraw();
	}

	


}
