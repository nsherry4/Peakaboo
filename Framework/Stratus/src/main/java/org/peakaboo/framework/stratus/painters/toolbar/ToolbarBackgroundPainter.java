package org.peakaboo.framework.stratus.painters.toolbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.Stratus;
import org.peakaboo.framework.stratus.painters.SimpleThemed;
import org.peakaboo.framework.stratus.theme.Theme;

public class ToolbarBackgroundPainter extends SimpleThemed implements Painter<JComponent> {

    private Color c1, c2;
    protected float[] points = new float[] {0f, 1.0f};
    
    
    public ToolbarBackgroundPainter(Theme theme) {
    	super(theme);
    	this.c1 = Stratus.lighten(theme.getNegative(), theme.widgetCurve()/2f);
    	this.c2 = Stratus.darken(theme.getNegative(), theme.widgetCurve()/2f);
    }

    @Override
    public void paint(Graphics2D g, JComponent object, int width, int height) {
    	if (Stratus.focusedWindow(object)) {
    		g.setPaint(getTheme().getNegative());	
    	} else {
    		g.setPaint(new LinearGradientPaint(0, 0, 0, height, points, new Color[] {getTheme().getControl(), getTheme().getControl()}));
    	}
        
        g.fillRect(0, 0, width-1, height-1);
    }

}
