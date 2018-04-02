package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.transition.TransitionSeries;
import peakaboo.curvefit.transition.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSSelector;
import peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;
import swidget.widgets.ClearPanel;



class SummationWidget extends TSSelectorGroup
{

	private SummationPanel parent;
	
	public SummationWidget(FittingController controller, SummationPanel parent)
	{

		super(controller, 2);
		this.parent = parent;
		resetSelectors(true);
		refreshGUI();
		
	}
	
	

	
	
	@Override
	public List<TransitionSeries> getTransitionSeries()
	{
		//get a list of all TransitionSeries to be summed
		List<TransitionSeries> tss = selectors.stream().map(e -> e.getTransitionSeries()).filter(ts -> ts != null).collect(Collectors.toList());
		List<TransitionSeries> sum = new ArrayList<>();
		sum.add(TransitionSeries.summation(tss));
		return sum;
	}
	


	@Override
	public void setTransitionSeriesOptions(final List<TransitionSeries> tss)
	{
		selectors.stream().forEach((TSSelector selector) -> {
			selector.setTransitionSeries(tss);
		});
	}
	

	@Override
	protected void refreshGUI()
	{

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

		if (parent.active) TSSelectorUpdated(parent.active);


	}
	
	
	
	@Override
	protected TSSelector addTSSelector(boolean active)
	{	
		TSSelector sel = super.addTSSelector(active);
		
		sel.setTransitionSeries(
			controller.getFittedTransitionSeries().stream().filter(element ->element.mode == TransitionSeriesMode.PRIMARY).collect(Collectors.toList())		
		);
		
		
		return sel;
				
		
	}



}

