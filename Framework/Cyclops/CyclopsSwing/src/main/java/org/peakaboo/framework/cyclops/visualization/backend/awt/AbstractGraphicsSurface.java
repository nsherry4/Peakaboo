package org.peakaboo.framework.cyclops.visualization.backend.awt;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.composite.BlendComposite;
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

abstract class AbstractGraphicsSurface implements Surface
{

	protected Graphics2D		graphics;
	private GeneralPath			path;
	private BasicStroke			stroke;
	private Stack<Graphics2D>	saveStack;
	
	private CompositeModes		compositeMode;


	public AbstractGraphicsSurface(Graphics2D g)
	{
		
		this.graphics = g;

		graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		saveStack = new Stack<Graphics2D>();

		path = newPath();
		stroke = new BasicStroke();
		graphics.setStroke(stroke);
		
		compositeMode = CompositeModes.OVER;
	}


	private GeneralPath newPath()
	{
		GeneralPath newpath = new GeneralPath();
		newpath.setWindingRule(GeneralPath.WIND_EVEN_ODD);
		newpath.moveTo(0.0f, 0.0f);
		return newpath;
	}


	public void clip()
	{
		graphics.clip(path);
		path = newPath();
	}


	public void fill()
	{
		fillPreserve();
		path = newPath();
	}


	public void fillPreserve()
	{
		path.closePath();
		graphics.fill(path);
	}


	public void lineTo(float x, float y)
	{
		path.lineTo(x, y);
	}
	

	public void moveTo(float x, float y)
	{
		path.moveTo(x, y);
	}
	
	
	public void rectAt(float x, float y, float width, float height) {
		path.append(new Rectangle2D.Float(x, y, width, height), false);
	}
	
	public void roundRectAt(float x, float y, float width, float height, float xradius, float yradius) {
		path.append(new RoundRectangle2D.Float(x, y, width, height, xradius*2, yradius*2), false);
	}


	public void restore()
	{
		graphics = saveStack.pop();
	}


	public void save()
	{
		saveStack.push((Graphics2D) graphics.create());
	}
	
	public void restoreFromMarker(int marker)
	{
		saveStack.setSize(marker);
		graphics = saveStack.pop();
	}

	public int saveWithMarker()
	{
		saveStack.push((Graphics2D) graphics.create());
		return saveStack.size();
	}


	public void setSource(int red, int green, int blue)
	{
		graphics.setColor(new Color(red, green, blue));
	}


	public void setSource(int red, int green, int blue, int alpha)
	{
		graphics.setColor(new Color(red, green, blue, alpha));
	}

	
	public void setSource(float red, float green, float blue)
	{
		graphics.setColor(new Color(red, green, blue));
	}


	public void setSource(float red, float green, float blue, float alpha)
	{
		graphics.setColor(new Color(red, green, blue, alpha));
	}


	public void setSource(PaletteColour c)
	{
		graphics.setColor(new Color(c.getARGB(), true));

	}


	public void stroke()
	{
		strokePreserve();
		path = newPath();
	}


	public void strokePreserve()
	{
		graphics.draw(path);
	}


	public void setSourceGradient(float x1, float y1, PaletteColour colour1, float x2, float y2, PaletteColour colour2)
	{
		GradientPaint gradient = new GradientPaint(x1, y1, new Color(colour1.getARGB(), true), x2,	y2, new Color(colour2.getARGB(), true));

		graphics.setPaint(gradient);
	}


	public void setLineWidth(float width)
	{
		setLineStyle(width, stroke.getEndCap(), stroke.getLineJoin());
	}
	
	public void setLineJoin(LineJoin join)
	{
			
		int joinstyle = 0;
		
		switch (join)
		{
			case BEVEL : joinstyle = BasicStroke.JOIN_BEVEL; break;
			case MITER : joinstyle = BasicStroke.JOIN_MITER; break;
			case ROUND : joinstyle = BasicStroke.JOIN_ROUND; break;
		}
		
		setLineStyle(stroke.getLineWidth(), stroke.getEndCap(), joinstyle);
	}

	public void setLineEnd(EndCap cap)
	{
				
		int capstyle = 0;
		
		switch (cap)
		{
			case BUTT: capstyle = BasicStroke.CAP_BUTT; break;
			case ROUND: capstyle = BasicStroke.CAP_ROUND; break;
			case SQUARE: capstyle = BasicStroke.CAP_SQUARE; break;
		}
		
		setLineStyle(stroke.getLineWidth(), capstyle, stroke.getLineJoin());
	}
	
	public void setLineStyle(float width, EndCap cap, LineJoin join)
	{
				
		setLineWidth(width);
		setLineEnd(cap);
		setLineJoin(join);
		
	}
	
	private void setLineStyle(float width, int cap, int join)
	{
		stroke = new BasicStroke(width, cap, join);
		graphics.setStroke(stroke);
	}

	public void writeText(String text, float x, float y)
	{
		graphics.drawString(text, (int) x, (int) y);
	}



	public float getFontHeight()
	{
		return graphics.getFontMetrics().getHeight();
	}


	public float getFontLeading()
	{
		return graphics.getFontMetrics().getLeading();
	}


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


	public void scale(float x, float y)
	{
		graphics.scale(1.0 / x, 1.0 / y);
	}


	public void translate(float x, float y)
	{
		graphics.translate(x, y);
	}


	public void rotate(float radians)
	{
		graphics.rotate(radians);
	}


	public float getFontSize()
	{
		Font f = graphics.getFont();
		return f.getSize2D();
	}


	public void setFontSize(float size)
	{
		Font f = graphics.getFont();
		f = new Font(f.getName(), f.getStyle(), (int) (size));
		graphics.setFont(f);
	}


	public void useMonoFont()
	{
		setFont("Mono");
	}


	public void useSansFont()
	{
		setFont("Sans");
	}

	public void setFont(String name)
	{
		Font f = graphics.getFont();
		graphics.setFont(new Font(name, f.getStyle(), f.getSize()));
	}
	
	public void setFontBold(boolean bold)
	{
		Font f = graphics.getFont();
		graphics.setFont(f.deriveFont(bold ? Font.BOLD : Font.PLAIN));
	}
	

	public float getFontAscent()
	{
		return graphics.getFontMetrics().getAscent();
	}


	public float getFontDescent()
	{
		return graphics.getFontMetrics().getDescent();
	}



	public void compose(Buffer buffer, int x, int y, float scale)
	{
		BufferedImage image = (BufferedImage) buffer.getImageSource();

		graphics.drawImage(image, 0, 0, (int)(image.getWidth()*scale), (int)(image.getHeight()*scale), null);

		
	}
	
	


	public Buffer getImageBuffer(int x, int y)
	{
		//BufferedImage bi = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		return new ImageBuffer(x, y);
	}


	public void setAntialias(boolean antialias)
	{
		if (antialias)
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
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
	
	public CompositeModes getCompositeMode()
	{
		return compositeMode;
	}


}
