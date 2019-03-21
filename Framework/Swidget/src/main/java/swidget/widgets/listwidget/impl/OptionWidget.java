package swidget.widgets.listwidget.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JLabel;

import swidget.Swidget;
import swidget.widgets.JTextLabel;
import swidget.widgets.Spacing;
import swidget.widgets.listwidget.ListWidget;

/**
 * Widget used to show one of several options in a list
 * @author NAS 2018
  */

public class OptionWidget<T> extends ListWidget<T>{

	private JTextLabel lblTitle, lblBody;
	private JLabel lblIcon;
	private Function<T, String> fnTitle; 
	private Function<T, String> fnBody;
	private Function<T, Icon> fnIcon;
	
		
	public OptionWidget(Function<T, String> fnTitle, Function<T, String> fnBody, Function<T, Icon> fnIcon) {
		lblTitle = new JTextLabel();
		lblBody = new JTextLabel();
		lblIcon = new JLabel();
		
		lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD).deriveFont(lblTitle.getFont().getSize() + 4f));
		
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = Spacing.iSmall();
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		add(lblTitle, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(lblBody, c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		add(lblIcon, c);
		
		this.fnTitle = fnTitle;
		this.fnBody = fnBody;
		this.fnIcon = fnIcon;
		
		setBorder(Spacing.bLarge());
		
	}
	
	@Override
	protected void onSetValue(T value) {
		lblTitle.setText(fnTitle.apply(value));
		lblBody.setText(fnBody.apply(value));
		lblIcon.setIcon(fnIcon.apply(value));
		
		if (lblIcon.getIcon() == null) {
			remove(lblIcon);
		} else if (!Arrays.asList(this.getComponents()).contains(lblIcon)) {
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.NORTHWEST;
			c.fill = GridBagConstraints.NONE;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 0;
			c.weighty = 1;
			c.insets = Spacing.iTiny();
			add(lblIcon, c);
		}
		
	}
	
	
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		if (lblTitle != null) lblTitle.setForeground(color);
		if (lblBody != null) lblBody.setForeground(color);
	}
	
	

	
}
