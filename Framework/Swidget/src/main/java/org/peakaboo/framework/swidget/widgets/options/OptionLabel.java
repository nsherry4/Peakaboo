package org.peakaboo.framework.swidget.widgets.options;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class OptionLabel extends OptionComponent {

	private final int LINE_SPACING = 2;
	
	public OptionLabel(String title, String description) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(lblTitle.getFont().deriveFont(14f).deriveFont(Font.PLAIN));		
		lblTitle.setForeground(fg);
		this.add(lblTitle);
		
		this.add(Box.createVerticalStrut(LINE_SPACING));
		
		JLabel lblDesc = new JLabel(description);
		lblDesc.setFont(lblDesc.getFont().deriveFont(11f).deriveFont(Font.PLAIN));
		lblDesc.setForeground(fgDisabled);
		this.add(lblDesc);
		
		Dimension tps = lblTitle.getPreferredSize();
		Dimension dps = lblDesc.getPreferredSize();
		Dimension ps = new Dimension(Math.max(tps.width, dps.width), tps.height + dps.height + LINE_SPACING);
		
		setPreferredSize(ps);
		setMaximumSize(ps);
		setMinimumSize(ps);
		
		
	}

	@Override
	protected void paintBackground(Graphics2D g) {
		// Nothing to paint
	}
		
}
