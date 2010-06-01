package peakaboo.drawing.backends.graphics2d;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import peakaboo.drawing.backends.Buffer;

public class ImageBuffer extends ScreenSurface implements Buffer
{

	private BufferedImage	image;
	
	private boolean 		dirty;
	private int[]			datasource;

 
	public ImageBuffer(int x, int y)
	{
		this(new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB));
	}

	public ImageBuffer(BufferedImage image)
	{
		super((Graphics2D) image.getGraphics());
		this.image = image;
		init();
	}
	private void init()
	{
		datasource = image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), datasource); 
		dirty = false;
	}


	public Object getImageSource()
	{
		commitChanges();
		return image;
	}

	private void commitChanges()
	{
		if (dirty) image.getRaster().setPixels(0, 0, image.getWidth(), image.getHeight(), datasource);
		dirty = false;
	}

	public void setPixelValue(int x, int y, Color c)
	{
		dirty = true;
		int offset = (y * image.getWidth() + x);
		setPixelValue(offset, c);
	}


	public void setPixelValue(int offset, Color c)
	{

		dirty = true;
		offset *= 4;

		int alpha = (int) (c.getAlpha());
		int red = (int) (c.getRed());
		int green = (int) (c.getGreen());
		int blue = (int) (c.getBlue());

		datasource[offset + 3] = alpha;
		datasource[offset + 0] = red;
		datasource[offset + 1] = green;
		datasource[offset + 2] = blue;

		/*
		 * int alpha = 255 << 24; int red = (int)(c.red255)<<16; int green = (int)(c.green255)<<8; int blue =
		 * (int)(c.blue255);
		 * 
		 * d.setElem( offset, alpha + red + green + blue );
		 */

	}

}
