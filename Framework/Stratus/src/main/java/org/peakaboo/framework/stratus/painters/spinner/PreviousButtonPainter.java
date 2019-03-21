package org.peakaboo.framework.stratus.painters.spinner;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.ButtonPainter;
import org.peakaboo.framework.stratus.theme.Theme;

public class PreviousButtonPainter extends ButtonPainter {

	private ButtonPalette palette;
	
	public PreviousButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		palette = super.makePalette(null);
		palette.bevel = new Color(0x00000000, true);	
	}
  
	@Override
	protected Shape fillShape(JComponent object, float width, float height, float pad) {
		pad++;
		Shape fillArea = new RoundRectangle2D.Float(1, 1-radius, width-pad-1, height-pad+radius, radius, radius);
		return fillArea;
    }
    
	@Override
    protected Shape borderShape(JComponent object, float width, float height, float pad) {
    	Shape border = new RoundRectangle2D.Float(0, 0-radius, width-pad, height-0+radius, radius, radius);
    	return border;
    }
    
	@Override
    protected Shape shadowShape(JComponent object, float width, float height, float pad) {
    	GeneralPath path = new GeneralPath();
    	float y = (int)(height-(pad));
    	float startx = 2;
    	float endx = width-(pad+1)*2;
    	path.moveTo(startx, y);
    	path.lineTo(endx, y);
    	return path;
    }

	@Override
    protected Shape dashShape(JComponent object, float width, float height, float pad) {
    	return new RoundRectangle2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, radius, radius);
    }
	
    protected Paint mainPaint(float width, float height, float pad, ButtonPalette palette) {
    	try {
    		return new LinearGradientPaint(0, -(height-pad), 0, height-pad, palette.fillPoints, palette.fillArray);
    	} catch (IllegalArgumentException e) {
    		return super.getTheme().getWidget();
    	}
    }
	
}
