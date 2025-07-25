package org.peakaboo.framework.stratus.laf.theme;

import java.awt.Color;
import java.awt.Font;

import org.peakaboo.framework.stratus.api.ColourPalette;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.palettes.BrightPalette;

public class DuskTheme implements Theme {

	protected static final ColourPalette PALETTE = new BrightPalette();
	protected Accent accent;
	
	protected Color highlight = PALETTE.getColour("Blue", "3");
	private Color highlightText = PALETTE.getColour("Light", "1");
	
	private Color control = new Color(0x484848);
	private Color controlText = new Color(0xd0d0d0);
	private Color controlTextDisabled = new Color(0x808080);
	private Color negative = new Color(0x383838);
	
	private Color border = new Color(0x202020);
	private Color borderAlpha = new Color(0x30ffffff, true);
	
	private Color widget = new Color(0x404040);
	private Color widgetAlpha = new Color(0x13ffffff, true);
	private Color widgetBevel = new Color(0x202020);
	
	private Color selectionAlpha = new Color(0x3f498ed8, true);
	private Color shadowAlpha = new Color(0x27000000, true);
	
	private Color recessedComponent = new Color(0x303030);
	
	private Color menuControl = new Color(0x202020);
	private Color menuControlText = controlText;
	
	private Color tableHeaderText = new Color(0x666666);
	
	private Color scrollHandle = StratusColour.lighten(getWidgetBorder(), 0.1f);
	
	public DuskTheme() {
		this(Accent.BLUE);
	}
	
	public DuskTheme(String accentName) {
		this(Accent.forName(accentName));
	}
	
	public DuskTheme(Accent accent) {
		this.accent = accent;
		this.highlight = getAccent(accent);
	}
	
	@Override
	public ColourPalette getPalette() {
		return PALETTE;
	}
	
	@Override
	public Color getAccent(Accent accent) {
			
		return switch (accent) {
			case BLUE -> new Color(0x5080df);
			case GREEN -> new Color(0x4c8e4b);
			case GREY -> new Color(0x728194);
			case ORANGE -> new Color(0xdc6823);
			case PINK -> new Color(0xc76996);
			case PURPLE -> new Color(0x8e47ab);
			case RED -> new Color(0xd64848);
			case TEAL -> new Color(0x3e8889);
			case YELLOW -> new Color(0xbd8b25);
			
			default -> getAccent(Accent.BLUE);
		};
		
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
	
	@Override
	public Font getMonospaceFont() {
		return new Font("JetBrains Mono Medium", Font.PLAIN, 11);
	}
	
}
