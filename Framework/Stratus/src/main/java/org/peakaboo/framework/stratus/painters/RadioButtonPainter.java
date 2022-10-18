package org.peakaboo.framework.stratus.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.Stratus.ButtonState;
import org.peakaboo.framework.stratus.theme.Theme;

public class RadioButtonPainter extends AbstractButtonPainter {

	private boolean selected;
	private ButtonPalette palette;
	private final int RADIO_MARGIN = 4;
	
	public RadioButtonPainter(Theme theme, boolean selected, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		palette = super.makePalette(null);
		palette.border = Stratus.darken(palette.border, 0.1f);
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
			if (isDisabled()) {
				g.setColor(getTheme().getWidgetBorder());
			} else {
				g.setColor(getTheme().getHighlight());				
			}
			
			g.fillArc(RADIO_MARGIN, RADIO_MARGIN, width-RADIO_MARGIN-RADIO_MARGIN, height-RADIO_MARGIN-RADIO_MARGIN, 0, 360);
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
