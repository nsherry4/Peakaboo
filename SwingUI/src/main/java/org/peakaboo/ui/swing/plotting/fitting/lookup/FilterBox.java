package org.peakaboo.ui.swing.plotting.fitting.lookup;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.Spacing;

public class FilterBox extends JTextField {

	private ImageIcon icon = IconFactory.getImageIcon("filter", IconSize.BUTTON);
	
	public FilterBox() {
		super();
		
		
		UIDefaults dialogTheme = new UIDefaults();
		Painter<FilterBox> painter = new Painter<FilterBox>() {

			@Override
			public void paint(Graphics2D g, FilterBox object, int width, int height) {
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
				
			}
		};
		dialogTheme.put("TextField[Enabled].backgroundPainter", painter);
		dialogTheme.put("TextField[Focused].backgroundPainter", painter);
		
		this.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
        this.putClientProperty("Nimbus.Overrides", dialogTheme);
		
		
		this.setBorder(Spacing.bLarge());
		this.setBackground(Color.GRAY);
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		paintIcon(g);
		paintText(g);
	}

	private void paintIcon(Graphics g) {
		int iconWidth = icon.getIconWidth();
		int iconHeight = icon.getIconHeight();
		int x = Spacing.large;
		int y = (this.getHeight() - iconHeight) / 2;
		icon.paintIcon(this, g, x, y);

		setBorder(new EmptyBorder(Spacing.medium, Spacing.large * 2 + iconWidth, Spacing.medium, Spacing.large));
	}

	private void paintText(Graphics g) {
		if (!this.hasFocus() && this.getText().equals("")) {
			int width = this.getWidth();
			int height = this.getHeight();
			Font prev = g.getFont();
			Font italic = prev.deriveFont(Font.ITALIC);
			Color prevColor = g.getColor();
			g.setFont(italic);
			g.setColor(UIManager.getColor("textInactiveText"));
			int h = g.getFontMetrics().getHeight();
			int textBottom = (height - h) / 2 + h - 3;
			int x = this.getInsets().left;
			Graphics2D g2d = (Graphics2D) g;
			RenderingHints hints = g2d.getRenderingHints();
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.drawString("Filter...", x, textBottom);
			g2d.setRenderingHints(hints);
			g.setFont(prev);
			g.setColor(prevColor);
		}
	}

}
