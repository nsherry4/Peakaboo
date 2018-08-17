package stratus.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import stratus.Stratus;
import stratus.Stratus.ButtonState;
import stratus.theme.Theme;

public class RadioButtonPainter extends ButtonPainter {

	private boolean selected;
	private ButtonPalette palette;
	
	
	public RadioButtonPainter(Theme theme, boolean selected, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		palette = super.makePalette(null);
		palette.shadow = Stratus.lessTransparent(palette.shadow, 0.15f);
		palette.border = Stratus.lessTransparent(palette.border);
		palette.fillArray = new Color[] {Stratus.lighten(palette.fillArray[0]), palette.fillArray[0], palette.fillArray[1]};
		palette.fillPoints = new float[] {0, 0.2f, 1f};
		
		this.selected = selected;
		

	}


	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		radius = width;
		
		
    	float pad = margin;
    	drawBorder(width, height, pad, g, palette);
    	drawMain(width, height, pad, g, palette);
    	drawShadow(width, height, pad, g, palette);
    	drawBevel(width, height, pad, g, palette);
    	drawDash(width, height, pad, g, palette);
		
		
		if (selected) {
			if (isDisabled()) {
				g.setColor(getTheme().getWidgetBorder());
			} else {
				g.setColor(getTheme().getControlText());				
			}
			
			g.fillArc(6, 6, width-12, height-12, 0, 360);
		}
	}
	
    protected ButtonPalette makePalette(JComponent object) {
    	return palette;
    }
    
    protected Shape fillShape(float width, float height, float pad) {
    	pad++;
    	return new Ellipse2D.Float(pad, pad, width-pad*2, height-pad*2);
    }
    
    protected Shape borderShape(float width, float height, float pad) {
    	return new Ellipse2D.Float(pad, pad, width-pad*2, height-pad*2);
    }
    
    protected Shape shadowShape(float width, float height, float pad) {
    	return new Arc2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, 180, 180, Arc2D.OPEN);
    }
    
    protected Shape bevelShape(float width, float height, float pad) {
    	pad++;
    	return new Arc2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, 0, 180, Arc2D.OPEN);
    }
    
    
    protected Paint shadowPaint(float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, height-pad, new float[] {0.5f, 0.9f}, new Color[] {new Color(0x0000000, true), palette.shadow});
    }

    protected Paint bevelPaint(float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, height-pad, new float[] {0.075f, 0.5f}, new Color[] {palette.bevel, new Color(0x0000000, true)});
    }
    
	
}
