package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Container;

import eventful.EventfulTypeListener;

import peakaboo.controller.plotter.FilterController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.filter.AbstractFilter;
import swidget.containers.SwidgetContainer;
import swidget.containers.SwidgetDialog;
import swidget.containers.SwidgetFrame;

public class FilterEditDialogue extends SwidgetDialog
{

	protected FilterController	controller;
	protected AbstractFilter	filter;


	public FilterEditDialogue(AbstractFilter _filter, FilterController _controller, SwidgetFrame owner)
	{

		super(owner, _filter.getFilterName(), false);
		init(_filter, _controller);

	}
	public FilterEditDialogue(AbstractFilter _filter, FilterController _controller, SwidgetContainer owner)
	{

		super(owner, _filter.getFilterName());
		init(_filter, _controller);
	}
	
	private void init(AbstractFilter _filter, FilterController _controller){
		
		this.controller = _controller;
		this.filter = _filter;



		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		SingleFilterView view = new SingleFilterView(_filter, controller);
		c.add(view, BorderLayout.CENTER);



		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{
				// TODO Auto-generated method stub
				if (!controller.filterSetContains(filter)) {
					setVisible(false);
				}
				pack();
			}
		});

		centreOnParent();
		setTitle("Filter Settings");
		setResizable(false);
		pack();
		setVisible(true);
	}

}
