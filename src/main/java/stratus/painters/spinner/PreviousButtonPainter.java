package stratus.painters.spinner;

import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import stratus.Stratus.ButtonState;
import stratus.painters.ButtonPainter;
import stratus.theme.Theme;

public class PreviousButtonPainter extends ButtonPainter {

	public PreviousButtonPainter(Theme theme, ButtonState... buttonStates) {
		super(theme, buttonStates);
	}
	
	@Override
    public void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	float pad = margin, lpad = 0, bpad = 0, tpad=0;
    	
    	
    	
    	//Bevel under button
    	g.setPaint(getTheme().getWidgetBevel());
    	Shape bevel = new RoundRectangle2D.Float(lpad, tpad+1, width-pad-lpad, height-pad-bpad, radius, radius);     
    	g.fill(bevel);
    	
    	//Border
    	g.setPaint(palette.border);
    	Shape border = new RoundRectangle2D.Float(lpad, tpad-radius, width-pad-lpad, height-pad-bpad+radius, radius, radius);     
    	g.fill(border);
    	
    	
    	
    	//Main fill
    	pad += borderWidth;
    	lpad += borderWidth;
    	bpad += borderWidth;
    	tpad += borderWidth;
    	Shape fillArea = new RoundRectangle2D.Float(lpad, tpad-radius, width-pad-lpad, height-pad-bpad+radius, radius, radius);
    	g.setPaint(new LinearGradientPaint(0, -(height-(pad+1)), 0, height-bpad, palette.fillPoints, palette.fillArray));
    	g.fill(fillArea);


    	
    	//Restore border at top after mail fill was overextended
    	//Border
    	g.setPaint(palette.border);
    	//g.drawLine(0, 0, (int)(width-pad), 0);
    	
	}
	
	
}
