package org.peakaboo.framework.swidget.widgets.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.widgets.Spacing;

public class SettingsPanel extends JPanel {

	
	public enum LabelPosition {
		BESIDE,
		ABOVE,
		NONE
	}
	
	private GridBagConstraints c = new GridBagConstraints();
	private Insets insets = new Insets(0, 0, 0, 0);
	
	public SettingsPanel() {
		this(new Insets(0, 0, 0, 0));
	}
	
	public SettingsPanel(Insets insets) {
		setLayout(new GridBagLayout());
		
		c.insets = Spacing.iTiny();
		c.ipadx = 0;
		c.ipady = 0;
		
		this.insets = insets;
		
	}
	
	public void addSetting(Component component) {
		addSetting(component, "", LabelPosition.NONE, false, false);
	}
	
	

	public void addSetting(Component component, String label) {
		addSetting(component, label, LabelPosition.BESIDE);
	}
	
	public void addSetting(Component component, Component label) {
		addSetting(component, label, LabelPosition.BESIDE);
	}
	
	public void addSetting(Component component, String label, LabelPosition labelPosition) {
		addSetting(component, makeLabel(label), labelPosition);
	}
	
	public void addSetting(Component component, Component label, LabelPosition labelPosition) {
		addSetting(component, label, labelPosition, false, false);
	}
	
	
	
	public void addSetting(Component component, String label, LabelPosition labelPosition, boolean vFill, boolean hFill) {
		addSetting(component, makeLabel(label), labelPosition, vFill, hFill);
	}
	
	public void addSetting(Component component, Component label, LabelPosition labelPosition, boolean vFill, boolean hFill) {
		
		
		
		
		c.weighty = vFill ? 1f : 0f;
		c.weightx = hFill ? 1f : 0f;
		c.gridy += 1;
		c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		
		c.anchor = GridBagConstraints.LINE_START;

		if (labelPosition == LabelPosition.BESIDE)
		{
			c.weightx = 1;
			add(wrap(label), c);
			
			if (hFill && vFill) { c.fill = GridBagConstraints.BOTH;}
			else if (vFill) {c.fill = GridBagConstraints.VERTICAL;}
			else if (hFill) { c.fill = GridBagConstraints.HORIZONTAL;}
			else { c.fill = GridBagConstraints.NONE; }

			c.weightx = hFill ? 1f : 0f;
			c.gridx++;
			c.anchor = GridBagConstraints.LINE_END;
			
			add(wrap(component), c);
			
		}
		else if (labelPosition == LabelPosition.ABOVE)
		{
			c.gridwidth = 2;
			
			c.weighty = 0f;
			add(wrap(label), c);

			c.gridy++;
			
			c.weighty = vFill ? 1f : 0f;
			add(wrap(component), c);
			
			c.gridwidth = 1;
		}
		else if(labelPosition == LabelPosition.NONE)
		{
			c.gridwidth = 2;				
			add(wrap(component), c);
			c.gridwidth = 1;
		}
		
	}
	
	private JPanel wrap(Component c) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(c, BorderLayout.CENTER);
		wrapper.setBorder(new EmptyBorder(this.insets));
		return wrapper;
	}
	
	private JLabel makeLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}
	
	
}
