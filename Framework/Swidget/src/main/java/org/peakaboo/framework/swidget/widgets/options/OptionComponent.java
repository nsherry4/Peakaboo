package org.peakaboo.framework.swidget.widgets.options;

import java.awt.Color;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.theme.Theme;
import org.peakaboo.framework.swidget.Swidget;

public abstract class OptionComponent extends PaintedComponent {

	protected int padding = 10;
	protected float radius = 6;
	protected Color bg = Color.WHITE;
	protected Color fg = Color.BLACK;
	protected Color fgDisabled = Color.GRAY;
	protected Color borderAlpha = Color.LIGHT_GRAY;
	protected Color border = Color.LIGHT_GRAY;
	protected Color selectionBg = Color.LIGHT_GRAY;
	protected Color selectionFg = Color.BLACK;
	
	public OptionComponent() {
		if (Swidget.isStratusLaF()) {
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
	}
	
	
}
