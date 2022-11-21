package org.peakaboo.framework.stratus.laf.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.laf.painters.AbstractButtonPainter.ButtonPalette;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class ButtonPainter extends AbstractButtonPainter {

	private Color shadow;
	
	public ButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
	}
	
	public ButtonPainter(Theme theme, int margin, ButtonState... buttonStates) {
		super(theme, margin, buttonStates);
	}

    protected List<Shape> shadowShapes(JComponent object, float width, float height, float pad) {
    	return Arrays.asList(
    			new RoundRectangle2D.Float(pad-1, pad, width-pad*2+2, height-pad*2+2, radius, radius),
    			new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2+2, radius, radius),
    			new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2+1, radius, radius)
    		);
    }
    
}
