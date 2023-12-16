package org.peakaboo.framework.stratus.laf.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.swing.JComponent;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.Stratus.ButtonState;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.theme.Theme;

//Fills the area of button style controls (no borders, etc)
public abstract class AbstractButtonPainter extends StatefulPainter {

	public static class ButtonPalette {
		
		public Color fill;
		public Color selection, gloss, border;
		public Color text;
		
		public ButtonPalette() {
			// TODO Auto-generated constructor stub
		}
		
		public ButtonPalette(ButtonPalette copy) {
			this.fill = copy.fill;
			this.selection = copy.selection;
			this.gloss = copy.gloss;
			this.border = copy.border;
			this.text = copy.text;
			
			
		}
		
	}
	
	private ButtonPalette palette = basePalette();
	    
    protected float radius = 0;
    protected float borderWidth = 1;
    protected int margin = 1;
    
    

    
    public AbstractButtonPainter(Theme theme, ButtonState... buttonStates) {
    	this(theme, theme.widgetMargins(), buttonStates);
    }
    
    public AbstractButtonPainter(Theme theme, int margin, ButtonState... buttonStates) {
    	super(theme, buttonStates);
    	this.margin = margin;
    	this.radius = theme.borderRadius();
    }
    
    private ButtonPalette basePalette() {
    	
    	ButtonPalette palette = new ButtonPalette();
    	
    	Theme theme = getTheme();
    	
    	//ENABLED is default
    	palette.fill = theme.getWidgetAlpha();
    	palette.text = theme.getControlText();
    	palette.selection = theme.getWidgetSelectionAlpha();
    	palette.border = theme.getWidgetBorder();
    	
    	return palette;
    }
    
    private void setupPalette(ButtonPalette palette, JComponent object) {
    	   	
    	
    	Theme theme = getTheme();
    	
    	//ENABLED is default
    	palette.text = theme.getControlText();
    	palette.selection = theme.getWidgetSelectionAlpha();
    	palette.border = theme.getWidgetBorderAlpha();
    	
    	if (isDisabled()) {
    		palette.fill = new Color(0x00000000, true);
    		
    		//Disabled and selected, like toggle button
        	if (isSelected()) {
        		palette.fill = StratusColour.lessTransparent(palette.fill, 0.1f);
        	}
    		
    	} else {
    		
    		BiFunction<Color, Float, Color> darken;
        	if (StratusColour.isCustomColour(object.getBackground())) {
        		palette.fill = object.getBackground();
        		palette.selection = new Color(0x2fffffff, true);
        		darken = StratusColour::darken;
        	} else {
        		palette.fill = theme.getWidgetAlpha();
        		darken = StratusColour::lessTransparent;
        	}
        	
        	if (isMouseOver()) {
        		palette.fill = darken.apply(palette.fill, theme.selectionStrength());
        	}
        	
        	if (isPressed() || isSelected()) {
        		palette.fill = darken.apply(palette.fill, 0.10f);
        	}
    		
    	}

    			
    }
    
    
    /**
     * Makes a ButtonPalette object for this component. If there is nothing 
     * special about it, it will return the stock ButtonPalette instance.
     * The object may be null to retrieve the default ButtonPalette.
     */
    protected ButtonPalette makePalette(JComponent object) {
    	
    	if (object == null) {
    		return basePalette();
    	}
    	
    	setupPalette(palette, object);
    	return palette;
    }
    
    @Override
    public final void paint(Graphics2D g, JComponent object, int width, int height) {
    	
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	if (isBorderPainted(object)) {
    		paint(g, object, width, height, makePalette(object));   		
    	}
    	
    	var dot = getNotificationDotColour(object);
    	if (dot != null && dot.isPresent()) {
    		int size = 8;
    		g.setColor(dot.get());
    		g.fillArc(width-size, height-size, size, size, 0, 360);
    	}
		
    }
    
    


    protected boolean hasBorder() {
    	return !getTheme().isFlat();
    }
    
    protected void paint(Graphics2D g, JComponent object, int width, int height, ButtonPalette palette) {
    	if (hasBorder()) drawBorder(object, width, height, margin, g, palette);
    	drawMain(object, width, height, margin, g, palette);
    	drawSelection(object, width, height, margin, g, palette);
    }

    

	protected Shape fillShape(JComponent object, float width, float height, float pad) {
    	float p = pad+1;
    	return new RoundRectangle2D.Float(p, p, width-p*2, height-p*2, radius, radius);
    }
    
    protected Shape borderShape(JComponent object, float width, float height, float pad) {
    	return new RoundRectangle2D.Float(pad, pad, width-pad*2, height-pad*2, radius, radius);
    }
    
    
    protected Shape selectionShape(JComponent object, float width, float height, float pad) {
    	return new RoundRectangle2D.Float(pad, pad, width-pad*2-1, height-pad*2-1, radius, radius);
    }
    
    
    
    
    protected void drawBorder(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Border should be darker at the bottom when not pressed (bit of a shadow?)
    	g.setPaint(borderPaint(object, width, height, pad, palette));
    	g.fill(borderShape(object, width, height, pad));
    }
    
    protected void drawMain(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	g.setPaint(mainPaint(object, width, height, pad, palette));
    	g.fill(fillShape(object, width, height, pad));
	}


    protected void drawSelection(JComponent object, float width, float height, float pad, Graphics2D g, ButtonPalette palette) {
    	//Focus selection if focused but not pressed
    	pad += 2;
    	if (isFocused() && !isPressed()) {
        	g.setPaint(selectionPaint(object, width, height, pad, palette));
        	//Stroke old = g.getStroke();
        	//g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {2, 1}, 0f));
        	g.draw(selectionShape(object, width, height, pad));
        	//g.setStroke(old);
    	}
    }
    
    
    

    
    protected Paint mainPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
    	return palette.fill;
    }
    
    protected Paint borderPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
    	return palette.border;
    }
    
    protected Paint selectionPaint(JComponent object, float width, float height, float pad, ButtonPalette palette) {
    	return palette.selection;
    }
    
    
    protected boolean isBorderPainted(JComponent component) {
    	Object prop = component.getClientProperty(Stratus.KEY_BUTTON_BORDER_PAINTED);
    	if (prop == null) { 
    		return true;
    	}
    	try {
    		boolean painted = (boolean) prop;
    		return painted;
    	} catch (ClassCastException e) {
    		return true;
    	}
    }
    
    /**
     * Gets the colour of the notification dot, or null for not displayed
     */
    protected Optional<Color> getNotificationDotColour(JComponent component) {
    	Object prop = component.getClientProperty(Stratus.KEY_BUTTON_NOTIFICATION_DOT);
    	if (prop == null) { 
    		return null;
    	}
    	try {
    		Optional<Color> dotColour = (Optional<Color>) prop;
    		return dotColour;
    	} catch (ClassCastException e) {
    		return null;
    	}
    }
    
    
}



