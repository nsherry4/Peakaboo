package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.GridBagConstraints;
import java.util.List;

import peakaboo.common.DataTypeFactory;
import peakaboo.curvefit.controller.IFittingController;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSSelector;
import peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;
import swidget.widgets.ClearPanel;
import fava.Functions;
import fava.functionable.FList;

import java.util.function.Function;
import java.util.function.Predicate;



class SummationWidget extends TSSelectorGroup
{

	private SummationPanel parent;
	
	public SummationWidget(IFittingController controller, SummationPanel parent)
	{

		super(controller, 2);
		this.parent = parent;
		resetSelectors(true);
		refreshGUI();
		
	}
	
	

	
	
	@Override
	public FList<TransitionSeries> getTransitionSeries()
	{

		//get a list of all TransitionSeries to be summed
		FList<TransitionSeries> tss = selectors.map(new Function<TSSelector, TransitionSeries>() {

			public TransitionSeries apply(TSSelector element)
			{
				return element.getTransitionSeries();
			}
		}).filter(ts -> ts != null);
		
		
		return DataTypeFactory.<TransitionSeries>listInit(TransitionSeries.summation(tss));

	}
	


	@Override
	public void setTransitionSeriesOptions(final List<TransitionSeries> tss)
	{
		selectors.each((TSSelector selector) -> {
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
			controller.getFittedTransitionSeries().filter(element ->element.mode == TransitionSeriesMode.PRIMARY)			
		);
		
		
		return sel;
				
		
	}



}

