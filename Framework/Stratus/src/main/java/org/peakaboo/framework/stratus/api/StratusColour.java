package org.peakaboo.framework.stratus.api;

import java.awt.Color;

public class StratusColour {

	public static Color lighten(Color src) {
		return StratusColour.lighten(src, 0.05f);
	}

	public static Color lighten(Color src, float amount) {
		HSLColor hsl = new HSLColor(src);
		float l = Math.min(100, hsl.getLuminance() + amount*100);
		return hsl.adjustLuminance(l);
	}

	public static Color darken(Color src) {
		return StratusColour.darken(src, 0.05f);
	}

	public static Color darken(Color src, float amount) {
		HSLColor hsl = new HSLColor(src);
		float l = Math.max(0, hsl.getLuminance() - amount*100);
		return hsl.adjustLuminance(l);
	}

	public static Color saturate(Color src, float amount) {
		HSLColor hsl = new HSLColor(src);
		float s = Math.min(100, hsl.getSaturation() + amount*100);
		return hsl.adjustSaturation(s);
	}

	public static Color desaturate(Color src, float amount) {
		HSLColor hsl = new HSLColor(src);
		float s = Math.max(0, hsl.getSaturation() - amount*100);
		return hsl.adjustSaturation(s);
	}

	public static Color lessTransparent(Color src) {
		return StratusColour.lessTransparent(src, 0.05f);
	}

	public static Color lessTransparent(Color src, float amount) {
		return new Color(src.getRed(), src.getGreen(), src.getBlue(), (int)Math.min(src.getAlpha()+(amount*255), 255f));
	}

	public static Color moreTransparent(Color src) {
		return StratusColour.moreTransparent(src, 0.05f);
	}

	public static Color moreTransparent(Color src, float amount) {
		return new Color(src.getRed(), src.getGreen(), src.getBlue(), (int)Math.max(src.getAlpha()-(amount*255), 0f));
	}

    public static boolean isCustomColour(Color c) {
    	return c.getClass().getName().equals("java.awt.Color");
    }

	public static Color blackOrWhite(Color colour) {
		float lum = new HSLColor(colour).getLuminance();
		Color highlight;
		if (lum < 60) {
			highlight = Stratus.getTheme().getPalette().getColour("Light", "1");
		} else {
			highlight = Stratus.getTheme().getPalette().getColour("Dark", "5");
		}
		return highlight;
	}
	
}
