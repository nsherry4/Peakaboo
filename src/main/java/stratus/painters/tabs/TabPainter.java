package stratus.painters.tabs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import stratus.Stratus;
import stratus.Stratus.ButtonState;
import stratus.painters.StatefulPainter;
import stratus.theme.Theme;

public class TabPainter extends StatefulPainter{

	Color fillNL, bottomNL;
	Color fillTL, bottomTL;
	
	Stroke bottomStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	
	public TabPainter(Theme theme, ButtonState... buttonState) {
		super(theme, buttonState);
		
		if (isSelected()) {
			fillNL = Stratus.darken(getTheme().getControl(), 0.02f);
			bottomNL = getTheme().getHighlight();
			fillTL = getTheme().getControl();
			bottomTL = getTheme().getHighlight();
		} else {
			fillNL = Stratus.darken(getTheme().getControl(), 0.08f);
			bottomNL = Stratus.darken(getTheme().getWidgetBorder(), 0.1f);
			fillTL = Stratus.darken(getTheme().getControl(), 0.04f);;
			bottomTL = getTheme().getWidgetBorder();
		}

		
	}

	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		
		Color fill, bottom;
		boolean isTopLevel = (object.getParent().getParent().getParent().getParent() instanceof Window);
		if (isTopLevel) {
			fill = fillTL;
			bottom = bottomTL;
		} else {
			fill = fillNL;
			bottom = bottomNL;
		}
		
		if (isFocused() || isSelected() || isMouseOver()) {
		
			g.setColor(fill);
			g.fillRect(0, 0, width, height);
			
			g.setColor(getTheme().getWidgetBorder());
			g.drawRect(0, 0, width, height-1);
			
			Stroke old = g.getStroke();
			g.setStroke(bottomStroke);
			g.setColor(bottom);
			g.drawLine(0, height-2, width+1, height-2);
			g.setStroke(old);
		}
		
    	//Focus dash if focused but not pressed
		int pad = 4;
    	if (isFocused() && !isPressed()) {
        	g.setPaint(new Color(0, 0, 0, 0.15f));
        	Shape focus = new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2 - 3, 0, 0);
        	Stroke old = g.getStroke();
        	g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {2, 2}, 0f));
        	g.draw(focus);
        	g.setStroke(old);
    	}
	}

}
