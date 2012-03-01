package peakaboo.ui.swing.plotting.filters.settings;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class ParametersPanel extends JPanel implements Scrollable
{


	////////////////////////////////////
	// SCROLLABLE INTERFACE
	////////////////////////////////////
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 50;
	}

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2)
	{
		return 5;
	}
	
}
