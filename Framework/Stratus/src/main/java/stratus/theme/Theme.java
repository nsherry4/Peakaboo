package stratus.theme;

import java.awt.Color;

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
	
	
	
	
	
}
