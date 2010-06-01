package peakaboo.ui.swing.widgets.listcontrols;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.Spacing;


public abstract class SelectionListControls extends JPanel
{
	
	public SelectionListControls(String name){
		
		super();
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		JButton add = new ImageButton("ok", "OK", "Add Selected " + name);
		add.setMargin(Spacing.iSmall());
		add.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				approve();
			}
		});


		JButton cancel = new ImageButton("cancel", "Cancel");
		cancel.setMargin(Spacing.iSmall());
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
