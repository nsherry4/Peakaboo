package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.GridBagConstraints;
import java.util.List;

import fava.*;
import fava.functionable.FList;
import fava.signatures.FnCondition;
import fava.signatures.FnEach;
import fava.signatures.FnMap;

import peakaboo.common.DataTypeFactory;
import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.peaktable.TransitionSeriesMode;
import peakaboo.ui.swing.plotting.fitting.TSSelector;
import peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;
import swidget.widgets.ClearPanel;



class SummationWidget extends TSSelectorGroup
{


	public SummationWidget(IFittingController controller)
	{

		super(controller, 2);
		resetSelectors();
		refreshGUI();
		
	}
	
	

	
	
	@Override
	public FList<TransitionSeries> getTransitionSeries()
	{

		//get a list of all TransitionSeries to be summed
		FList<TransitionSeries> tss = selectors.map(new FnMap<TSSelector, TransitionSeries>() {

			public TransitionSeries f(TSSelector element)
			{
				return element.getTransitionSeries();
			}
		}).filter(Functions.<TransitionSeries>notNull());
		
		
		return DataTypeFactory.<TransitionSeries>listInit(TransitionSeries.summation(tss));

	}
	


	@Override
	public void setTransitionSeriesOptions(final List<TransitionSeries> tss)
	{
		selectors.each(new FnEach<TSSelector>() {

			public void f(TSSelector selector)
			{
				selector.setTransitionSeries(tss);
			}
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

		TSSelectorUpdated();


	}
	
	
	
	@Override
	protected TSSelector addTSSelector()
	{	
		TSSelector sel = super.addTSSelector();
		
		sel.setTransitionSeries(
		
				controller.getFittedTransitionSeries().filter(new FnCondition<TransitionSeries>() {

					public Boolean f(TransitionSeries element)
					{
						return element.mode == TransitionSeriesMode.PRIMARY;
					}
				})
				
		);
		
		
		return sel;
				
		
	}



}

