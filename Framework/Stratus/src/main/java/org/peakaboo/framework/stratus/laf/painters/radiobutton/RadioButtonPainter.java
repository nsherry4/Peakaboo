package org.peakaboo.framework.stratus.laf.painters.radiobutton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.laf.painters.AbstractButtonPainter;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class RadioButtonPainter extends AbstractButtonPainter {

	protected boolean selected;
	protected ButtonPalette palette;
	protected int radioMargin = 4;
	
	public RadioButtonPainter(Theme theme, boolean selected, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		palette = super.makePalette(null);
		palette.border = theme.getWidgetBorderAlpha();
		palette.fill = Color.white;
		
		this.selected = selected;
		

	}


	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		radius = width;

    	float pad = margin;
    	drawBorder(object, width, height, pad, g, palette);
    	drawMain(object, width, height, pad, g, palette);
    	drawSelection(object, width, height, pad, g, palette);
				
		if (selected) {
			g.setColor(getForegroundColor());
			g.fillArc(radioMargin, radioMargin, width-radioMargin-radioMargin, height-radioMargin-radioMargin, 0, 360);
		}
	}
	
	protected Color getForegroundColor() {
		if (isDisabled()) {
			return getTheme().getWidgetBorder();
		} else {
			return getTheme().getHighlight();
		}
	}
	
	@Override
	protected boolean hasBorder() {
		return true;
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


    
	
}
