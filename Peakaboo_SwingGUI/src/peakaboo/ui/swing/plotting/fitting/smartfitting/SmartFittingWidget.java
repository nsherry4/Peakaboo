package peakaboo.ui.swing.plotting.fitting.smartfitting;



import java.awt.ActiveEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


import fava.*;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.ui.swing.plotting.PlotCanvas;
import peakaboo.ui.swing.plotting.fitting.TSSelector;
import peakaboo.ui.swing.plotting.fitting.TSSelectorGroup;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;



class SmartFittingWidget extends TSSelectorGroup
{

	private int	activeIndex;


	public SmartFittingWidget(FittingController controller, PlotCanvas canvas)
	{
		super(controller, 1);
				
		resetSelectors();
		activeIndex = 0;

		refreshGUI();

	}


	@Override
	public List<TransitionSeries> getTransitionSeries()
	{
		return Fn.map(selectors, new FunctionMap<TSSelector, TransitionSeries>() {

			public TransitionSeries f(TSSelector selector)
			{
				return selector.getTransitionSeries();
			}
		});
	}

	public TransitionSeries getActiveTransitionSeries()
	{
		return selectors.get(activeIndex).getTransitionSeries();		
	}
	

	@Override
	public void setTransitionSeriesOptions(final List<TransitionSeries> tss)
	{
		selectors.get(activeIndex).setTransitionSeries(tss);
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
			add(createEditButton(selector, i), c);
			
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

		TSSelectorUpdated();


	}

	@Override
	protected void addTSSelector()
	{	
		activeIndex = selectors.size();
		super.addTSSelector();
	}
	
	@Override
	protected void removeTSSelector(TSSelector tssel)
	{
		if (selectors.get(activeIndex) == tssel || activeIndex == selectors.size()-1)
		{
			activeIndex = Math.max(0, selectors.size() - 2);
			System.out.println(activeIndex);
		}
		
		super.removeTSSelector(tssel);
	}

	private ImageButton createEditButton(final TSSelector selector, final int index)
	{

		ImageButton edit = new ImageButton(
			StockIcon.EDIT_EDIT,
			"Edit",
			"Edit this fitting",
			Layout.IMAGE,
			IconSize.BUTTON);

		edit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				activeIndex = index;
				disableAllSelectors();
				selector.setEnabled(true);
			}
		});

		return edit;

	}


	private void disableAllSelectors()
	{
		Fn.each(selectors, new FunctionEach<TSSelector>() {

			public void f(TSSelector selector)
			{
				selector.setEnabled(false);
			}
		});
	}



}
