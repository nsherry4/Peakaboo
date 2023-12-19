package org.peakaboo.ui.swing.plotting;



import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.source.model.datafile.PathDataFile;
import org.peakaboo.display.plot.PlotData;
import org.peakaboo.display.plot.PlotSettings;
import org.peakaboo.display.plot.Plotter;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.GraphicsPanel;
import org.peakaboo.framework.plural.monitor.TaskMonitor;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorPanel;
import org.peakaboo.framework.stratus.api.hookins.FileDrop;
import org.peakaboo.ui.swing.Peakaboo;



/**
 * @author Nathaniel Sherry, 2009 This class creates a Canvas object which can be used to draw on. It implements
 *         Scrollable, and tracks a zoom property. It does not handle mouse events -- UIs wishing to handle mouse motion
 *         logic should add their own listeners
 */

public class PlotCanvas extends GraphicsPanel implements Scrollable {

	private PlotController controller;
	private BiConsumer<Integer, Coord<Integer>>	onSingleClickCallback, onDoubleClickCallback, onRightClickCallback, onMouseMoveCallback;
	private Plotter plotter;
	
	private PlotPanel plotPanel;
	

	PlotCanvas(final PlotController controller, final PlotPanel parent) {

		super();
		this.plotPanel = parent; 
		this.setFocusable(true);

		this.controller = controller;
		this.plotter = new Plotter();
		this.setMinimumSize(new Dimension(100, 100));

		addControllerListener();
		addFileDropListener();
		addMouseListeners();
		
	}
	
