package org.peakaboo.ui.swing.plotting.fitting.guidedfitting;



import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.ui.swing.plotting.fitting.TSSelector;
import org.peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;



class GuidedFittingWidget extends TSSelectorGroup {

	private int	activeIndex;
	
	private List<FluentButton> editButtons;


	public GuidedFittingWidget(FittingController controller) {
		super(controller, 1);
		
		editButtons = new ArrayList<>();
		
		clearSelectors(true);
		activeIndex = 0;

		refreshGUI();
		
	}
	

	@Override
	public List<ITransitionSeries> getTransitionSeries() {
		return selectors.stream().map(TSSelector::getTransitionSeries).toList();
	}

	public ITransitionSeries getActiveTransitionSeries() {
		return selectors.get(activeIndex).getTransitionSeries();		
	}
	

	@Override
	public void setTransitionSeriesOptions(final List<ITransitionSeries> tss) {
		selectors.get(activeIndex).setTransitionSeries(tss);
	}
	
	
	@Override
	protected void refreshGUI() {

		removeAll();

		editButtons.clear();
		
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(Spacing.tiny, 0, Spacing.tiny, 0);
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;

		TSSelector selector;
		for (int i = 0; i < selectors.size(); i++)
		{
			
			selector = selectors.get(i);
			
			c.gridy += 1;

			c.gridx = 0;
			c.weightx = 1.0;
			selector.setEnabled(i == activeIndex);
			add(selector, c);

			c.gridx = 1;
			c.weightx = 0.0;
			FluentButton edit = createEditButton(selector, i);
			editButtons.add(edit);
			edit.setEnabled(i != activeIndex);
			add(edit, c);
			
			c.gridx = 2;
			c.weightx = 0.0;
			add(createRemoveButton(selector), c);

		}

		c.gridy++;
		c.gridx = 2;
		add(addButton, c);
		
		

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(new ClearPanel(), c);

		
		revalidate();
		
		
		tsSelectorUpdated(true);


	}

	@Override
	protected TSSelector addTSSelector(boolean active) {
		activeIndex = selectors.size();
		return super.addTSSelector(active);
	}
	
	@Override
	protected void removeTSSelector(TSSelector tssel) {
		if (selectors.get(activeIndex) == tssel || activeIndex == selectors.size()-1)
		{
			activeIndex = Math.max(0, selectors.size() - 2);
		}
		
		super.removeTSSelector(tssel);
	}

	private FluentButton createEditButton(final TSSelector selector, final int index) {

		final FluentButton edit = new FluentButton(StockIcon.EDIT_EDIT, IconSize.BUTTON)
				.withTooltip("Edit this fitting")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false);

		edit.withAction(() -> {
			activeIndex = index;
			disableAllSelectors();
			selector.setEnabled(true);
			edit.setEnabled(false);
		});

		return edit;

	}


	private void disableAllSelectors() {
		for (TSSelector selector : selectors)
		{
			selector.setEnabled(false);
		}
		
		for (FluentButton edit : editButtons)
		{
			edit.setEnabled(true);
		}
		
	}



}
