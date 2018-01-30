package peakaboo.ui.swing.mapping;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.maptab.MapTabController;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import swidget.widgets.Spacing;



public class MapperPanel extends JPanel
{

	private MapCanvas			canvas;
	
	protected MappingController	controller;

	private JLabel			warnOnTooSmallDataset;
	private JLabel			mapMouseMonitor;

	private MapTabController	tabController;
	//private SingleMapModel	viewModel;
	
	MapperFrame					owner;

	private MapperToolbar		toolbar;

	public MapperPanel(MapTabController _tabController, MappingController controller, MapperFrame owner)
	{

		this.controller = controller;
		this.tabController = _tabController;

		this.controller.addListener(s -> {
			if (! s.equals(MappingController.UpdateType.BOUNDING_REGION.toString())) setNeedsRedraw();
			
			toolbar.monochrome.setSelected(controller.mapsController.getMonochrome());
			toolbar.spectrum.setSelected(controller.mapsController.getShowSpectrum());
			toolbar.coords.setSelected(controller.mapsController.getShowCoords());
			
			if (tabController.hasBoundingRegion())
			{
				toolbar.readIntensities.setEnabled(true);
				toolbar.examineSubset.setEnabled(true);
			} else {
				toolbar.readIntensities.setEnabled(false);
				toolbar.examineSubset.setEnabled(false);
			}
			
			repaint();
		});


		this.owner = owner;
		init();

	}


	private void init()
	{

		setLayout(new BorderLayout());
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new SidePanel(controller), createCanvasPanel());
		split.setResizeWeight(0);
		split.setOneTouchExpandable(true);
		split.setBorder(Spacing.bNone());
		add(split, BorderLayout.CENTER);	
		
		toolbar = new MapperToolbar(this, controller);
		add(toolbar, BorderLayout.NORTH);
		
		
		class CompleteMouseListener implements MouseMotionListener, MouseListener
		{
			
			public void mouseDragged(MouseEvent e)
			{
				tabController.setDragEnd( canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true) );
				tabController.setHasBoundingRegion( true );
			}

			public void mouseMoved(MouseEvent e)
			{
				showValueAtCoord(canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), false));
			}

			public void mouseClicked(MouseEvent e){}

			public void mouseEntered(MouseEvent e){}

			public void mouseExited(MouseEvent e){}

			public void mousePressed(MouseEvent e)
			{			
				tabController.setDragStart( canvas.getMapCoordinateAtPoint(e.getX(), e.getY(), true) );
				tabController.setDragEnd( null );
				tabController.setHasBoundingRegion( false );
			}

			public void mouseReleased(MouseEvent e){}
			
		}
		
		canvas.addMouseMotionListener(new CompleteMouseListener());
		canvas.addMouseListener(new CompleteMouseListener());
		

		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String ss)
			{

				if (controller.mapsController.getDataHeight() * controller.mapsController.getDataWidth() == controller.mapsController.getMapSize())
				{
					warnOnTooSmallDataset.setVisible(false);
				}
				else
				{
					warnOnTooSmallDataset.setVisible(true);
				}

				if (controller.getActiveTabController() == tabController) fullRedraw();
			}
		});

		controller.updateListeners("");

	}
	
	public File savePicture(File folder)
	{
		return new SavePicture(owner, canvas, folder).getStartingFolder();
	}


	public MapTabController getTabController()
	{
		return tabController;
	}



	private JPanel createCanvasPanel()
	{
		canvas = new MapCanvas(controller, tabController);

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
		
		if (controller.mapsController.getDataHeight() * controller.mapsController.getDataWidth() == controller.mapsController.getMapSize())
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

		int index = mapCoord.y * controller.mapsController.getDataWidth() + mapCoord.x;
		index++;
		
		if (controller.mapsController.isValidPoint(mapCoord))
		{
			String value = tabController.getIntensityMeasurementAtPoint(mapCoord);
			if (controller.mapsController.getInterpolation() != 0) value += " (not interpolated)";
			
			mapMouseMonitor.setText("Index: " + index + ", X: " + (mapCoord.x + 1) + ", Y: " + (mapCoord.y + 1) + ", Value: "
					+ value);
		}
		else
		{
			mapMouseMonitor.setText(noValue);
		}

	}

}
