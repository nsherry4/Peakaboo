package cyclops.visualization.backend.awt;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cyclops.visualization.Buffer;
import cyclops.visualization.palette.PaletteColour;

public class ImageBuffer extends ScreenSurface implements Buffer
{

	private BufferedImage	image;
	
	private boolean 		dirty = false;
	private int[]			datasource;

 
	public ImageBuffer(int x, int y)
	{
		this(new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB));
	}

	public ImageBuffer(BufferedImage image)
	{
		super((Graphics2D) image.getGraphics());
		this.image = image;
	}
	private void init()
	{
		datasource = image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), datasource); 
	}


	public Object getImageSource()
	{
		commitChanges();
		return image;
	}

	private void commitChanges()
	{
		if (dirty) {
			image.getRaster().setPixels(0, 0, image.getWidth(), image.getHeight(), datasource);
		}
		dirty = false;
	}

	public void setPixelValue(int x, int y, PaletteColour c)
	{
		dirty = true;
		int offset = (y * image.getWidth() + x);
		setPixelValue(offset, c);
	}


	public void setPixelValue(int offset, PaletteColour c)
	{

		if (datasource == null) {
			init();
		}
		
		dirty = true;
		offset *= 4;

		int alpha = c.getAlpha();
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		datasource[offset + 3] = alpha;
		datasource[offset + 0] = red;
		datasource[offset + 1] = green;
		datasource[offset + 2] = blue;

	}

}
