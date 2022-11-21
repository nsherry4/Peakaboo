package org.peakaboo.framework.stratus.components.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.components.layouts.CenteringLayout;

public class BlankMessagePanel extends JPanel {

	public BlankMessagePanel(String title, String message) {
		setBorder(Spacing.bHuge());
		
		JLabel lblMessage = new JLabel();
		lblMessage.setText(StratusText.lineWrapHTML(lblMessage, message, 350));
		lblMessage.setForeground(Color.GRAY);

		JLabel lblTitle = new JLabel();
		lblTitle.setText(StratusText.lineWrapHTML(lblTitle, title, 350));
		lblTitle.setForeground(Color.GRAY);
		lblTitle.setFont(lblTitle.getFont().deriveFont(18f));
		lblTitle.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel container = new JPanel(new BorderLayout(Spacing.medium, Spacing.medium));
		container.setOpaque(false);
		container.add(lblMessage, BorderLayout.CENTER);
		container.add(lblTitle, BorderLayout.NORTH);
		
		setLayout(new CenteringLayout());
		add(container);
	}
	
	public BlankMessagePanel(String message) {
		setBorder(Spacing.bHuge());
		
		JLabel label = new JLabel();
		label.setText(StratusText.lineWrapHTML(label, message, 350));
		label.setForeground(Color.GRAY);
		
		setLayout(new CenteringLayout());
		add(label);
	}
	
}
