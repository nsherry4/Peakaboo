package org.peakaboo.framework.stratus.components.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.text.Format;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;

public class KeyValuePill extends JPanel {

	private JLabel key, value;
	private int padOuter, padInner;
	private Format format;
	
	private Color originalBackground = null;
	
	public KeyValuePill(String title) {
		this(title, null, -1);
	}
	
	public KeyValuePill(String title, int characters) {
		this(title, null, characters);
	}
	
	public KeyValuePill(String title, Format format, int characters) {
		this.format = format;
		
		this.padOuter = Spacing.huge;
		this.padInner = Spacing.small;
		
		this.key = new JLabel(title);
		this.key.setFont(this.key.getFont().deriveFont(Font.BOLD).deriveFont(11f));

		setForeground(this.getForeground());
		
		//This needs to be final for the value label inner method, but we also need to set it after defining the value label
		int[] width = new int[1];
		
		this.value = new JLabel("") {
			@Override
			public Dimension getPreferredSize() {
				int sh = Math.max(24, super.getPreferredSize().height);
				int sw = super.getPreferredSize().width;
				if (characters == -1) {
					return new Dimension(sw, sh);
				} else {
					return new Dimension(Math.max(width[0], sw), sh);
				}
			}
			
			//For some reason, boxlayout only looks at maximum?
			@Override
			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}
		};

		Font font = Stratus.getTheme().getMonospaceFont();
		this.value.setFont(font);
		this.value.setHorizontalAlignment(SwingConstants.RIGHT);
				
		//Measure the width of a character in the value label to determine proper width
		if (characters > 0) {
			this.value.setText("0".repeat(characters));
			width[0] = this.value.getPreferredSize().width;
			this.value.setText("");
		}
		
		
		this.setBorder(new EmptyBorder(0, padOuter, 0, padOuter));
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.add(key);
		this.add(Box.createHorizontalStrut(Spacing.huge+Spacing.small));
		this.add(value);
		
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = Stratus.g2d(g);
		g2d = (Graphics2D) g2d.create();
		
		Color bg = Stratus.getTheme().getWidgetAlpha();
		if (originalBackground != null) {
			// A custom background colour has been set
			float alpha = 1f - (bg.getAlpha() / 255f);
			// We skimp on the transparency a bit -- the background colours tend 
			// to be a bit lighter than the semi-transparent black we use normally
			bg = StratusColour.moreTransparent(this.getBackground(), alpha * 0.93f);
		}
		int r = (int)(getHeight() / 2f);

		g2d.setColor(bg);
		g2d.fillRoundRect(padInner, padInner, getWidth()-padInner*2, getHeight()-padInner*2, r, r);		
		
		g2d.setColor(bg);
		g2d.fillRoundRect(padInner, padInner, (int)(key.getWidth() + padOuter * 1.5f), getHeight()-padInner*2, r, r);
		
		
	}

	public void setKey(String key) {
		this.key.setText(key);
	}
	
	public void setValue(String value) {
		this.value.setText(value);
	}

	public void setValue(int value) {
		String text;
		if (this.format != null) {
			text = format.format(value);
		} else {
			text = Integer.toString(value);
		}
		this.value.setText(text);
	}

	public void setValue(float value) {
		String text;
		if (this.format != null) {
			text = format.format(value);
		} else {
			text = Float.toString(value);
		}
		this.value.setText(text);
	}
	
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		if (this.key != null) this.key.setForeground(color);
		if (this.value != null) this.value.setForeground(color);
	}
	
	@Override
	public void setBackground(Color color) {
		originalBackground = this.getBackground();
		super.setBackground(color);
		if (this.key != null) this.key.setBackground(color);
		if (this.value != null) this.value.setBackground(color);
	}

	public String getValue() {
		return this.value.getText();
	}
	
}
