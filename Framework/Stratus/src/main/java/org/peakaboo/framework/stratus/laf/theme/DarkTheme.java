package org.peakaboo.framework.stratus.laf.theme;

import java.awt.Color;
import java.awt.Paint;

import org.peakaboo.framework.stratus.api.StratusColour;

public class DarkTheme implements Theme {

	private Color highlight = new Color(0x498ed8);
	private Color control = new Color(0x3B4141);
	private Color controlText = new Color(0xffffff);
	private Color controlTextDisabled = new Color(0x666666);
	private Color border = new Color(0x212424);
	
	private Color dashAlpha = new Color(0x30000000, true);
	private Color borderAlpha = new Color(0x40000000, true);
	private Color shadowAlpha = new Color(0x1C000000, true);
	
	private Color widget = StratusColour.darken(control, 0.08f);
	private Color widgetBevel = StratusColour.lighten(control, 0.1f);
	
	private Color textControl = StratusColour.darken(control, 0.05f);
	
	private Color menuControl = control;
	private Color menuControlText = controlText;
	
	private Color scrollHandle = new Color(0x6F7372);
	
	@Override
	public Color getHighlight() {
		return highlight;
	}
	
	@Override
	public Color getControl() {
		return control;
	}
	
	@Override
	public Color getControlText() {
		return controlText;
	}
	
	@Override
	public Color getControlTextDisabled() {
		return controlTextDisabled;
	}
	
	@Override
	public Color getWidgetBorder() {
		return border;
	}
	
	
	@Override
	public Color getMenu() {
		return menuControl;
	}

	@Override
	public Color getMenuText() {
		return menuControlText;
	}
	
	@Override
	public Color getHighlightText() {
		return menuControlText;
	}

	@Override
	public Color getWidget() {
		return widget;
	}

	@Override
	public Color getWidgetBevel() {
		return widgetBevel;
	}

	@Override
	public Color getRecessedControl() {
		return textControl;
	}

	@Override
	public Color getRecessedText() {
		return controlText;
	}

	@Override
	public Color getTableHeader() {
		return control;
	}

	@Override
	public Color getTableHeaderText() {
		return controlText;
	}

	@Override
	public Color getScrollHandle() {
		return scrollHandle;
	}

	@Override
	public Color getWidgetBorderAlpha() {
		return borderAlpha;
	}

	@Override
	public Color getWidgetSelectionAlpha() {
		return dashAlpha;
	}

	
	@Override
	public Color getWidgetAlpha() {
		return widget;
	}

	
	@Override
	public Color getShadow() {
		return shadowAlpha;
	}


	
	
}
