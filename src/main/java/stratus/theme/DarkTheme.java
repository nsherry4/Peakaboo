package stratus.theme;

import java.awt.Color;

import stratus.Stratus;

public class DarkTheme implements Theme {

	private Color highlight = new Color(0x498ed8);
	private Color control = new Color(0x3B4141);
	private Color controlText = new Color(0xffffff);
	private Color border = new Color(0x212424);
	
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
		return border;
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
		return menuControlText;
	}


	@Override
	public Color getWidget() {
		return Stratus.darken(getControl(), 0.02f);
	}


	@Override
	public Color getWidgetBevel() {
		return Stratus.lighten(getControl(), 0.1f);
	}

	@Override
	public Color getTextControl() {
		return Stratus.darken(getControl(), 0.05f);
	}

	@Override
	public Color getTextText() {
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


	
	
}
