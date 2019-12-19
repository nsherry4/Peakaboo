package org.peakaboo.ui.swing.plotting.fitting.lookup;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;


public class LookupWidget extends ClearPanel {
	private JPanel elementContents;
	private JLabel elementName;
	private JCheckBox elementCheck;
	
	public LookupWidget() {
		super();
		
		setLayout(new BorderLayout(Spacing.small, Spacing.small));
		
		elementContents = new JPanel(new BorderLayout()); elementContents.setOpaque(false);
		
		elementName = new JLabel("");
		
		elementContents.add(elementName, BorderLayout.CENTER);
		elementContents.setBorder(Spacing.bSmall());

		add(elementContents, BorderLayout.CENTER);
		elementCheck = new JCheckBox(); elementCheck.setOpaque(false);
				
		elementName.setOpaque(false);
		add(elementCheck, BorderLayout.WEST);		
		
	}
	
	public boolean isSelected() {
		return elementCheck.isSelected();
	}
	
	public void setSelected(boolean selected) {
		elementCheck.setSelected(selected);
	}

	@Override
	public void setName(String title) {
		elementName.setText(title);
	}
		
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (elementName != null) elementName.setForeground(c);
	}
	
	public JCheckBox getCheckBox() {
		return elementCheck;
	}
		
	
}