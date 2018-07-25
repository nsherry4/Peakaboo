package peakaboo.ui.swing.plotting.fitting.lookup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.Position.Bias;

import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;

public class SearchBox extends JTextField {

	private ImageIcon icon = StockIcon.FIND.toImageIcon(IconSize.BUTTON);
		
	public SearchBox() {
		super();
		
		
		UIDefaults dialogTheme = new UIDefaults();
		Painter<SearchBox> painter = new Painter<SearchBox>() {

			@Override
			public void paint(Graphics2D g, SearchBox object, int width, int height) {
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
			g2d.drawString("Search", x, textBottom);
			g2d.setRenderingHints(hints);
			g.setFont(prev);
			g.setColor(prevColor);
		}
	}

}
