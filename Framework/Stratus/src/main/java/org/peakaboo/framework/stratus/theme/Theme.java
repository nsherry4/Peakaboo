package org.peakaboo.framework.stratus.theme;

import java.awt.Color;

import org.peakaboo.framework.stratus.Stratus;

public interface Theme {

	//Highlighted items/text, eg menu items, text, table entries
	public Color getHighlight();
	public Color getHighlightText();
	
	public Color getControl();
	public Color getControlText();
	public Color getControlTextDisabled();
	
	public Color getMenu();
	public Color getMenuText();
	
	//raised widgets like buttons, checkboxes, etc...
	public Color getWidget();
	public Color getWidgetBevel();
	public Color getWidgetBorder();
	
	//sunken widgets like text fields, tables, progress bars...
	public Color getRecessedControl();
	public Color getRecessedText();
	
	public Color getTableHeader();
	public Color getTableHeaderText();
	
	public Color getScrollHandle();
	
	Color getWidgetBorderAlpha();
	Color getWidgetDashAlpha();
	Color getShadow();
	
	/**
	 * Colour for control components representing negative space around widgets (eg
	 * toolbars, headers, etc). This is not the same as large blank spaces to
	 * indicate a lack of data/contents
	 */
	default Color getNegative() {
		return getControl();
	}
	
	/**
	 * Strength of gradient on widget surfaces, generally the stronger the gradient,
	 * the stronger the curve appearance
	 */
	default float widgetCurve() {
		return 0.06f;
	}
	
	/**
	 * Strength of the bevel highlight
	 */
	default float bevelStrength() {
		return 0.1f;
	}
	
	/**
	 * Strength of the border outline in circumstances where the border is being
	 * applied to a non-standard colour
	 */
	default float borderStrength() {
		return 0.1f;
	}
	 
	
	
	
	
}
