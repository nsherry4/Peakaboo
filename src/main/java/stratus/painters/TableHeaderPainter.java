package stratus.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;
import javax.swing.Painter;

import stratus.Stratus;
import stratus.theme.Theme;

public class TableHeaderPainter extends SimpleThemed implements Painter<JComponent>{

	
	
	public TableHeaderPainter(Theme theme) {
		super(theme);
	}

	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		// TODO Auto-generated method stub
		
		
		g.setPaint(new LinearGradientPaint(0, 0, 0, height, new float[] {0f, 1}, new Color[] {getTheme().getTableHeader(), getTheme().getControl()}));
		g.fillRect(-1, 0, width+1, height-1);
		
		g.setColor(getTheme().getTableHeader());
		g.fillRect(-1, 0, width, height-1);
		
		
		
	}
	
	

}
