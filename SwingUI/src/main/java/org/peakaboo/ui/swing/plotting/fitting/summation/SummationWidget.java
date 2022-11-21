package org.peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionSeriesMode;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.ui.swing.plotting.fitting.TSSelector;
import org.peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;



class SummationWidget extends TSSelectorGroup {

	private SummationPanel summationPanel;
	
	public SummationWidget(FittingController controller, SummationPanel parent) {
		super(controller, 2);
		this.summationPanel = parent;
		resetSelectors(true);
		refreshGUI();		
	}
		
	@Override
	public List<ITransitionSeries> getTransitionSeries() {
		//get a list of all TransitionSeries to be summed
		List<ITransitionSeries> tss = selectors.stream()
				.map(TSSelector::getTransitionSeries)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		List<ITransitionSeries> sum = new ArrayList<>();
		sum.add(ITransitionSeries.pileup(tss));
		return sum;
	}
	


	@Override
	public void setTransitionSeriesOptions(final List<ITransitionSeries> tss) {
		selectors.stream().forEach(selector -> selector.setTransitionSeries(tss));
	}
	

	@Override
	protected void refreshGUI() {

		removeAll();

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;

		for (TSSelector selector : selectors)
		{
			c.gridy += 1;

			c.gridx = 0;
			c.weightx = 1.0;
			add(selector, c);

			c.gridx = 1;
			c.weightx = 0.0;
			add(createRemoveButton(selector), c);


		}

		c.gridy++;
		c.gridx = 1;
		add(addButton, c);

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(new ClearPanel(), c);

		revalidate();

		if (summationPanel.active) tsSelectorUpdated(summationPanel.active);


	}
	
	
	
	@Override
	protected TSSelector addTSSelector(boolean active) {	
		TSSelector sel = super.addTSSelector(active);
		
		sel.setTransitionSeries(
			controller.getFittedTransitionSeries().stream().filter(element ->element.getMode() == TransitionSeriesMode.PRIMARY).collect(Collectors.toList())		
		);
		
		return sel;
		
	}

}
