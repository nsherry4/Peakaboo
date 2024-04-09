package org.peakaboo.ui.swing.plotting.fitting.auto;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.ui.swing.plotting.fitting.TSSelector;
import org.peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;

public class AutoFittingWidget extends TSSelectorGroup {

	public AutoFittingWidget(FittingController controller) {
		super(controller, 0);		
	}

	@Override
	protected void refreshGUI() {

		removeAll();

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(Spacing.tiny, 0, Spacing.tiny, 0);
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
			var button = createRemoveButton(selector);
			add(button, c);


		}

		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(new ClearPanel(), c);

		revalidate();
		
	}

	TSSelector addTSSelector(ITransitionSeries ts) {
		TSSelector sel = super.addTSSelector(true);
		sel.setTransitionSeries(List.of(ts));
		return sel;
	}

	
	@Override
	public void setTransitionSeriesOptions(List<ITransitionSeries> tss) {
		// NOOP
	}

	@Override
	public List<ITransitionSeries> getTransitionSeries() {
		return selectors.stream().map(TSSelector::getTransitionSeries).toList();
	}
	
	protected void removeTSSelector(TSSelector tssel) {
		tssel.setTransitionSeries(null);
		super.removeTSSelector(tssel);
	}

}
