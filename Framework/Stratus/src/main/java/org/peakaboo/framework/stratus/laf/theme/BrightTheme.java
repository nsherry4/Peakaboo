package org.peakaboo.framework.stratus.laf.theme;

import java.awt.Color;

import org.peakaboo.framework.stratus.api.ColourPalette;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.palettes.BrightPalette;

public class BrightTheme implements Theme {

	protected ColourPalette palette = new BrightPalette();
	
	protected Color highlight = palette.getColour("Blue", "3");
	protected Color highlightText = palette.getColour("Light", "1");
	
	protected Color control = new Color(0xfafafa);
	protected Color controlText = palette.getColour("Dark", "3");
	protected Color controlTextDisabled = palette.getColour("Light", "5");
	protected Color negative = new Color(0xebebeb);
	protected Color border = new Color(0xdadada);
	
	protected Color widget = new Color(0xe6e6e6);
	protected Color widgetAlpha = new Color(0x13000000, true);
	protected Color widgetBevel = widget;
	
	protected Color selectionAlpha = new Color(0x3f498ed8, true);
	protected Color borderAlpha = new Color(0x30000000, true);
	protected Color shadowAlpha = new Color(0x27000000, true);
	
	protected Color recessedComponent = new Color(0xffffff);
	
	protected Color menuControl = new Color(0xffffff);
	protected Color menuControlText = controlText;
	
	protected Color tableHeaderText = new Color(0x666666);
	
	protected Color scrollHandle = StratusColour.darken(getWidgetBorder(), 0.1f);
	
	@Override
	public ColourPalette getPalette() {
		return palette;
	}
	
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
