package stratus.painters;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Painter;

//Fills a component with a series of other painters in order
public class CompositePainter implements Painter<JComponent> {

	private List<Painter<JComponent>> painters;
	
	public CompositePainter(Painter<JComponent>... painters) {
		this(new ArrayList<>(Arrays.asList(painters)));
	}
	
	public CompositePainter(List<Painter<JComponent>> painters) {
		this.painters = painters;
	}
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		for (Painter<JComponent> painter : painters) {
			painter.paint(g, object, width, height);
		}
	}

}
