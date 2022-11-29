package org.peakaboo.framework.stratus.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Spacing;

public class ButtonLinker extends JComponent {

	private GridBagConstraints c = new GridBagConstraints();
	
	
	
	public ButtonLinker(AbstractButton... buttons) {
		this(Arrays.asList(buttons));
	}
	
	public ButtonLinker(List<AbstractButton> buttons) {
		this(buttons, false);
	}
	
	public ButtonLinker(List<AbstractButton> buttons, boolean fillVertical) {
		this(buttons, fillVertical, Spacing.iNone());
	}
	
	public ButtonLinker(List<AbstractButton> buttons, boolean fillVertical, Insets spacing) {
		setLayout(new GridBagLayout());
		setOpaque(false);
		c.fill = fillVertical ? GridBagConstraints.VERTICAL : GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = spacing;
		for (AbstractButton button : buttons) {
			addButton(button);
		}
	}
	
	public void addButton(AbstractButton button) {
		add(button, c);
		c.gridx++;
	}
	
	public String getUIClassID() {
		return "StratusButtonLinkerUI";
	}
	
}
