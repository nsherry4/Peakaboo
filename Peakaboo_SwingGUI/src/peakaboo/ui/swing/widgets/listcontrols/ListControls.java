package peakaboo.ui.swing.widgets.listcontrols;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.ImageButton.Layout;


public abstract class ListControls extends JPanel
{

	private ImageButton add, remove, clear, up, down;
	
	private List<ListControlButton> customButtons;
	
	public enum ElementCount{NONE, ONE, MANY}
	
	
	public ListControls(String[] tooltips){
		
		customButtons = DataTypeFactory.<ListControlButton>list();
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		String tooltip;
		int tooltipCount = 0;
		tooltip = (tooltips != null && tooltips.length > tooltipCount) ? tooltips[tooltipCount] : "";
		add = new ImageButton("add", "Add", tooltip, Layout.IMAGE);
		add.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				add();
			}
		});

		tooltipCount++;
		tooltip = (tooltips != null && tooltips.length > tooltipCount) ? tooltips[tooltipCount] : "";
		remove = new ImageButton("remove", "Remove", tooltip, Layout.IMAGE);
		remove.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		
		tooltipCount++;
		tooltip = (tooltips != null && tooltips.length > tooltipCount) ? tooltips[tooltipCount] : "";
		clear = new ImageButton("clear", "Clear", tooltip, Layout.IMAGE);
		clear.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		
		tooltipCount++;
		tooltip = (tooltips != null && tooltips.length > tooltipCount) ? tooltips[tooltipCount] : "";
		up = new ImageButton("go-up", "Up", tooltip, Layout.IMAGE);
		
		tooltipCount++;
		tooltip = (tooltips != null && tooltips.length > tooltipCount) ? tooltips[tooltipCount] : "";
		down = new ImageButton("go-down", "Down", tooltip, Layout.IMAGE);
		
		
		up.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				up();
				
			}
		});
		
		down.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				down();
			}
		});
		
		
		add(  add  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  remove  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  clear  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  Box.createHorizontalGlue()  );
		add(  up  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  down  );
		
		
		setBorder(Spacing.bSmall());
		
		setElementCount(ElementCount.NONE);

	}
	
	public void setElementCount(ElementCount ec){
		
		if ( ec == ElementCount.NONE ){
			
			add.setEnabled(true);
			remove.setEnabled(false);
			clear.setEnabled(false);
			up.setEnabled(false);
			down.setEnabled(false);
			
		} else if ( ec == ElementCount.ONE ){
		
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			up.setEnabled(false);
			down.setEnabled(false);
			
		} else {
		
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			up.setEnabled(true);
			down.setEnabled(true);
			
		}
		
		for (ListControlButton button : customButtons)
		{
			button.setEnableState(ec);
		}
		
	}
	
	protected abstract void add();
	protected abstract void remove();
	protected abstract void clear();
	protected abstract void up();
	protected abstract void down();
	
	
	public void addButton(ListControlButton button, int index)
	{
				
		add(button, index);
		add(  Box.createRigidArea(new Dimension(5,0)), index+1 );		
		
		button.setEnableState(ElementCount.NONE);
		
		customButtons.add(button);
	}
	
}
