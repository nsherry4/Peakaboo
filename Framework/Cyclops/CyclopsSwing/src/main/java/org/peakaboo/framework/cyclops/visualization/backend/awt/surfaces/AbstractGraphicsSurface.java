package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.composite.BlendComposite;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics.ImageBuffer;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * @author Nathaniel Sherry, 2009
 * 
 *         This class is the implementation of the Surface interface using Java's Graphics2D. Graphics2D is a
 *         mature library, although it has some weaknesses - Lack of build-in scalable surfaces such as
 *         SVG/PS/PDF, Poor type setting/rendering
 * 
 * @see Surface
 * @see Graphics2D
 * 
 */

public abstract class AbstractGraphicsSurface implements Surface
{

	protected Graphics2D		graphics;
	private GeneralPath			path;
	private BasicStroke			stroke;
	private Stack<Graphics2D>	saveStack;
	
	private CompositeModes		compositeMode;
	protected SurfaceDescriptor descriptor;

	protected AbstractGraphicsSurface(Graphics2D g, SurfaceDescriptor descriptor)
	{
		this.descriptor = descriptor;
		this.graphics = g;

		graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		// In case we inherit from a graphics context with a non-standard font set.
		graphics.setFont(new Font("Dialog", Font.PLAIN, 12));

		saveStack = new Stack<>();

		path = newPath();
		stroke = new BasicStroke();
		graphics.setStroke(stroke);
		
		compositeMode = CompositeModes.OVER;
	}

	private GeneralPath newPath()
	{
		GeneralPath newpath = new GeneralPath();
		newpath.setWindingRule(Path2D.WIND_EVEN_ODD);
		newpath.moveTo(0.0f, 0.0f);
		return newpath;
	}

	@Override
	public void clip()
	{
		graphics.clip(path);
		path = newPath();
	}

	@Override
	public void fill()
	{
		fillPreserve();
		path = newPath();
	}

	@Override
	public void fillPreserve()
	{
		path.closePath();
		graphics.fill(path);
	}

	@Override
	public void lineTo(float x, float y)
	{
		path.lineTo(x, y);
	}
	
	@Override
	public void moveTo(float x, float y)
	{
		path.moveTo(x, y);
	}
	
	@Override
	public void rectAt(float x, float y, float width, float height) {
		path.append(new Rectangle2D.Float(x, y, width, height), false);
	}
	
	@Override
	public void roundRectAt(float x, float y, float width, float height, float xradius, float yradius) {
		path.append(new RoundRectangle2D.Float(x, y, width, height, xradius*2, yradius*2), false);
	}

	@Override
	public void restore()
	{
		graphics = saveStack.pop();
	}

	@Override
	public void save()
	{
		saveStack.push((Graphics2D) graphics.create());
	}
	
	@Override
	public void restoreFromMarker(int marker)
	{
		saveStack.setSize(marker);
		graphics = saveStack.pop();
	}
	
	@Override
	public int saveWithMarker()
	{
		saveStack.push((Graphics2D) graphics.create());
		return saveStack.size();
	}

	@Override
	public void setSource(int red, int green, int blue)
	{
		graphics.setColor(new Color(red, green, blue));
	}

	@Override
	public void setSource(int red, int green, int blue, int alpha)
	{
		graphics.setColor(new Color(red, green, blue, alpha));
	}

	@Override
	public void setSource(float red, float green, float blue)
	{
		graphics.setColor(new Color(red, green, blue));
	}

	@Override
	public void setSource(float red, float green, float blue, float alpha)
	{
		graphics.setColor(new Color(red, green, blue, alpha));
	}

	@Override
	public void setSource(PaletteColour c)
	{
		graphics.setColor(new Color(c.getARGB(), true));

	}

	@Override
	public void stroke()
	{
		strokePreserve();
		path = newPath();
	}

	@Override
	public void strokePreserve()
	{
		graphics.draw(path);
	}

	@Override
	public void setSourceGradient(float x1, float y1, PaletteColour colour1, float x2, float y2, PaletteColour colour2)
	{
		GradientPaint gradient = new GradientPaint(x1, y1, new Color(colour1.getARGB(), true), x2,	y2, new Color(colour2.getARGB(), true));

		graphics.setPaint(gradient);
	}

	@Override
	public void setLineWidth(float width)
	{
		setLineStyle(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getDashArray(), stroke.getDashPhase());
	}
	
	@Override
	public void setLineJoin(LineJoin join)
	{
			
		int joinstyle = 0;
		
		switch (join)
		{
			case BEVEL : joinstyle = BasicStroke.JOIN_BEVEL; break;
			case MITER : joinstyle = BasicStroke.JOIN_MITER; break;
			case ROUND : joinstyle = BasicStroke.JOIN_ROUND; break;
		}
		
		setLineStyle(stroke.getLineWidth(), stroke.getEndCap(), joinstyle, stroke.getDashArray(), stroke.getDashPhase());
	}

