package org.peakaboo.framework.stratus.laf.painters.radiobutton;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class MenuRadioButtonPainter extends RadioButtonPainter {
	
	public MenuRadioButtonPainter(Theme theme, boolean selected, ButtonState... buttonStates) {
		super(theme, selected, buttonStates);

		Color transparent = new Color(0x00000000, true);
		palette.border = transparent;
		palette.fill = transparent;
		
		radioMargin = 0;
		
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		radius = width;

    	float pad = margin;
    	drawBorder(object, width, height, pad, g, palette);
    	drawMain(object, width, height, pad, g, palette);
    	drawSelection(object, width, height, pad, g, palette);
				
		if (selected) {
			g.setColor(getForegroundColor());
			g.fillArc(radioMargin, 1+radioMargin, (int)radius-radioMargin-radioMargin, (int)radius-radioMargin-radioMargin, 0, 360);
		}
	}
	
	protected Color getForegroundColor() {
		if (isDisabled()) {
			return getTheme().getControlTextDisabled();
		} else {
			return getTheme().getMenuText();
		}
	}

}
