package peakaboo.drawing.backends.cairo;

/**
 * @author Nathaniel Sherry, 2009
 * 
 *  This class is the implementation of the Backend interface using the Cairo
 *  graphics library. This graphics library has native support for
 *  - Image Surfaces (eg png)
 *  - Xlib 
 *  - Win32
 *  - Quartz
 *  - SVG
 *  - PS
 *  - PDF
 *  and other experimental ones such as OS/2
 *
 *	This is ideal. as scalable formats such as SVG/PS/PDF would be good for
 *  print publications.
 *  
 *  Unfortunately, the Java bindings are immature at this point, and so this backend
 *  is not yet used.
 *
 */

class CairoSurface{}

/*
 * Cairo backend not properly supported yet, as Java-Gnome bindings are incomplete (but maturing rapidly)
 * This has been disabled in order to remove the dependancy for now.
 * To enable, uncomment this class, and also uncomment its entry in DrawingBackendFactory.java
 * 
 */


/*
import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.LinearPattern;
import org.freedesktop.cairo.Surface;

import peakaboo.drawing.Colour;

public class CairoBackend implements Backend {

	private Context context;
	
	public CairoBackend(Surface surface){
		context = new Context(surface);
	}
	
	public void fill(){ context.fill(); }
	public void fillPreserve(){ context.fillPreserve(); }
	
	public void stroke(){ context.stroke(); }
	public void strokePreserve(){ context.strokePreserve(); }
	
	public void moveTo(double x, double y){ context.moveTo(x, y); }
	public void lineTo(double x, double y){ context.lineTo(x, y); }
	
	public void setSource(double red, double green, double blue){
		context.setSource(red, green, blue);
	}
	public void setSource(double red, double green, double blue, double alpha){
		context.setSource(red, green, blue, alpha);
	}
	
	public void save(){context.save();}
	public void restore(){context.restore();}
	public void clip(){context.clip();}
	
	public void rectangle(double x, double y, double width, double height){
		context.rectangle(x, y, width, height);
	}

	@Override
	public void setSourceGradient(double x1, double y1, Colour colour1,
			double x2, double y2, Colour colour2) {
		// TODO Auto-generated method stub
		//fill with gradient green
		LinearPattern gradient = new LinearPattern(x1, y1, x2, y2);
		
		gradient.addColorStopRGB(0, colour1.red, colour1.green, colour1.blue);
		gradient.addColorStopRGB(1, colour2.red, colour2.green, colour2.blue);
		
		context.setSource(gradient);
	}

	public void setLineWidth(double width){
		context.setLineWidth(width);
	}

	
	public void writeText(String text, double x, double y)
	{
		
	}

	
	@Override
	public double getFontHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTextWidth(String text) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void scale(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getFontSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFontSize(double size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useMonoFont() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useSansFont() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getFontAscent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getFontDescent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void rotate(double radians) {
		// TODO Auto-generated method stub
		context.rotate(radians);
	}
	
}
*/