package peakaboo.ui.swing.widgets.toggle;

import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import peakaboo.datatypes.DataTypeFactory;


public class ComplexToggleGroup
{

	protected List<ComplexToggle> buttons;
	
	public ComplexToggleGroup(){
		
		buttons = DataTypeFactory.<ComplexToggle>list();
		
	}
	
	public void setToggled(ComplexToggle c){
		c.getModel().setSelected(true);
	}
	public void setToggled(int buttonNo){
		if (buttonNo >= buttons.size()) return;
		setToggled(buttons.get(buttonNo));
	}
	
	public int getToggledIndex(){
		int toggle = 0;
		for (ComplexToggle c : buttons){
			if ( c.getModel().isSelected() ) return toggle;
			toggle++;
		}
		return -1;
	}
	
	public void registerButton(ComplexToggle b){
		buttons.add(b);
		
		b.addChangeListener(new ChangeListener() {
		
			public void stateChanged(ChangeEvent e)
			{
				ComplexToggle c = (ComplexToggle)e.getSource();
				
				
				ensureSingleSelection:
				if (c.getModel().isSelected()){
					
					for (ComplexToggle c2 : buttons){ if (c2 != c) c2.getModel().setSelected(false); }
					
				} 
				else {
					
					for (ComplexToggle c2 : buttons){ 
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
