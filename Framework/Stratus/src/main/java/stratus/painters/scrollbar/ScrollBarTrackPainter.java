package stratus.painters.scrollbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.Painter;

import stratus.Stratus;
import stratus.painters.SimpleThemed;
import stratus.theme.Theme;

public class ScrollBarTrackPainter extends SimpleThemed implements Painter<JComponent> {

	public ScrollBarTrackPainter(Theme theme) {
		super(theme);
	}


	Color c = Stratus.darken(getTheme().getControl(), 0.1f);


	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    	
    	g.setPaint(c);    
    	g.fillRect(0, 0, width, height);
		
	}
	
	
}