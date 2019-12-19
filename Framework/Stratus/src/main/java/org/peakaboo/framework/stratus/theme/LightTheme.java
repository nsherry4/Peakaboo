package org.peakaboo.framework.stratus.theme;

import java.awt.Color;

import org.peakaboo.framework.stratus.Stratus;

public class LightTheme implements Theme {

	private Color highlight = new Color(0x498ed8);
	private Color control = new Color(0xe9e9e9);
	private Color controlText = new Color(0x202020);
	private Color controlTextDisabled = new Color(0x999999);
	private Color border = new Color(0xB7B7B7);
	
	private Color widget = Stratus.darken(getControl(), 0.08f);
	private Color widgetBevel = Stratus.lighten(getWidget(), 0.2f);
	
	private Color dashAlpha = new Color(0x30000000, true);
	private Color borderAlpha = new Color(0x40000000, true);
	private Color shadowAlpha = new Color(0x27000000, true);
	
	private Color menuControl = new Color(0xffffff);
	private Color menuControlText = controlText;
	
	private Color tableHeaderText = new Color(0x666666);
	
	private Color scrollHandle = Stratus.darken(getWidgetBorder(), 0.1f);
	
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
	public Color getWidgetBorderAlpha() {
		return borderAlpha;
	}
	
	@Override
	public Color getWidgetDashAlpha() {
		return dashAlpha;
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
		return Color.WHITE;
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
		return Color.WHITE;
	}

	@Override
	public Color getRecessedText() {
		return controlText;
	}

	@Override
	public Color getTableHeader() {
		return Color.WHITE;
	}

	@Override
	public Color getTableHeaderText() {
		return tableHeaderText;
	}

	@Override
	public Color getScrollHandle() {
		return scrollHandle;
	}

	@Override
	public Color getShadow() {
		return shadowAlpha;
	}

	
	@Override
	public float widgetCurve() {
		return 0.03f;
	}
	
	
}
