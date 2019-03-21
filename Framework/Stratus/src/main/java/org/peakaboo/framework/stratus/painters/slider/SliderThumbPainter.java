package org.peakaboo.framework.stratus.painters.slider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.ButtonPainter;
import org.peakaboo.framework.stratus.painters.ButtonPainter.ButtonPalette;
import org.peakaboo.framework.stratus.theme.Theme;

public class SliderThumbPainter extends ButtonPainter {

	private ButtonPalette palette;
	
	public SliderThumbPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		palette = super.makePalette(null);

		palette.shadow = Stratus.lessTransparent(palette.shadow, 0.15f);
		palette.border = Stratus.lessTransparent(palette.border);
		palette.fillArray = new Color[] {Stratus.lighten(palette.fillArray[0]), palette.fillArray[0], palette.fillArray[1]};
		palette.fillPoints = new float[] {0, 0.2f, 1f};

	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		radius = width;
		super.paint(g, object, width, height, palette);		
	}
	
	@Override
	protected ButtonPalette makePalette(JComponent object) {
		return palette;
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
    
	@Override
    protected Shape shadowShape(JComponent object, float width, float height, float pad) {
    	return new Arc2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, 180, 180, Arc2D.OPEN);
    }
    
	@Override
    protected Shape bevelShape(JComponent object, float width, float height, float pad) {
    	pad++;
    	return new Arc2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, 0, 180, Arc2D.OPEN);
    }
    
	@Override
    protected Paint shadowPaint(float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, height-pad, new float[] {0.5f, 0.9f}, new Color[] {new Color(0x0000000, true), palette.shadow});
    }

	@Override
    protected Paint bevelPaint(float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, height-pad, new float[] {0.075f, 0.5f}, new Color[] {palette.bevel, new Color(0x0000000, true)});
    }
    
	
}
