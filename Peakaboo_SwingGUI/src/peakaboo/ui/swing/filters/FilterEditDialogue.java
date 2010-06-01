package peakaboo.ui.swing.filters;


import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;

import peakaboo.controller.plotter.FilterController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.filters.AbstractFilter;

public class FilterEditDialogue extends JDialog
{

	protected FilterController	controller;
	protected AbstractFilter	filter;


	public FilterEditDialogue(AbstractFilter _filter, FilterController _controller, JFrame owner)
	{

		super(owner);
		
		this.controller = _controller;
		this.filter = _filter;



		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		SingleFilterView view = new SingleFilterView(_filter, controller);
		c.add(view, BorderLayout.CENTER);



		controller.addListener(new PeakabooSimpleListener() {

			public void change()
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
