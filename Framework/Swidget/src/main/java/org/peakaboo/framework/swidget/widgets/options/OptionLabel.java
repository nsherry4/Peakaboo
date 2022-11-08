package org.peakaboo.framework.swidget.widgets.options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

import javax.lang.model.type.NullType;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class OptionLabel extends OptionComponent implements OptionFluentAPI<NullType> {

	private final int LINE_SPACING = 2;
	private JLabel lblDesc;
	private JLabel lblTitle;
		
	public enum TextSize {
		SMALL, 
		MEDIUM, 
		LARGE
		
		;

		float getTitleSize() {
			return switch(this) {
				case SMALL -> 10;
				case MEDIUM -> 12;
				case LARGE -> 14;
			};
		}
		
		float getDescriptionSize() {
			return switch(this) {
				case SMALL -> 8;
				case MEDIUM -> 10;
				case LARGE -> 11;
			};
		}
		
	}
	
	public OptionLabel(String title, String description) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblTitle = new JLabel(title);
		lblTitle.setForeground(fg);
		this.add(lblTitle);
		
		this.add(Box.createVerticalStrut(LINE_SPACING));
		
		lblDesc = new JLabel(description);
		lblDesc.setForeground(fgDisabled);
		this.add(lblDesc);
		
		this.setTextSize(TextSize.MEDIUM);
		
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
	


	public void setTextSize(TextSize size) {
		lblTitle.setFont(lblTitle.getFont().deriveFont(size.getTitleSize()).deriveFont(Font.PLAIN));
		lblDesc.setFont(lblDesc.getFont().deriveFont(size.getDescriptionSize()).deriveFont(Font.PLAIN));
	}

	@Override
	public OptionFluentAPI<NullType> withDescription(String description) {
		lblDesc.setText(description);
		setDimensions();
		return this;
	}

	@Override
	public OptionFluentAPI<NullType> withTooltip(String tooltip) {
		this.setToolTipText(tooltip);
		return this;
	}

	@Override
	public OptionFluentAPI<NullType> withTitle(String title) {
		lblTitle.setText(title);
		setDimensions();
		return this;
	}

	@Override
	public OptionFluentAPI<NullType> withTextSize(TextSize size) {
		this.setTextSize(size);
		setDimensions();
		return this;
	}


	@Override
	public OptionFluentAPI<NullType> withText(String title, String description) {
		lblTitle.setText(title);
		lblDesc.setText(description);
		setDimensions();
		return this;
	}


		
}


