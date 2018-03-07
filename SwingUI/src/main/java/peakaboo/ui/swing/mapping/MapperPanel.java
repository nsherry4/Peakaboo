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
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.mapdisplay.AreaSelection;
import peakaboo.controller.mapper.mapdisplay.MapDisplayMode;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterManager;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.widgets.Spacing;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedInterfacePanel;



public class MapperPanel extends TabbedInterfacePanel
{

	private MapCanvas			canvas;
	
	protected MappingController	controller;
	protected TabbedPlotterManager parentPlotter;
	
	private JLabel			warnOnTooSmallDataset;
	private JLabel			mapMouseMonitor;
	
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
			
			toolbar.monochrome.setSelected(controller.settings.getMonochrome());
			toolbar.spectrum.setSelected(controller.settings.getShowSpectrum());
			toolbar.coords.setSelected(controller.settings.getShowCoords());
			
			if (controller.getDisplay().getAreaSelection().hasSelection() || controller.getDisplay().getPointsSelection().hasSelection())
			{
				toolbar.readIntensities.setEnabled(true);
				toolbar.examineSubset.setEnabled(true);
			} else {
				toolbar.readIntensities.setEnabled(false);
				toolbar.examineSubset.setEnabled(false);
			}
			
			
			owner.setTabTitle(this, getTitle());
			repaint();
		});

		owner.setTabTitle(this, getTitle());
		

		init();

	}
	
	public String getTitle() {
		return controller.getDisplay().mapLongTitle();
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
				controller.getDisplay().getPointsSelection().clearSelection();
				
				AreaSelection selection = controller.getDisplay().getAreaSelection();
				selection.setEnd( canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true) );
				selection.setHasBoundingRegion( true );
			}

			public void mouseMoved(MouseEvent e)
			{
				showValueAtCoord(canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), false));
			}

			public void mouseClicked(MouseEvent e){
				//Double-click selects points with similar intensity
				if (e.getClickCount() >= 2 && controller.getDisplay().getMapDisplayMode() == MapDisplayMode.COMPOSITE) {
					
					controller.getDisplay().getAreaSelection().clearSelection();
					
					Coord<Integer> clickedAt = canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true);
					controller.getDisplay().getPointsSelection().makeSelection(clickedAt, e.getClickCount() == 2);
				}
			}

			public void mouseEntered(MouseEvent e){}

			public void mouseExited(MouseEvent e){}

			public void mousePressed(MouseEvent e)
			{			
				controller.getDisplay().getPointsSelection().clearSelection();
				
				AreaSelection selection = controller.getDisplay().getAreaSelection();
				selection.setStart( canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true) );
				selection.setEnd( null );
				selection.setHasBoundingRegion( false );
			}

			public void mouseReleased(MouseEvent e){}
			
		}
		
		canvas.addMouseMotionListener(new CompleteMouseListener());
		canvas.addMouseListener(new CompleteMouseListener());
		

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String ss)
			{

				if (controller.settings.getDataHeight() * controller.settings.getDataWidth() == controller.mapsController.getMapSize())
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
		if (controller.settings.savePictureFolder == null) controller.settings.savePictureFolder = controller.settings.dataSourceFolder;
		File result = new SavePicture(frame, canvas, controller.settings.savePictureFolder).getStartingFolder();
		if (result != null) {
			controller.settings.savePictureFolder = result;
		}
	}
	
	public void actionSaveCSV()
	{
		if (controller.settings.savePictureFolder == null) controller.settings.savePictureFolder = controller.settings.dataSourceFolder;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		controller.getDisplay().mapAsCSV(baos);
		try
		{
			File result = SwidgetIO.saveFile(this, "Save Map(s) as Text", "txt", "Text File", controller.settings.savePictureFolder, baos);
			if (result != null) {
				controller.settings.savePictureFolder = result;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	


	private JPanel createCanvasPanel()
	{
		canvas = new MapCanvas(controller);

		JPanel canvasContainer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		canvasContainer.add(canvas, c);

		warnOnTooSmallDataset = new JLabel("Warning: Map dimensions are smaller than data set ("
				+ controller.mapsController.getMapSize() + ")");
		warnOnTooSmallDataset.setBorder(Spacing.bSmall());
		warnOnTooSmallDataset.setBackground(new Color(0.64f, 0.0f, 0.0f));
		warnOnTooSmallDataset.setForeground(new Color(1.0f, 1.0f, 1.0f));
		warnOnTooSmallDataset.setOpaque(true);
		warnOnTooSmallDataset.setHorizontalAlignment(SwingConstants.CENTER);
		
		if (controller.settings.getDataHeight() * controller.settings.getDataWidth() == controller.mapsController.getMapSize())
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

		mapMouseMonitor = new JLabel("");
		mapMouseMonitor.setBorder(Spacing.bSmall());
		mapMouseMonitor.setHorizontalAlignment(JLabel.CENTER);
		mapMouseMonitor.setFont(mapMouseMonitor.getFont().deriveFont(Font.PLAIN));
		showValueAtCoord(null);

		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		canvasContainer.add(mapMouseMonitor, c);

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

	
	public void showValueAtCoord(Coord<Integer> mapCoord)
	{
		String noValue = "Index: -, X: -, Y: -, Value: -";

		if (mapCoord == null)
		{
			mapMouseMonitor.setText(noValue);
			return;
		}

		int index = mapCoord.y * controller.settings.getDataWidth() + mapCoord.x;
		index++;
		
		if (controller.settings.isValidPoint(mapCoord))
		{
			String value = controller.getDisplay().getIntensityMeasurementAtPoint(mapCoord);
			if (controller.settings.getInterpolation() != 0) value += " (not interpolated)";
			
			mapMouseMonitor.setText("Index: " + index + ", X: " + (mapCoord.x + 1) + ", Y: " + (mapCoord.y + 1) + ", Value: "
					+ value);
		}
		else
		{
			mapMouseMonitor.setText(noValue);
		}

	}

}
