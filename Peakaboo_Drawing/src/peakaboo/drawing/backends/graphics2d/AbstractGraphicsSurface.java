package peakaboo.drawing.backends.graphics2d;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Stack;

import peakaboo.drawing.backends.Buffer;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.backends.graphics2d.composite.BlendComposite;

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
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
				java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		saveStack = new Stack<Graphics2D>();

		path = newPath();
		
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


	public void rectangle(float x, float y, float width, float height)
	{
		path.moveTo(x, y);
		path.lineTo(x, (y + height));
		path.lineTo((x + width), (y + height));
		path.lineTo((x + width), y);
		path.lineTo(x, y);
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


	public void setSource(Color c)
	{
		graphics.setColor(c);

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


	public void setSourceGradient(float x1, float y1, Color colour1, float x2, float y2, Color colour2)
	{
		GradientPaint gradient = new GradientPaint(x1, y1, colour1, x2,	y2, colour2);

		graphics.setPaint(gradient);
	}


	public void setLineWidth(float width)
	{
		stroke = new BasicStroke(width);
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
		
		TextLayout layout = new TextLayout(text, graphics.getFont(), graphics.getFontRenderContext());

		return (float) (layout.getBounds().getWidth() + layout.getBounds().getX());
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
		Font f = graphics.getFont();
		graphics.setFont(new Font("Mono", f.getStyle(), f.getSize()));
	}


	public void useSansFont()
	{
		Font f = graphics.getFont();
		graphics.setFont(new Font("Sans", f.getStyle(), f.getSize()));
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


		BufferedImageOp op = new AffineTransformOp(new AffineTransform(scale, 0, 0, scale, 0, 0),
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		graphics.drawImage(image, op, x, y);
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
