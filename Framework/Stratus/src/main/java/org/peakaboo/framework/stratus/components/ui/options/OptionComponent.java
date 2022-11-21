package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public abstract class OptionComponent extends ClearPanel {

	protected int padding = OptionSize.MEDIUM.getPaddingSize();
	protected float radius = 6;
	protected Color bg = Color.WHITE;
	protected Color fg = Color.BLACK;
	protected Color fgDisabled = Color.GRAY;
	protected Color borderAlpha = Color.LIGHT_GRAY;
	protected Color border = Color.LIGHT_GRAY;
	protected Color selectionBg = Color.LIGHT_GRAY;
	protected Color selectionFg = Color.BLACK;
	
	public OptionComponent() {
		Theme theme = Stratus.getTheme();
		radius = theme.borderRadius();
		bg = theme.getRecessedControl();
		fg = theme.getRecessedText();
		fgDisabled = theme.getControlTextDisabled();
		borderAlpha = theme.getWidgetBorderAlpha();
		border = theme.getWidgetBorder();
		selectionBg = theme.getHighlight();
		selectionFg = theme.getHighlightText();
	}
	
	@Override
	public void paintBorder(Graphics g) {
		
		g = g.create();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		paintBackground(g2);
		g2.dispose();
		
		super.paintBorder(g);
		
	}
	
	protected abstract void paintBackground(Graphics2D g);
	
	
}
