package stratus.painters;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;

import stratus.Stratus;
import stratus.theme.Theme;

public class SplitPaneDividerPainter extends SimpleThemed implements Painter<JComponent> {

	public SplitPaneDividerPainter(Theme theme) {
		super(theme);
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		
		g.setColor(getTheme().getControl());
		g.fillRect(0, 0, width, height);
		
		g.setColor(getTheme().getWidgetBorder());
		g.drawLine(0, 0, width, 0);
		g.drawLine(0, height-1, width, height-1);
		
		g.setColor(Stratus.lighten(getTheme().getControl()));
		g.drawLine(0, 1, width, 1);
		g.drawLine(0, height-2, width, height-2);
		
	}

}
