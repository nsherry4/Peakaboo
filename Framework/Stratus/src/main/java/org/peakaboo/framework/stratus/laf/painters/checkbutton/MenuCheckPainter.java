package org.peakaboo.framework.stratus.laf.painters.checkbutton;

import java.awt.Color;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.laf.theme.Theme;

public class MenuCheckPainter extends CheckPainter {

	public MenuCheckPainter(Theme theme, int pad, int voffset, boolean enabled) {
		super(theme, pad, voffset, enabled);
	}

	protected Color getCheckColour(JComponent object) {
		if (enabled) {
			return getTheme().getMenuText();
		} else {
			return getTheme().getControlTextDisabled();
		}
	}
	
}
