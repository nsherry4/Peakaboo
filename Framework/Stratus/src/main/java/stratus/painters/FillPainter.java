package stratus.painters;

import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JComponent;
import javax.swing.Painter;

//Fills an area with a Paint
public class FillPainter implements Painter<JComponent> {

    private final Paint paint;

    public FillPainter(Paint p) { paint = p; }

    @Override
    public void paint(Graphics2D g, JComponent object, int width, int height) {
        //g.setColor(color);
        g.setPaint(paint);
        g.fillRect(0, 0, width-1, height-1);
    }

}