package org.peakaboo.framework.stratus.laf.painters.tabs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.Painter;

import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.laf.painters.SimpleThemed;
import org.peakaboo.framework.stratus.laf.theme.Theme;

public class TabbedAreaPainter extends SimpleThemed implements Painter<JComponent> {

	protected Color normal, top;
	
	public TabbedAreaPainter(Theme theme, boolean enabled) {
		super(theme);
		top = getTheme().getNegative();
		normal = getTheme().getControl();
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {

		Color color;
		boolean isTopLevel = (object.getParent().getParent().getParent().getParent() instanceof Window);
		boolean focused = Stratus.focusedWindow(object);
		
		if (isTopLevel && focused) {
			color = top;
		} else {
			color = normal;
		}

		g.setPaint(color);
    	g.fillRect(0, 0, width, height);
    	
	}

}
