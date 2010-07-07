package peakaboo.ui.swing.plotting;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import fava.Fn;
import fava.signatures.FunctionMap;

import peakaboo.controller.plotter.PlotController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import swidget.dialogues.fileio.AbstractFile;
import swidget.environment.Env;



/**
 * @author Nathaniel Sherry, 2009 This class creates a Canvas object which can be used to draw on. It implements
 *         Scrollable, and tracks a zoom property. It does not handle mouse events -- UIs wishing to handle mouse motion
 *         logic should add their own listeners
 */

public class PlotCanvas extends JPanel implements Scrollable
{


	private PlotController	controller;

	private boolean			hasData;


	public PlotCanvas(PlotController controller, final PlotPanel parent)
	{

		super(true);


		this.controller = controller;
		this.setMinimumSize(new Dimension(100, 100));
		this.setFocusable(false);
		
		//setCanvasSize();

		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{
				// TODO Auto-generated method stub
				setCanvasSize();
			}
		});

		//can't accept drag'n'drop if we're in a webstart session, since
		//there are security restrictions
		if (!Env.isWebStart())
		{
			
			FileDrop fileDrop = new FileDrop(this, new FileDrop.Listener() {
				public void filesDropped(File[] files)
				{
					
					
					parent.loadFiles(
						Fn.map(files, new FunctionMap<File, AbstractFile>() {
	
							public AbstractFile f(File element)
							{
								return new AbstractFile(element);
							}
						})
					);
					
					
				}
			});
			
		}


	}


	@Override
	public void paintComponent(Graphics g)
	{


		controller.setImageWidth(this.getWidth());
		controller.setImageHeight(this.getHeight());

		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.fillRect(0, 0, (int) controller.getImageWidth(), (int) controller.getImageHeight());

		if (hasData)
		{
			controller.draw(g);
		}

	}


	private void setCanvasSize()
	{
		double parentWidth = 1.0;
		if (this.getParent() != null)
		{
			parentWidth = this.getParent().getWidth();
		}

		int newWidth = (int) (controller.getDataWidth() * controller.getZoom());
		if (newWidth < parentWidth) newWidth = (int) parentWidth;

		this.setPreferredSize(new Dimension(newWidth, 1));

		this.revalidate();

	}


	public Dimension getPreferredScrollableViewportSize()
	{
		// TODO Auto-generated method stub

		return new Dimension(600, 300);
	}


	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub
		return channelWidth(50);
	}


	public boolean getScrollableTracksViewportHeight()
	{
		// TODO Auto-generated method stub
		return true;
	}


	public boolean getScrollableTracksViewportWidth()
	{
		// TODO Auto-generated method stub
		return false;
	}


	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub
		return channelWidth(5);
	}


	public Point getLocationOnScreenOffset(Point offset)
	{

		Point p = new Point(this.getLocation());
		p.x += offset.x;
		p.y += offset.y;

		return p;

	}



	public void setHasData(boolean hasData)
	{
		this.hasData = hasData;
	}


	private int channelWidth(int multiplier)
	{
		return (int) Math.max(1.0f, Math.round(controller.getZoom() * multiplier));
	}



}