	private void addMouseListeners() {
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				onMouseClicked(e);
			}	

		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Coord<Integer> mouseCoords = new Coord<>(e.getX(), e.getY());
				int channel = plotter.getChannel(e.getX());
				onMouseMove(channel, mouseCoords);
			}
		});
	}
	
	private void onMouseClicked(MouseEvent e) {
		if (!controller.data().hasDataSet()) {
			return;
		}
		
		/*
		 * Mouse clicking on the plot can be a few things
		 * * Selecting a fitting 'for' a channel (single left click)
		 * * Annotating a fitting 'for' a channel (double left click)
		 * * Getting a popup menu 'for' a channel (right click)
		 */

		boolean oneclick = e.getClickCount() == 1;
		boolean twoclick = e.getClickCount() == 2;
		boolean rightclick = SwingUtilities.isRightMouseButton(e);
		boolean leftclick = SwingUtilities.isLeftMouseButton(e);


		Coord<Integer> mouseCoords = new Coord<>(e.getX(), e.getY());
		int channel = plotter.getChannel(e.getX());
		if (oneclick && leftclick) {
			onSingleClick(channel, mouseCoords);
		} else if (twoclick && leftclick) {
			onDoubleClick(channel, mouseCoords);
		} else if (oneclick && rightclick) {
			onRightClick(channel, mouseCoords);
		}
				
		//Make the plot canvas focusable
		if (!PlotCanvas.this.hasFocus()) {
			PlotCanvas.this.requestFocus();
		}
	}
	
	private void onSingleClick(int channel, Coord<Integer> mouseCoords) {		
		if (onSingleClickCallback != null) {
			onSingleClickCallback.accept(channel, mouseCoords);
		} else {
			ITransitionSeries bestFit = controller.fitting().selectTransitionSeriesAtChannel(channel);
	        controller.fitting().clearProposedTransitionSeries();
	        controller.fitting().setHighlightedTransitionSeries(Collections.emptyList());
	        if (bestFit != null) {
	            controller.fitting().setHighlightedTransitionSeries(Collections.singletonList(bestFit));
	        }
		}
	}
	
	private void onDoubleClick(int channel, Coord<Integer> mouseCoords) {
		if (onDoubleClickCallback != null) {
			onDoubleClickCallback.accept(channel, mouseCoords);
		}
	}
	
	private void onRightClick(int channel, Coord<Integer> mouseCoords) {
		if (controller.data().hasDataSet() && onRightClickCallback != null) {
			onRightClickCallback.accept(channel, mouseCoords);
		}
	}
	
	private void onMouseMove(int channel, Coord<Integer> mouseCoords) {
		if (controller.data().hasDataSet() && onMouseMoveCallback != null) {
			onMouseMoveCallback.accept(channel, mouseCoords);
		}
	}
	
	
	private void addControllerListener() {
		controller.addListener(type -> {
			switch (type) {
			case UI:
			case DATA:	
			case UNDO:
				updateCanvasSize();
				break;
			default: 
				break;
			}
		});
	}
	
	private void addFileDropListener() {
		new FileDrop(this, getFileDropListener());
	}
	
	public FileDrop.Listener getFileDropListener() {
		return new FileDrop.Listener() {
			
			@Override
			public void urlsDropped(URL[] urls) {
				PlotCanvas.this.urlsDropped(urls);
			}
			
			@Override
			public void filesDropped(File[] files) {
				PlotCanvas.this.filesDropped(files);
			}
		};
	}
	
	void filesDropped(File[] files) {
		plotPanel.load(Arrays.asList(files).stream().map(PathDataFile::new).collect(Collectors.toList()));
	}
	
	void urlsDropped(URL[] urls) {
		try {
			
			TaskMonitor<List<File>> monitor = Peakaboo.getUrlsAsync(Arrays.asList(urls), optfiles -> {
				if (!optfiles.isPresent()) { return; }
				plotPanel.load(optfiles.get().stream().map(PathDataFile::new).collect(Collectors.toList()));
			});

			TaskMonitorPanel.onLayerPanel(monitor, plotPanel);

		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to download data", e);
		}
	}
	




	/////////////////////////////////////////////
	// Low Level click handlers
	/////////////////////////////////////////////
	
	public void setSingleClickCallback(BiConsumer<Integer, Coord<Integer>> callback) {
		onSingleClickCallback = callback;
	}

	public void setDoubleClickCallback(BiConsumer<Integer, Coord<Integer>> onDoubleClickCallback) {
		this.onDoubleClickCallback = onDoubleClickCallback;
	}

	public void setRightClickCallback(BiConsumer<Integer, Coord<Integer>> onRightClickCallback) {
		this.onRightClickCallback = onRightClickCallback;
	}

	public void setMouseMoveCallback(BiConsumer<Integer, Coord<Integer>> onMouseMoveCallback) {
		this.onMouseMoveCallback = onMouseMoveCallback;
	}
	

	
	
	
	
	private Dimension calculateCanvasSize() {
		
		final float WIDTH_FUDGE_FACTOR = 1.2f;
		
		int channels = controller.data().getDataSet().getAnalysis().channelsPerScan();
		float zoom = controller.view().getZoom();

		//Transform zoom
		// UI zoom is from 0.1 to 10
		/**
		 * We want most of the control for the zoom to be around normal zoom levels, so
		 * we re-center the zoom value from -1 to 1 and apply an exponential function to
		 * the zoom value
		 * 
		 * We center the zoom value around 0 so that we can apply an inverse multiplier when
		 * zooming out and so we mirror the curve for zooming in 
		 */
		
		//convert to 0 - 9.9
		zoom -= 0.1f;
		//convert to 0 - 1
		zoom /= 9.9f;
		//convert to -1 to 1
		zoom = (zoom * 2f) - 1f;
		boolean invert = zoom < 0;
		boolean lockToWidth = zoom <= -0.99f;
		zoom = Math.abs(zoom);
		zoom = (float)Math.exp(zoom);
		
		if (invert) {
			zoom = 1f / zoom;
		}
			
		double parentWidth = 1.0;
		double parentHeight = 1.0;
		if (this.getParent() != null) {
			parentWidth = this.getParent().getWidth();
			parentHeight = this.getParent().getHeight();
		}

		// Height
		int newHeight = (int) parentHeight;
		
		// Width
		// The width scales at a fraction the rate of height. y=mx+b
		float heightScale = (0.0004f * newHeight) + 0.75f;
		
		int newWidth = (int) (channels * zoom * heightScale * WIDTH_FUDGE_FACTOR);
		if (newWidth < parentWidth || lockToWidth) {
			newWidth = (int) parentWidth;
		}
		
		//Generate new size
		return new Dimension(newWidth, newHeight);
	}

	void updateCanvasSize()
	{
		
		Dimension newSize = calculateCanvasSize();
		Rectangle oldView = this.getVisibleRect();
		Dimension oldSize = getPreferredSize();
		
		if (newSize.equals(oldSize)) {
			return;
		}
		
		Rectangle newView = new Rectangle(oldView);
		

		//Ratio of new size to old one.
		float dx = (float)newSize.width / (float)oldSize.width;
		float dy = (float)newSize.height / (float)oldSize.height;

		//Scale view by size ratio
		newView.x = (int) (oldView.x * dx);
		newView.y = (int) (oldView.y * dy);

		//Set new size and update
		this.setPreferredSize(newSize);
		this.revalidate();
		this.scrollRectToVisible(newView);

	}

	@Override
	public void validate() {
		updateCanvasSize();
		super.validate();
	}
	

	private int channelWidth(int multiplier)
	{
		return (int) Math.max(1.0f, Math.round(controller.view().getZoom() * multiplier));
	}


	int channelFromCoordinate(int x) {
		return plotter.getChannel(x);
	}



	
	
	
	
	
	
	
	
	
	
	
	
	

	//**************************************************************
	// GraphicsPanel extension
	//**************************************************************
	@Override
	protected void drawGraphics(Surface context, Coord<Integer> size)
	{
				
		try {
			
			PlotData data = controller.getPlotData();
			if (data.filtered == null) {
				//No Data
				return;
			}
			PlotSettings settings = controller.view().getPlotSettings();
			
			plotter.draw(data, settings, context, size);
	
			
		} catch (Exception e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to draw plot", e);
			throw e;
		}
	}


	@Override
	public float getUsedHeight() {
		return getUsedHeight(1);
	}


	@Override
	public float getUsedWidth() {
		return getUsedWidth(1);
	}
	
	@Override
	public float getUsedWidth(float zoom) {
		return getWidth() * zoom;
	}


	@Override
	public float getUsedHeight(float zoom) {
		return getHeight() * zoom;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//**************************************************************
	// Scrollable Interface
	//**************************************************************
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(600, 1000);
	}


	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return channelWidth(50);
	}


	public boolean getScrollableTracksViewportHeight() {
		return false;
	}


	public boolean getScrollableTracksViewportWidth() {
		return false;
	}


	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return channelWidth(5);
	}



	
	
	
	
	public void setNeedsRedraw() {
		plotter.invalidate();
	}


}
