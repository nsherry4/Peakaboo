package org.peakaboo.ui.swing.plotting.fitting;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;


public abstract class TSSelectorGroup extends JPanel implements Scrollable {

	protected FittingController controller;

	protected List<TSSelector>	selectors;
	protected FluentButton		addButton;
	
	private int					minSelectors;
	
	public TSSelectorGroup(FittingController controller, int minimumSelectors) {
		this.controller = controller;
		this.minSelectors = minimumSelectors;
		
		setLayout(new GridBagLayout());
		
		addButton = new FluentButton()
				.withIcon(StockIcon.EDIT_ADD, IconSize.BUTTON)
				.withTooltip("Add")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false);
		
		addButton.withAction(() -> {
			addTSSelector(true);
			addButton.requestFocusInWindow();
		});
		
				
		selectors = new ArrayList<>();
		
		
	}
	
	
	protected abstract void refreshGUI();
	public abstract void setTransitionSeriesOptions(final List<ITransitionSeries> tss);
	public abstract List<ITransitionSeries> getTransitionSeries();
	
	protected void removeTSSelector(TSSelector tssel) {
		selectors.remove(tssel);
		if (selectors.size() < minSelectors) addTSSelector(true);
		refreshGUI();
	}

	
	protected TSSelector addTSSelector(final boolean active) {
		
		TSSelector sel = new TSSelector();
		sel.addListener(() -> tsSelectorUpdated(active));
		selectors.add(sel);
		
		refreshGUI();
		
		return sel;
	}
	
	
	
	protected FluentButton createRemoveButton(final TSSelector tss) {
		return new FluentButton()
				.withIcon(StockIcon.EDIT_REMOVE, IconSize.BUTTON)
				.withTooltip("Remove")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false)
				.withAction(() -> removeTSSelector(tss));

	}
	
	
	public final void resetSelectors(boolean active) {
		selectors.clear();
		
		for (int i = 0; i < minSelectors; i++) {
			addTSSelector(active);	
		}
		

	}
	
	protected final void tsSelectorUpdated(final boolean active) {
		controller.clearProposedTransitionSeries();
		List<ITransitionSeries> tss = getTransitionSeries();
		if (tss == null) { return; }
		
		//add all of the transition series that come back from the summation widget
		tss.stream().forEach(ts -> {
			if (ts != null && active) controller.addProposedTransitionSeries(ts);
		});
		
	}

	
	/* SCROLLABLE METHODS */

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}


	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		TSSelector s = selectors.get(0);
		if (s == null) return 4;
		return s.getPreferredSize().height;
	}


	public boolean getScrollableTracksViewportHeight() {
		return false;
	}


	public boolean getScrollableTracksViewportWidth() {
		return true;
	}


	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		TSSelector s = selectors.get(0);
		if (s == null) return 1;
		return s.getPreferredSize().height / 4;
	}
	
}
