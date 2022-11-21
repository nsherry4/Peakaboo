package org.peakaboo.framework.stratus.laf.theme;

import java.awt.Color;

import org.peakaboo.framework.stratus.api.StratusColour;

public class BrightTheme implements Theme {

	private Color highlight = new Color(0x498ed8);
	private Color highlightText = new Color(0xffffff);
	
	private Color control = new Color(0xfafafa);
	private Color controlText = new Color(0x323232);
	private Color controlTextDisabled = new Color(0x969696);
	private Color negative = new Color(0xebebeb);
	private Color border = new Color(0xdadada);
	
	private Color widget = new Color(0xe6e6e6);
	private Color widgetAlpha = new Color(0x13000000, true);
	private Color widgetBevel = new Color(0xe6e6e6);
	
	private Color selectionAlpha = new Color(0x3f498ed8, true);
	private Color borderAlpha = new Color(0x30000000, true);
	private Color shadowAlpha = new Color(0x27000000, true);
	
	private Color recessedComponent = new Color(0xffffff);
	
	private Color menuControl = new Color(0xffffff);
	private Color menuControlText = controlText;
	
	private Color tableHeaderText = new Color(0x666666);
	
	private Color scrollHandle = StratusColour.darken(getWidgetBorder(), 0.1f);
	
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
	public Color getNegative() {
		return negative;
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
	public Color getWidgetSelectionAlpha() {
		return selectionAlpha;
	}
	
	@Override
	public Color getWidgetAlpha() {
		return widgetAlpha;
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
		return highlightText;
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
		return recessedComponent;
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
		return 0.0f;
	}
	
	@Override
	public float borderRadius() {
		return 12f;
	}
	
	@Override
	public boolean isFlat() {
		return true;
	}
	
	@Override
	public int widgetMargins() {
		return 0;
	}
}
