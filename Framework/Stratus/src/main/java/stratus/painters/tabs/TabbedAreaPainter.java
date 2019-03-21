package stratus.painters.tabs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.Painter;

import stratus.Stratus;
import stratus.painters.SimpleThemed;
import stratus.theme.Theme;

public class TabbedAreaPainter extends SimpleThemed implements Painter<JComponent> {

	protected Color n1, n2, t1, t2;
	
	protected boolean enabled = true;

	private int lastWidth=-1, lastHeight=-1;
	private Paint lastPaint;
	
	public TabbedAreaPainter(Theme theme, boolean enabled) {
		super(theme);
		this.enabled = enabled;
		if (enabled) {
			n1 = Stratus.darken(getTheme().getControl(), 0.15f);
			n2 = Stratus.darken(getTheme().getControl(), 0.1f);
		} else {
			n1 = getTheme().getControl();
			n2 = getTheme().getControl();
		}
		
		if (enabled) {
			t1 = Stratus.darken(getTheme().getControl(), 0.03f);
			t2 = getTheme().getControl();
		} else {
			t1 = getTheme().getControl();
			t2 = getTheme().getControl();
		}
		
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {

		Color c1, c2;
		
		//System.out.println(object);
		//System.out.println(object.getParent().getParent().getParent().getParent());
		
		boolean isTopLevel = (object.getParent().getParent().getParent().getParent() instanceof Window);
		if (isTopLevel) {
			c1 = t1;
			c2 = t2;
			//System.out.println("Top");
		} else {
			c1 = n1;
			c2 = n2;
		}

		//if (width != lastWidth || height != lastHeight || lastPaint == null) {
		lastPaint = new LinearGradientPaint(0, 0, 0, 6, new float[] {0, 1f}, new Color[] {c1, c2});
		lastWidth = width;
		lastHeight = height;
		//}
		
    	g.setPaint(lastPaint);
    	g.fillRect(0, 0, width, height);
    	
    	g.setColor(getTheme().getWidgetBorder());
    	g.drawLine(0, height-1, width, height-1);
		
	}

}
