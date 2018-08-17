package stratus.painters.spinner;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import stratus.Stratus.ButtonState;
import stratus.painters.ButtonPainter;
import stratus.theme.Theme;

public class NextButtonPainter extends ButtonPainter {

	private ButtonPalette palette;
	
	public NextButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
		
		palette = super.makePalette(null);
		palette.shadow = new Color(0x00000000, true);
	}
	
    protected ButtonPalette makePalette(JComponent object) {
    	return palette;
    }
	   
    
	protected Shape fillShape(float width, float height, float pad) {
		pad++;
		Shape fillArea = new RoundRectangle2D.Float(1, pad, width-pad-1, height+radius, radius, radius);
		return fillArea;
    }
    
    protected Shape borderShape(float width, float height, float pad) {
    	Shape border = new RoundRectangle2D.Float(0, pad, width-pad, height+radius, radius, radius);
    	return border;
    }
        
    protected Paint mainPaint(float width, float height, float pad, ButtonPalette palette) {
    	return new LinearGradientPaint(0, pad, 0, (height-pad)*2f, palette.fillPoints, palette.fillArray);
    }
	
}
