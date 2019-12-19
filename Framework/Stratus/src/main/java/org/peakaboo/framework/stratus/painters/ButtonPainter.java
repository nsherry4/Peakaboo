package org.peakaboo.framework.stratus.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.painters.AbstractButtonPainter.ButtonPalette;
import org.peakaboo.framework.stratus.theme.Theme;

public class ButtonPainter extends AbstractButtonPainter {

	private Color shadow;
	
	public ButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
	}
	
	public ButtonPainter(Theme theme, int margin, ButtonState... buttonStates) {
		super(theme, margin, buttonStates);
	}

	/* Complex shadows WIP
	@Override
    protected void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
    	drawShadow(object, width, height, margin, g, palette);
    	drawBorder(object, width, height, margin, g, palette);
    	drawMain(object, width, height, margin, g, palette);
   		drawBevel(object, width, height, margin, g, palette);
   		drawDash(object, width, height, margin, g, palette);
    }

	@Override
    protected void drawShadow(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Shadow at bottom of button unless pressed
    	if (!(isPressed() || isSelected()) && !(isDisabled())) {
	    	g.setPaint(shadowPaint(width, height, pad, palette));
	    	for (Shape shape : shadowShapes(object, width, height, pad)) {
	    		g.fill(shape);
	    	}
    	}
    }

    @Override
    protected Paint shadowPaint(float width, float height, float pad, ButtonPalette palette) {
    	if (shadow == null) {
    		shadow = new Color(
    				palette.shadow.getRed(),
    				palette.shadow.getGreen(),
    				palette.shadow.getBlue(),
    				palette.shadow.getAlpha()/8
    			);
    				
    	}
    	return shadow;
    }
    */
	
    protected List<Shape> shadowShapes(JComponent object, float width, float height, float pad) {
    	return Arrays.asList(
    			new RoundRectangle2D.Float(pad-1, pad, width-pad*2+2, height-pad*2+2, radius, radius),
    			new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2+2, radius, radius),
    			new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2+1, radius, radius)
    		);
    }
    
}
