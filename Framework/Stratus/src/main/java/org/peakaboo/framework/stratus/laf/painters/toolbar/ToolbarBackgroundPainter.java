package org.peakaboo.framework.stratus.laf.painters.toolbar;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class ToolbarBackgroundPainter extends SimpleThemed implements Painter<JComponent> {

    protected float[] points = new float[] {0f, 1.0f};
    
    
    public ToolbarBackgroundPainter(Theme theme) {
    	super(theme);
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
