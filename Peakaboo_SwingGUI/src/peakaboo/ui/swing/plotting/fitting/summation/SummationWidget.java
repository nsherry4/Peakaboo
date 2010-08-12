package peakaboo.ui.swing.plotting.fitting.summation;



import java.awt.GridBagConstraints;
import java.util.List;

import fava.*;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;
import static fava.Fn.*;

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
	public List<TransitionSeries> getTransitionSeries()
	{

		//get a list of all TransitionSeries to be summed
		List<TransitionSeries> tss = filter(map(selectors, new FunctionMap<TSSelector, TransitionSeries>() {

			public TransitionSeries f(TSSelector element)
			{
				return element.getTransitionSeries();
			}
		}), Functions.<TransitionSeries>notNull());
		
		
		return DataTypeFactory.<TransitionSeries>listInit(TransitionSeries.summation(tss));

	}
	


	@Override
	public void setTransitionSeriesOptions(final List<TransitionSeries> tss)
	{
		Fn.each(selectors, new FunctionEach<TSSelector>() {

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
		
				filter(controller.getFittedTransitionSeries(), new FunctionMap<TransitionSeries, Boolean>() {

					public Boolean f(TransitionSeries element)
					{
						return element.mode == TransitionSeriesMode.PRIMARY;
					}
				})
				
		);
		
		
		return sel;
				
		
	}



}

