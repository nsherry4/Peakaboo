package org.peakaboo.framework.stratus.painters.textfield;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.StatefulPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class TextFieldBackgroundPainter extends StatefulPainter {

	protected int margin = 2;
	protected float radius = 0;
	protected float[] points = new float[] {0f, 0.25f};
	
	protected Color c1, c2;
	
	public TextFieldBackgroundPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		this.radius = theme.borderRadius();
		
		if (!isDisabled()) {
			c1 = Stratus.darken(getTheme().getRecessedControl(), 0.025f);
			c2 = getTheme().getRecessedControl();
		} else {
			c1 = Stratus.darken(getTheme().getControl(), 0.025f);
			c2 = getTheme().getControl();
		}
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		if (object instanceof JTextArea) {
			JTextArea t = (JTextArea) object;
			if (!t.isOpaque()) {
				return;
			}
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	float pad = margin;
    	   
  
    	//Bevel under area
    	//g.setPaint(getTheme().getWidgetBevel());
    	//Shape bevel = new RoundRectangle2D.Float(pad, pad+1, width-pad*2, height-pad*2+1, radius, radius);     
    	//g.fill(bevel);
    	
    	

    	//Border
    	Stroke old = g.getStroke();
    	float borderStroke = 1;
    	if (isSelected() || isFocused()) {
    		g.setPaint(getTheme().getHighlight());
    		borderStroke = 3;
    	} else {
    		g.setPaint(getTheme().getWidgetBorderAlpha());
    	}
    	
    	g.setStroke(new BasicStroke(borderStroke));
    	Shape border = new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2, radius, radius);     
    	g.draw(border);
    	
    	g.setStroke(old);
    	
    	
    	//Main fill
    	pad = margin + 1;
    	Shape fillArea = new RoundRectangle2D.Float(pad, pad, width-pad*2+1, height-pad*2+1, radius, radius);
    	try {
    		//g.setPaint(new LinearGradientPaint(0, pad, 0, height-pad, points, new Color[] {c1, c2}));
    		g.setPaint(getTheme().getRecessedControl());
    	} catch (IllegalArgumentException e) {
    		g.setPaint(getTheme().getRecessedControl());
    	}
    	g.fill(fillArea);

		
	}

}
