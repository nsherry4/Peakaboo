package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import eventful.EventfulTypeListener;

import peakaboo.controller.plotter.FilterController;
import peakaboo.filter.AbstractFilter;


public class FilterEditDialogue extends JDialog
{

	protected FilterController	controller;
	protected AbstractFilter	filter;


	public FilterEditDialogue(AbstractFilter _filter, FilterController _controller, JFrame owner)
	{

		super(owner, _filter.getFilterName(), false);
		init(_filter, _controller, owner);

	}

	private void init(AbstractFilter _filter, FilterController _controller, Window owner){
		
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

		setLocationRelativeTo(owner);
		setTitle("Filter Settings");
		setResizable(false);
		pack();
		setVisible(true);
	}

}
