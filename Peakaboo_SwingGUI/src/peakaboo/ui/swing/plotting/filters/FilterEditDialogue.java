package peakaboo.ui.swing.plotting.filters;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;


import eventful.EventfulListener;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import scidraw.drawing.backends.graphics2d.ImageBuffer;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.WrappingLabel;
import swidget.widgets.ImageButton.Layout;


public class FilterEditDialogue extends JDialog
{

	protected IFilteringController	controller;
	protected AbstractFilter	filter;


	public FilterEditDialogue(AbstractFilter _filter, IFilteringController _controller, JFrame owner)
	{

		super(owner, _filter.getPluginName(), false);
		init(_filter, _controller, owner);

	}

	private void init(AbstractFilter _filter, IFilteringController _controller, Window owner){
		
		this.controller = _controller;
		this.filter = _filter;



		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		SingleFilterView view = new SingleFilterView(filter, controller);
		c.add(view, BorderLayout.CENTER);


		c.add(createButtonBox(filter), BorderLayout.SOUTH);

		controller.addListener(new EventfulListener() {

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
	
	
	private JPanel createButtonBox(final AbstractFilter f)
	{
				
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
		close.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				FilterEditDialogue.this.setVisible(false);
				FilterEditDialogue.this.dispose();
			}
		});
		
		
		ImageButton info = new ImageButton(StockIcon.BADGE_HELP, "Filter Information", Layout.IMAGE, true);
		info.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				JDialog infodialog = new JDialog(FilterEditDialogue.this, true);
				WrappingLabel infotext = new WrappingLabel(f.getPluginDescription(), 400);
				infotext.setBorder(Spacing.bMedium());
				infodialog.getContentPane().add(infotext);
				infodialog.pack();
				infodialog.setLocationRelativeTo(FilterEditDialogue.this);
				infodialog.setVisible(true);
			}
		});

		ButtonBox bbox = new ButtonBox();
		bbox.addLeft(0, info);
		bbox.addRight(0, close);
		
		return bbox;
		
	}
	

}
