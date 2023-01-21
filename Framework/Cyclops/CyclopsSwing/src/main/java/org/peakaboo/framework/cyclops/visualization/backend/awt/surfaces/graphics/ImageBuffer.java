package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class ImageBuffer extends ScreenSurface implements Buffer
{

	private BufferedImage	image;
	
	private boolean 		dirty = false;
	private int[]			datasource;

 
	public ImageBuffer(int x, int y) {
		this(new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB));
	}

	public ImageBuffer(BufferedImage image) {
		super((Graphics2D) image.getGraphics());
		this.image = image;
	}
	private synchronized void init()
	{
		if (datasource == null) {
			datasource = image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), datasource); 
		}
	}


	public BufferedImage getImageSource()
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
	
	public void clear() {
		if (datasource == null) {
			init();
		}
		Arrays.fill(datasource, 0);
		commitChanges();
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

		datasource[offset + 3] = c.getAlpha();
		datasource[offset + 0] = c.getRed();
		datasource[offset + 1] = c.getGreen();
		datasource[offset + 2] = c.getBlue();

	}
	
	

	@Override
	public PaletteColour getPixelValue(int x, int y) {
		int index = (y * image.getWidth() + x);
		return getPixelValue(index);
	}

	@Override
	public PaletteColour getPixelValue(int index) {
		
		if (datasource == null) {
			init();
		}
		
		int offset = index * 4;
		
		return new PaletteColour(
				datasource[offset+3], 
				datasource[offset+0], 
				datasource[offset+1], 
				datasource[offset+2]
			);
	}

	@Override
	public List<PaletteColour> getPixelValues() {
		List<PaletteColour> pixels = new ArrayList<>();
		for (int i = 0; i < getSize(); i++) {
			pixels.add(getPixelValue(i));
		}
		return pixels;
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}

	@Override
	public List<Integer> getPixelsARGB() {
		List<Integer> pixels = new ArrayList<>();
		for (int i = 0; i < getSize(); i++) {
			pixels.add(getPixelARGB(i));
		}
		return pixels;
	}

	@Override
	public int getPixelARGB(int index) {
		int offset = index * 4;
		
		int argb = 0;
		argb += datasource[offset+3] << 24;
		argb += datasource[offset+0] << 16;
		argb += datasource[offset+1] << 8;
		argb += datasource[offset+2];
		
		return argb;
		
	}

	@Override
	public int getPixelARGB(int x, int y) {
		int index = (y * image.getWidth() + x);
		return getPixelARGB(index);
	}

}
