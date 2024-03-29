package org.peakaboo.framework.stratus.laf.painters.textfield;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.painters.StatefulPainter;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class TextFieldBackgroundPainter extends StatefulPainter {

	protected int margin = 2;
	protected float radius = 0;
	protected float[] points = new float[] {0f, 0.25f};
	
	protected Color c1, c2;
	
	public TextFieldBackgroundPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		this.radius = theme.borderRadius();
		
		if (!isDisabled()) {
			c1 = StratusColour.darken(getTheme().getRecessedControl(), 0.025f);
			c2 = getTheme().getRecessedControl();
		} else {
			c1 = StratusColour.darken(getTheme().getControl(), 0.025f);
			c2 = getTheme().getControl();
		}
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	

    	//Border
    	Stroke old = g.getStroke();
    	float borderStroke = 1;
    	if (isSelected() || isFocused()) {
    		g.setPaint(getTheme().getHighlight());
    		borderStroke = 3;
    	} else {
    		g.setPaint(getTheme().getWidgetBorderAlpha());
    	}
    	float pad;
    	
    	g.setStroke(new BasicStroke(borderStroke));
    	pad = margin;
    	g.drawRoundRect((int)pad, (int)pad, (int)(width-pad*2), (int)(height-pad*2), (int)radius, (int)radius);
    	g.setStroke(old);
    	
    	
    	//Main fill
    	pad = margin + 1;
    	Color bg = getTheme().getRecessedControl();
    	if (StratusColour.isCustomColour(object.getBackground())) {
    		bg = object.getBackground();
    	}
   		g.setPaint(bg);
    	g.fillRoundRect((int)pad, (int)pad, (int)(width-pad*2+1), (int)(height-pad*2+1), (int)radius, (int)radius);
    	
    	

		
	}

}
