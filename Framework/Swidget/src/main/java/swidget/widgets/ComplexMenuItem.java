package swidget.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ComplexMenuItem extends JPanel {
	
	private JMenuItem template = new JMenuItem("Text");


	public ComplexMenuItem(String title, Component control) {
		this(title, control, false);
	}
	
	public ComplexMenuItem(JLabel title, Component control) {
		this(title, control, false);
	}
	
	public ComplexMenuItem(String title, Component control, boolean expand) {
		this(new JLabel(title), control, expand);
	}
	
	public ComplexMenuItem(JLabel title, Component control, boolean expand) {
		setOpaque(false);
		
		//The goal here is to generate a border with insets such that the total menu 
		//item height matches as closely as possible to the real menu item height.
		int templateHeight = (int) template.getPreferredSize().getHeight() - template.getInsets().bottom - template.getInsets().top;
		int controlHeight = (int) control.getPreferredSize().getHeight();
		
		int deltaHeight = controlHeight - templateHeight;
		
		Insets templateInsets = template.getInsets();
		int insetTop = templateInsets.top - (int) Math.floor(deltaHeight / 2.0);
		int insetBottom = templateInsets.bottom - (int) Math.ceil(deltaHeight / 2.0);
		Insets controlInsets = new Insets(
				Math.max(0, insetTop), 
				templateInsets.left, 
				Math.max(0, insetBottom), 
				templateInsets.right
			);
		setBorder(new EmptyBorder(controlInsets));
		
		//If the top/bottom insets are negative, it means that the control is too 
		//large to fit the standard menu item height. If expand is false, hammer
		//the component into the right height
		if (insetTop + insetBottom < 0 && !expand) {
			control.setPreferredSize(new Dimension(
				(int)control.getPreferredSize().getWidth(), 
				(int)control.getPreferredSize().getHeight() + insetTop + insetBottom
			));
		}
		
		
		setLayout(new BorderLayout(Spacing.medium, Spacing.medium));
		add(title, BorderLayout.CENTER);
		add(control, BorderLayout.EAST);
	}
	
}
