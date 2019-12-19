package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.widgets.Spacing;

public class PropertyPanel extends JPanel {

	private int minWidth = 0;;
	private Map<String, String> properties = new HashMap<>();
	private boolean centered = false;
	private boolean rightAlignedLabels = true;
	private boolean verticalCenterd = true;
	private GridBagConstraints c;
	
	public PropertyPanel() {
		make();
	}

	public PropertyPanel(Map<String, String> properties) {
		this(properties, false, 255);
	}
	
	public PropertyPanel(Map<String, String> properties, boolean centered, int minWidth) {
		this(properties, centered, minWidth, true);
	}
	
	public PropertyPanel(Map<String, String> properties, boolean centered, int minWidth, boolean rightAlignedLabels) {
		this.minWidth = minWidth;
		this.centered = centered;
		this.properties = properties;
		this.rightAlignedLabels = rightAlignedLabels;
		make();
	}
	
	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		make();
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
		make();
	}

	public boolean isCentered() {
		return centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
		make();
	}
	
	
	public boolean isRightAlignedLabels() {
		return rightAlignedLabels;
	}

	public void setRightAlignedLabels(boolean rightAlignedLabels) {
		this.rightAlignedLabels = rightAlignedLabels;
		make();
	}

	
	
	public boolean isVerticalCenterd() {
		return verticalCenterd;
	}

	public void setVerticalCenterd(boolean verticalCenterd) {
		this.verticalCenterd = verticalCenterd;
		make();
	}

	private void make() {
				
		removeAll();
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.ipadx = 8;
		c.ipady = 8;
		c.gridx = 0;
		c.gridy = 0;
		
		for (Entry<String, String> property : properties.entrySet())
		{
			addJLabelPair(property.getKey(), property.getValue());
		}

		if (!verticalCenterd) {
			c.gridwidth = 2;
			c.weighty = 1d;
			c.fill = GridBagConstraints.BOTH;
			add(Box.createVerticalGlue(), c);
			c.gridy++;
		}
		
		this.repaint();
		
	}
	
	

	private void addJLabelPair(String one, String two)
	{
		
		JLabel label;
		
		
		c.gridx = 0;
		c.weightx = 0;
		c.fill = (centered ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
		c.anchor = rightAlignedLabels ? GridBagConstraints.NORTHEAST : GridBagConstraints.NORTHWEST;
		
		label = new JLabel(one);
		label.setForeground(Color.GRAY);
		add(label, c);
		
		
		
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		label = new JLabel(two);
		if (!centered && minWidth > 0) label.setPreferredSize(new Dimension(minWidth, label.getPreferredSize().height));
		label.setToolTipText(two);
		label.setBorder(new EmptyBorder(0, Spacing.large, 0, 0));
		add(label, c);
		
		c.gridy += 1;
		c.gridx = 0;
		
	}
	
	
	
}
