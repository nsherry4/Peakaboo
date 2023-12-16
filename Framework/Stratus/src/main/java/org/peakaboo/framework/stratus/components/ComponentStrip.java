package org.peakaboo.framework.stratus.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Spacing;

public class ComponentStrip extends JComponent {

	private GridBagConstraints c = new GridBagConstraints();
	
	public ComponentStrip(JComponent... components) {
		this(Arrays.asList(components));
	}
	
	public ComponentStrip(List<JComponent> components) {
		this(components, false);
	}
	
	public ComponentStrip(List<JComponent> components, boolean fillVertical) {
		this(components, fillVertical, Spacing.iNone(), 0);
	}
	
	public ComponentStrip(List<JComponent> components, boolean fillVertical, Insets spacing, int padding) {
		setLayout(new GridBagLayout());
		setOpaque(false);
		c.fill = fillVertical ? GridBagConstraints.VERTICAL : GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = padding;
		c.insets = spacing;
		for (JComponent component : components) {
			addButton(component);
		}
	}
	
	public void addButton(JComponent button) {
		add(button, c);
		c.gridx++;
	}
	
}
