package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.peakaboo.framework.stratus.api.StratusColour;

public class OptionLabel extends OptionComponent implements OptionFluentAPI {

	private final int LINE_SPACING = 2;
	private JLabel lblDesc;
	private JLabel lblTitle;
		
	public OptionLabel(String title, String description) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblTitle = new JLabel(title);
		lblTitle.setForeground(fg);
		this.add(lblTitle);
		
		this.add(Box.createVerticalStrut(LINE_SPACING));
		
		lblDesc = new JLabel(description);
		lblDesc.setForeground(StratusColour.moreTransparent(fg, 0.3f));
		this.add(lblDesc);
		
		this.withSize(OptionSize.MEDIUM);
		
	}

	@Override
	protected void paintBackground(Graphics2D g) {
		// Nothing to do
	}
	
	private void setDimensions() {
		Dimension tps = lblTitle.getPreferredSize();
		Dimension dps = lblDesc.getPreferredSize();
		Dimension ps = new Dimension(Math.max(tps.width, dps.width), tps.height + dps.height + LINE_SPACING);
		
		setPreferredSize(ps);
		setMaximumSize(ps);
		setMinimumSize(ps);
	}
	


	public void setTextSize(OptionSize size) {
		lblTitle.setFont(lblTitle.getFont().deriveFont((float)size.getTitleSize()));
		lblDesc.setFont(lblDesc.getFont().deriveFont((float)size.getDescriptionSize()));
	}

	@Override
	public OptionLabel withDescription(String description) {
		lblDesc.setText(description);
		setDimensions();
		return this;
	}

	@Override
	public OptionLabel withTooltip(String tooltip) {
		this.setToolTipText(tooltip);
		return this;
	}

	@Override
	public OptionLabel withTitle(String title) {
		lblTitle.setText(title);
		setDimensions();
		return this;
	}

	@Override
	public OptionLabel withSize(OptionSize size) {
		this.setTextSize(size);
		setDimensions();
		return this;
	}


	@Override
	public OptionLabel withText(String title, String description) {
		lblTitle.setText(title);
		lblDesc.setText(description);
		setDimensions();
		return this;
	}


		
}


