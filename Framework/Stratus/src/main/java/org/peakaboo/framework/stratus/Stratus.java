package org.peakaboo.framework.stratus;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.peakaboo.framework.stratus.theme.Theme;

public class Stratus {

	public static final String KEY_WINDOW_FOCUSED = "stratus-window-focus";
	public static final String KEY_BUTTON_BORDER_PAINTED = "stratus-button-border-painted";
	
    public enum ButtonState {
    	DISABLED,
    	ENABLED,
    	MOUSEOVER,
    	FOCUSED,
    	PRESSED,
    	DEFAULT,
    	SELECTED
    }
	
//    
//	public static Color highlight = new Color(0x498ed8);
//	public static Color control = new Color(0x393F3F);
//	public static Color controlText = new Color(0xffffff);
//	public static Color border = new Color(0x575D5D);
//	

    
	public static boolean hasTheme() {
		return getTheme() != null;
	}
	
	public static Theme getTheme() {
		try {
			return (Theme) UIManager.get("stratus-theme");
		} catch (NullPointerException e) {
			return null;
		}
	}
		
	public static Color lighten(Color src) {
		return lighten(src, 0.05f);
	}
	
	public static Color lighten(Color src, float amount) {
		HSLColor hsl = new HSLColor(src);
		float l = Math.min(100, hsl.getLuminance() + amount*100);
		return hsl.adjustLuminance(l);
    }	
    
	public static Color darken(Color src) {
		return darken(src, 0.05f);
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
		return lessTransparent(src, 0.05f);
	}
	
	public static Color lessTransparent(Color src, float amount) {
		return new Color(src.getRed(), src.getGreen(), src.getBlue(), (int)Math.min(src.getAlpha()+(amount*255), 255f));
	}
	
	public static Color moreTransparent(Color src) {
		return moreTransparent(src, 0.05f);
	}
	
	public static Color moreTransparent(Color src, float amount) {
		return new Color(src.getRed(), src.getGreen(), src.getBlue(), (int)Math.max(src.getAlpha()-(amount*255), 0f));
	}
    
	
	public static boolean focusedWindow(JComponent component) {
		Object prop = component.getRootPane().getClientProperty(Stratus.KEY_WINDOW_FOCUSED);
		if (prop == null) {
			return true;
		}
		try {
			return (boolean)prop;
		} catch (ClassCastException e) {
			return true;
		}
	}
    	
}
