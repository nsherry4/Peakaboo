package swidget.widgets.listcontrols;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;



public abstract class SelectionListControls extends JPanel
{
	
	public SelectionListControls(String name){
		
		super();
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		ImageButton add = new ImageButton("OK", StockIcon.CHOOSE_OK).withTooltip("Add Selected " + name).withBordered(false);
		add.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				approve();
			}
		});


		ImageButton cancel = new ImageButton("Cancel", StockIcon.CHOOSE_CANCEL).withTooltip("Discard Selections").withBordered(false);
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				cancel();
			}
		});


		add(add);
		add(cancel);

		setBorder(Spacing.bSmall());

	}
	
	protected abstract void approve();
	protected abstract void cancel();
	
}
