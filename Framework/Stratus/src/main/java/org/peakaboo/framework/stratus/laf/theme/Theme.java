package org.peakaboo.framework.stratus.laf.theme;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peakaboo.framework.stratus.api.ColourPalette;
import org.peakaboo.framework.stratus.api.HSLColor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusColour;

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
	
	public Color getWidgetAlpha();
	
	Color getWidgetBorderAlpha();
	Color getWidgetSelectionAlpha();
	Color getShadow();
	
	ColourPalette getPalette();

	Font getMonospaceFont();

	/**
	 * The accent colours a user can choose between. These are mid-tone by design so
	 * that they carry white text and sit legibly on both light and dark controls,
	 * which is why both themes share one table.
	 */
	default Color getAccent(Accent accent) {
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
		};
	}

	default Map<Accent, Color> getAccents() {
		var map = new LinkedHashMap<Accent, Color>();
		Arrays.stream(Accent.values()).forEach(accent -> map.put(accent, getAccent(accent)));
		return map; 
	}
	
	default String getColourAccentName(Color color) {
		return getAccents().entrySet().stream()
				.filter(entry -> entry.getValue().equals(color))
				.map(Map.Entry::getKey)
				.map(Accent::toString)
				.findFirst()
				.orElse("Unknown");
	}
	
	/**
	 * Colour for control components representing negative space around widgets (eg
	 * toolbars, headers, etc). This is not the same as large blank spaces to
	 * indicate a lack of data/contents
	 */
	default Color getNegative() {
		return getControl();
	}

	/**
	 * Is this a dark theme? Derived from the control colour rather than declared, so
	 * that any theme gets a sensible answer without having to remember to override it.
	 */
	default boolean isDark() {
		return new HSLColor(getControl()).getLuminance() < 50;
	}

	/**
	 * Move a colour away from this theme's background, so it stands out more. Use this
	 * instead of picking lighten/darken by hand -- "darken to emphasize" is only true
	 * on a light theme.
	 */
	default Color emphasize(Color src, float amount) {
		return isDark() ? StratusColour.lighten(src, amount) : StratusColour.darken(src, amount);
	}

	/**
	 * Move a colour toward this theme's background, so it recedes. The opposite of
	 * {@link #emphasize(Color, float)}.
	 */
	default Color recede(Color src, float amount) {
		return isDark() ? StratusColour.darken(src, amount) : StratusColour.lighten(src, amount);
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
	
	
	/**
	 * Radius of various rounded corners for widgets
	 */
	default float borderRadius() {
		return Spacing.medium;
	}
	
	default float selectionStrength() {
		return 0.05f;
	}
	
	default boolean isFlat() {
		return false;
	}
	
	default int widgetMargins() {
		return 1;
	}
	

	public enum Accent {
		RED, ORANGE, YELLOW, GREEN, TEAL, BLUE, PURPLE, PINK, GREY;
		
		// Name with capitalized first letter
		@Override
		public String toString() {
			var name = this.name().toLowerCase();
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
			return name;
		}
		
		// Safe version of valueOf which handles capitalization and fallback value
		public static Accent forName(String accentName) {
			try {
				return Accent.valueOf(accentName.toUpperCase());
			} catch (IllegalArgumentException e) {
				return Accent.BLUE;
			}
		}
	}
	
	
	
	
}
