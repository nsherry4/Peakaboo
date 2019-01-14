package peakaboo.ui.swing.plotting.fitting;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import eventful.EventfulListener;
import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonLayout;


public abstract class TSSelectorGroup extends JPanel implements Scrollable
{

	protected FittingController controller;

	protected List<TSSelector>	selectors;
	protected ImageButton		addButton;
	
	private int					minSelectors;
	
	public TSSelectorGroup(FittingController controller, int minimumSelectors)
	{
		this.controller = controller;
		this.minSelectors = minimumSelectors;
		
		setLayout(new GridBagLayout());
		
		addButton = new ImageButton()
				.withIcon(StockIcon.EDIT_ADD, IconSize.BUTTON)
				.withTooltip("Add")
				.withLayout(ImageButtonLayout.IMAGE)
				.withBordered(false);
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				addTSSelector(true);
				addButton.requestFocusInWindow();
			}
		});
		
				
		selectors = new ArrayList<TSSelector>();
		
		
	}
	
	
	protected abstract void refreshGUI();
	public abstract void setTransitionSeriesOptions(final List<ITransitionSeries> tss);
	public abstract List<ITransitionSeries> getTransitionSeries();
	
	protected void removeTSSelector(TSSelector tssel)
	{
		selectors.remove(tssel);
		if (selectors.size() < minSelectors) addTSSelector(true);
		refreshGUI();
	}

	
	protected TSSelector addTSSelector(final boolean active)
	{
		
		TSSelector sel = new TSSelector();
		
		sel.addListener(new EventfulListener() {
			
			public void change()
			{
				TSSelectorUpdated(active);
			}
		});
		
		selectors.add(sel);
		
		refreshGUI();
		
		return sel;
	}
	
	
	
	protected ImageButton createRemoveButton(final TSSelector tss)
	{
		ImageButton remove = new ImageButton()
				.withIcon(StockIcon.EDIT_REMOVE, IconSize.BUTTON)
				.withTooltip("Remove")
				.withLayout(ImageButtonLayout.IMAGE)
				.withBordered(false);

		remove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				removeTSSelector(tss);
			}
		});

		return remove;
	}
	
	
	public final void resetSelectors(boolean active)
	{
		selectors.clear();
		
		for (int i = 0; i < minSelectors; i++)
		{
			addTSSelector(active);	
		}
		

	}
	
	protected final void TSSelectorUpdated(final boolean active)
	{
		controller.clearProposedTransitionSeries();
		List<ITransitionSeries> tss = getTransitionSeries();
		if (tss == null) return;
		
		//add all of the transition series that come back from the summation widget
		tss.stream().forEach(ts -> {
			if (ts != null && active) controller.addProposedTransitionSeries(ts);
		});
		
	}

	
	/* SCROLLABLE METHODS */

	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}


	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		TSSelector s = selectors.get(0);
		if (s == null) return 4;
		return s.getPreferredSize().height;
	}


	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}


	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}


	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		TSSelector s = selectors.get(0);
		if (s == null) return 1;
		return s.getPreferredSize().height / 4;
	}

	
	
}
