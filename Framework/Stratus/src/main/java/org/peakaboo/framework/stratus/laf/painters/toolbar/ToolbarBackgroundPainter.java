package org.peakaboo.framework.stratus.laf.painters.toolbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class ToolbarBackgroundPainter extends SimpleThemed implements Painter<JComponent> {

    private Color c1, c2;
    protected float[] points = new float[] {0f, 1.0f};
    
    
    public ToolbarBackgroundPainter(Theme theme) {
    	super(theme);
    	this.c1 = StratusColour.lighten(theme.getNegative(), theme.widgetCurve()/2f);
    	this.c2 = StratusColour.darken(theme.getNegative(), theme.widgetCurve()/2f);
    }

    @Override
    public void paint(Graphics2D g, JComponent object, int width, int height) {
    	if (Stratus.focusedWindow(object)) {
    		g.setPaint(getTheme().getNegative());	
    	} else {
    		g.setPaint(getTheme().getControl());
    	}
        
        g.fillRect(0, 0, width-1, height-1);
    }

}