	@Override
	public void setLineEnd(EndCap cap)
	{
				
		int capstyle = 0;
		
		switch (cap)
		{
			case BUTT: capstyle = BasicStroke.CAP_BUTT; break;
			case ROUND: capstyle = BasicStroke.CAP_ROUND; break;
			case SQUARE: capstyle = BasicStroke.CAP_SQUARE; break;
		}
		
		setLineStyle(stroke.getLineWidth(), capstyle, stroke.getLineJoin(), stroke.getDashArray(), stroke.getDashPhase());
	}
	
	
	@Override
	public void setDashedLine(Dash dash) {
		if (dash == null) {
			setLineStyle(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), null, 0);
		} else {
			setLineStyle(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), dash.pattern(), dash.offset());
		}
		
	}
	

	@Override
	public void setLineStyle(float width, EndCap cap, LineJoin join)
	{

		setLineWidth(width);
		setLineEnd(cap);
		setLineJoin(join);
		setDashedLine(null);
		
	}

	@Override
	public void setLineStyle(float width, EndCap cap, LineJoin join, Dash dash)
	{
				
		setLineWidth(width);
		setLineEnd(cap);
		setLineJoin(join);
		setDashedLine(dash);
		
	}
	
	private void setLineStyle(float width, int cap, int join, float[] dashPattern, float dashOffset)
	{
		if (dashPattern == null) {
			stroke = new BasicStroke(width, cap, join, stroke.getMiterLimit());
		} else {
			stroke = new BasicStroke(width, cap, join, stroke.getMiterLimit(), dashPattern, dashOffset);
		}
		graphics.setStroke(stroke);
	}

	@Override
	public void writeText(String text, float x, float y)
	{
		graphics.drawString(text, x, y);
	}


	@Override
	public float getFontHeight()
	{
		return graphics.getFontMetrics().getHeight();
	}

	@Override
	public float getFontLeading()
	{
		return graphics.getFontMetrics().getLeading();
	}

	@Override
	public float getTextWidth(String text)
	{
		if (text == null || "".equals(text)) return 0.0f;
		
		try {
			TextLayout layout = new TextLayout(text, graphics.getFont(), graphics.getFontRenderContext());
			return (float) (layout.getBounds().getWidth() + layout.getBounds().getX());
		} catch (Throwable e) {
			CyclopsLog.get().log(Level.SEVERE, "Failed to determine text width", e);
		}

		return 0.0f;
		
	}

	@Override
	public void scale(float x, float y)
	{
		graphics.scale(1.0 / x, 1.0 / y);
	}

	@Override
	public void translate(float x, float y)
	{
		graphics.translate(x, y);
	}

	@Override
	public void rotate(float radians)
	{
		graphics.rotate(radians);
	}

	@Override
	public float getFontSize()
	{
		Font f = graphics.getFont();
		return f.getSize2D();
	}

	@Override
	public void setFontSize(float size)
	{
		Font f = graphics.getFont();
		f = new Font(f.getName(), f.getStyle(), (int) (size));
		f = f.deriveFont(size);
		graphics.setFont(f);
	}

	@Override
	public void useMonoFont()
	{
		setFont("Mono");
	}

	@Override
	public void useSansFont()
	{
		setFont("Sans");
	}
	
	@Override
	public void setFont(String name)
	{
		Font current = graphics.getFont();
		Font font = new Font(name, current.getStyle(), current.getSize());
		// Important! Don't let the API round the font size to the nearest integer
		font = font.deriveFont(current.getSize2D());
		graphics.setFont(font);
	}
	
	@Override
	public void setFontBold(boolean bold)
	{
		Font f = graphics.getFont();
		graphics.setFont(f.deriveFont(bold ? Font.BOLD : Font.PLAIN));
	}
	
	@Override
	public float getFontAscent()
	{
		return graphics.getFontMetrics().getAscent();
	}

	@Override
	public float getFontDescent()
	{
		return graphics.getFontMetrics().getDescent();
	}


	@Override
	public void compose(Buffer buffer, int x, int y, float scale)
	{
		BufferedImage image = (BufferedImage) buffer.getImageSource();
		graphics.drawImage(image, x, y, (int)(image.getWidth()*scale), (int)(image.getHeight()*scale), null);
	}
	
	

	@Override
	public Buffer getImageBuffer(int x, int y)
	{
		return new ImageBuffer(x, y);
	}

	@Override
	public void setAntialias(boolean antialias)
	{
		if (antialias)
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	@Override
	public void setCompositeMode(CompositeModes mode)
	{
		
		Composite c = null;
		
		compositeMode = mode;
		
		switch (mode)
		{
			case OVER: c = AlphaComposite.SrcOver; 	break;
			case OUT: c = AlphaComposite.SrcOut;	break;
			case IN: c = AlphaComposite.SrcIn;		break;
			case ATOP: c = AlphaComposite.SrcAtop;	break;
			case SOURCE: c = AlphaComposite.Src;	break;
			case XOR: c = AlphaComposite.Xor;		break;
			case ADD: c = BlendComposite.Add;		break;
				
				
				
		}
		
		graphics.setComposite(c);
		
	}
	
	@Override
	public CompositeModes getCompositeMode()
	{
		return compositeMode;
	}
	
	@Override
	public SurfaceDescriptor getSurfaceDescriptor() {
		return descriptor;
	}


}
