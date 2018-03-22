package stratus.theme;

import java.awt.Color;

import stratus.Stratus;

public class LightTheme implements Theme {

	private Color highlight = new Color(0x498ed8);
	private Color control = new Color(0xe9e9e9);
	private Color controlText = new Color(0x202020);
	private Color controlTextDisabled = new Color(0x999999);
	private Color border = new Color(0xBABABA);
	
	private Color menuControl = new Color(0xffffff);
	private Color menuControlText = controlText;
	
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
	public Color getBorder() {
		return border;
	}

	@Override
	public Color getMenuControl() {
		return menuControl;
	}

	@Override
	public Color getMenuControlText() {
		return menuControlText;
	}

	@Override
	public Color getMenuControlTextSelected() {
		return Color.WHITE;
	}

	
	@Override
	public Color getWidget() {
		return Stratus.darken(getControl(), 0.02f);
	}

	@Override
	public Color getWidgetBevel() {
		return Stratus.lighten(getWidget(), 0.2f);
	}

	@Override
	public Color getTextControl() {
		return Color.WHITE;
	}

	@Override
	public Color getTextText() {
		return controlText;
	}

	@Override
	public Color getTableHeader() {
		return Color.WHITE;
	}

	@Override
	public Color getTableHeaderText() {
		return Stratus.darken(getControlTextDisabled(), 0.25f);
	}

	@Override
	public Color getScrollHandle() {
		return Stratus.darken(getBorder(), 0.1f);
	}

	
	
}
