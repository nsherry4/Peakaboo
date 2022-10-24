package org.peakaboo.framework.stratus.painters.tabs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.StatefulPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class TabPainter extends StatefulPainter{

	protected Color normal, top;
	
	public TabPainter(Theme theme, ButtonState... buttonState) {
		super(theme, buttonState);
		top = getTheme().getNegative();
		normal = getTheme().getControl();
	}

	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
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
			color = Stratus.darken(color, 0.10f);
		}
		
		if (isMouseOver()) {
			color = Stratus.darken(color, theme.selectionStrength());
		}
		
		
		float pad = 3;
		float radius = theme.borderRadius();
		Shape shape = new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2, radius, radius);


		if (isFocused() || isSelected() || isMouseOver()) {
			g.setColor(color);
			g.fill(shape);
		}
		
	}
	

}
