package org.peakaboo.framework.stratus.laf.painters.tabs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Window;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.painters.StatefulPainter;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class TabPainter extends StatefulPainter{

	protected Color normal, top;
	
	public TabPainter(Theme theme, ButtonState... buttonState) {
		super(theme, buttonState);
		top = getTheme().getNegative();
		normal = getTheme().getControl();
	}

	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		g = Stratus.modernGraphicsSettings(g);
		
		Theme theme = getTheme();
		
		Color color;
		boolean isTopLevel = (object.getParent().getParent().getParent().getParent() instanceof Window);
		boolean focused = Stratus.focusedWindow(object);
		
		if (isTopLevel && focused) {
			color = top;
		} else {
			color = normal;
		}
		
		
		if (isSelected()) {
			color = StratusColour.darken(color, 0.10f);
		}
		
		if (isMouseOver()) {
			color = StratusColour.darken(color, theme.selectionStrength());
		}
		
		
		float pad = 3;
		float radius = theme.borderRadius();

		if (isFocused() || isSelected() || isMouseOver()) {
			g.setColor(color);
			g.fillRoundRect((int)pad, (int)pad, (int)(width-pad*2), (int)(height-pad*2), (int)radius, (int)radius);
		}
		
	}
	

}
