package org.peakaboo.framework.stratus.laf.painters.slider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.laf.painters.AbstractButtonPainter;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class SliderThumbPainter extends AbstractButtonPainter {

	private ButtonPalette palette;
	
	public SliderThumbPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		palette = super.makePalette(null);
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		radius = width;
		super.paint(g, object, width, height, palette);		
	}
	
	@Override
	protected ButtonPalette makePalette(JComponent object) {
		super.makePalette(object);
		palette.fill = getTheme().getControl();
		palette.border = StratusColour.lessTransparent(getTheme().getWidgetBorderAlpha());
		return palette;
	}
	
    protected boolean hasBorder() {
    	return true;
    }
    
	
	@Override
    protected Shape fillShape(JComponent object, float width, float height, float pad) {
    	pad++;
    	return new Ellipse2D.Float(pad, pad, width-pad*2, height-pad*2);
    }
    
	@Override
    protected Shape borderShape(JComponent object, float width, float height, float pad) {
    	return new Ellipse2D.Float(pad, pad, width-pad*2, height-pad*2);
    }
    


	
}
