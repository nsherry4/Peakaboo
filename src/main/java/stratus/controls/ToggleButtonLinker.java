package stratus.controls;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

public class ToggleButtonLinker extends JComponent {

	public ToggleButtonLinker(AbstractButton... buttons) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		for (AbstractButton button : buttons) {
			add(button, c);
			c.gridx++;
		}
	}
	
	public String getUIClassID() {
		return "StratusButtonLinkerUI";
	}
	
}
