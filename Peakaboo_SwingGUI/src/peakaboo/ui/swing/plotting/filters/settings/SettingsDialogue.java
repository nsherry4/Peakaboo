package peakaboo.ui.swing.plotting.filters.settings;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import eventful.EventfulListener;

import peakaboo.controller.plotter.filtering.IFilteringController;
import peakaboo.filter.AbstractFilter;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.TextWrapping;
import swidget.widgets.ImageButton.Layout;


public class SettingsDialogue extends JDialog
{

	protected IFilteringController	controller;
	protected AbstractFilter	filter;
	

	public SettingsDialogue(AbstractFilter _filter, IFilteringController _controller, JFrame owner)
	{

		super(owner, _filter.getFilterName(), false);
		init(_filter, _controller, owner);

	}

	private void init(AbstractFilter _filter, IFilteringController _controller, Window owner){
		
		this.controller = _controller;
		this.filter = _filter;

		


		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		SingleFilterView view = new SingleFilterView(filter, controller);
		
		JScrollPane scroller = new JScrollPane(view);
		scroller.setBorder(Spacing.bNone());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		c.add(scroller, BorderLayout.CENTER);


		c.add(createButtonBox(filter), BorderLayout.SOUTH);

		controller.addListener(new EventfulListener() {

			public void change()
			{
				if (!controller.filterSetContains(filter)) {
					setVisible(false);
				}
			}
		});

		setLocationRelativeTo(owner);
		setTitle(_filter.getFilterName() + " Filter Settings");
		
		pack();
		setVisible(true);
	}
	
	
	private JPanel createButtonBox(final AbstractFilter f)
	{
				
		ImageButton close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", true);
		close.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				SettingsDialogue.this.setVisible(false);
				SettingsDialogue.this.dispose();
			}
		});
		
		
		ImageButton info = new ImageButton(StockIcon.BADGE_HELP, "Filter Information", Layout.IMAGE, true);
		info.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{	
				JOptionPane.showMessageDialog(
						SettingsDialogue.this, 
						TextWrapping.wrapTextForMultiline(f.getFilterDescription()),
						f.getFilterName() + " Filter Information", 
						JOptionPane.INFORMATION_MESSAGE, 
						StockIcon.BADGE_HELP.toImageIcon(IconSize.ICON)
					);

			}
		});

		ButtonBox bbox = new ButtonBox();
		bbox.addLeft(0, info);
		bbox.addRight(0, close);
		
		return bbox;
		
	}
	

}
