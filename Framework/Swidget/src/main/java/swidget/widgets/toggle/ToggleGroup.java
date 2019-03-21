package swidget.widgets.toggle;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;




public class ToggleGroup
{

	protected List<JToggleButton> buttons;
	
	public ToggleGroup(){
		buttons = new ArrayList<JToggleButton>();
	}
	
	public void setToggled(JToggleButton c){
		c.getModel().setSelected(true);
	}
	public void setToggled(int buttonNo){
		if (buttonNo >= buttons.size()) return;
		setToggled(buttons.get(buttonNo));
	}
	
	public int getToggledIndex(){
		int toggle = 0;
		for (JToggleButton c : buttons){
			if ( c.getModel().isSelected() ) return toggle;
			toggle++;
		}
		return -1;
	}
	
	public void registerButton(JToggleButton b){
		buttons.add(b);
		
		b.addChangeListener(new ChangeListener() {
		
			public void stateChanged(ChangeEvent e)
			{
				JToggleButton c = (JToggleButton)e.getSource();
				
				
				ensureSingleSelection:
				if (c.getModel().isSelected()){
					
					for (JToggleButton c2 : buttons){ if (c2 != c) c2.getModel().setSelected(false); }
					
				} 
				else {
					
					for (JToggleButton c2 : buttons){ 
						if (c2 != c && c2.getModel().isSelected()) {
							break ensureSingleSelection;
						}
					}
					
					c.getModel().setSelected(true);
					
				}
			}
			
		});
		
	}
	
}
