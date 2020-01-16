package org.peakaboo.framework.swidget.widgets;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.widgets.layout.CenteringLayout;

public class BlankMessagePanel extends JPanel {

	public BlankMessagePanel(String title, String message) {
		setBorder(Spacing.bHuge());
		
		JLabel lblMessage = new JLabel();
		lblMessage.setText(Swidget.lineWrapHTML(lblMessage, message, 350));
		lblMessage.setForeground(Color.GRAY);

		JLabel lblTitle = new JLabel();
		lblTitle.setText(Swidget.lineWrapHTML(lblTitle, title, 350));
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
		label.setText(Swidget.lineWrapHTML(label, message, 350));
		label.setForeground(Color.GRAY);
		
		setLayout(new CenteringLayout());
		add(label);
	}
	
}
