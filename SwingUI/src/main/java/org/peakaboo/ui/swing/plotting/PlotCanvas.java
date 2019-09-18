package org.peakaboo.ui.swing.plotting;



import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.PlotUpdateType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.plot.PlotData;
import org.peakaboo.display.plot.PlotSettings;
import org.peakaboo.display.plot.Plotter;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.GraphicsPanel;
import org.peakaboo.framework.eventful.EventfulTypeListener;



/**
 * @author Nathaniel Sherry, 2009 This class creates a Canvas object which can be used to draw on. It implements
 *         Scrollable, and tracks a zoom property. It does not handle mouse events -- UIs wishing to handle mouse motion
 *         logic should add their own listeners
 */

public class PlotCanvas extends GraphicsPanel implements Scrollable
{

	private PlotController controller;
	private BiConsumer<Integer, Coord<Integer>>	onSingleClickCallback, onDoubleClickCallback, onRightClickCallback;
	private PlotPanel plotPanel;
	private Plotter plotter;

	PlotCanvas(final PlotController controller, final PlotPanel parent)
	{

		super();
		this.setFocusable(true);

		this.controller = controller;
		this.plotter = new Plotter();
		this.plotPanel = parent;
		this.setMinimumSize(new Dimension(100, 100));

		//setCanvasSize();

		controller.addListener(type -> {
			switch (type) {
			case UI:
			case DATA:	
			case UNDO:
				updateCanvasSize();
			default: break;
			}
		});
		
		
		new FileDrop(this, new FileDrop.Listener() {
			
			@Override
			public void urisDropped(URI[] uris) {
				List<File> files = new ArrayList<>();
				for (URI uri : uris) {
					try {
						//TODO: download this as part of opening the dataset
						files.add(FileDrop.getUriAsFile(uri));
					} catch (IOException e) {
						PeakabooLog.get().log(Level.SEVERE, "Failed to download data", e);
						return;
					}
				}
				parent.load(files);
			}
			
			@Override
			public void filesDropped(File[] files) {
				parent.load(Arrays.asList(files));
			}
		});


		
		addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e)
			{}
			
		
			public void mousePressed(MouseEvent e)
			{}
			
		
			public void mouseExited(MouseEvent e)
			{}
			
		
			public void mouseEntered(MouseEvent e)
			{}
			
		
			public void mouseClicked(MouseEvent e) {
				
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
						
				if (controller.data().hasDataSet()) {
					
					Coord<Integer> mouseCoords = new Coord<>(e.getX(), e.getY());
					if (oneclick && leftclick) {
						if (onSingleClickCallback != null) {
							onSingleClickCallback.accept(plotter.getChannel(e.getX()), mouseCoords);
						} else {
							onSingleClick(e);
						}
					} else if (twoclick && leftclick) {
						if (onDoubleClickCallback != null) {
							onDoubleClickCallback.accept(plotter.getChannel(e.getX()), mouseCoords);
						}
					} else if (oneclick && rightclick) {
						if (controller.data().hasDataSet() && onRightClickCallback != null) {
							onRightClickCallback.accept(plotter.getChannel(e.getX()), mouseCoords);
						}
					}
						
					
				}
				
				
				//Make the plot canvas focusable
				if (!PlotCanvas.this.hasFocus()) {
					PlotCanvas.this.requestFocus();
				}
			}
		});

	}
	
	private void onSingleClick(MouseEvent e) {

		ITransitionSeries bestFit = controller.fitting().selectTransitionSeriesAtChannel(plotter.getChannel(e.getX()));
        controller.fitting().clearProposedTransitionSeries();
        controller.fitting().setHighlightedTransitionSeries(Collections.emptyList());
        if (bestFit != null) {
            controller.fitting().setHighlightedTransitionSeries(Collections.singletonList(bestFit));
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


	
	
	
	
	private Dimension calculateCanvasSize() {
		//Width
		double parentWidth = 1.0;
		if (this.getParent() != null)
		{
			parentWidth = this.getParent().getWidth();
		}

		int newWidth = (int) (controller.data().getDataSet().getAnalysis().channelsPerScan() * controller.view().getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		
		
		//Height
		double parentHeight = 1.0;
		if (this.getParent() != null)
		{
			parentHeight = this.getParent().getHeight();
		}

		int newHeight = (int) (200 * controller.view().getZoom());
		if (newHeight < parentHeight) newHeight = (int) parentHeight;
		
		if (controller.view().getLockPlotHeight()) {
			newHeight = (int) parentHeight;
		}
		
		//Generate new size
		Dimension newSize = new Dimension(newWidth, newHeight);
		
		return newSize;
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
	public float getUsedHeight()
	{
		return getUsedHeight(1);
	}


	@Override
	public float getUsedWidth()
	{
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
	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(600, 1000);
	}


	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return channelWidth(50);
	}


	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}


	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}


	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return channelWidth(5);
	}



	
	
	
	
	public void setNeedsRedraw() {
		plotter.setNeedsRedraw();
	}


}
