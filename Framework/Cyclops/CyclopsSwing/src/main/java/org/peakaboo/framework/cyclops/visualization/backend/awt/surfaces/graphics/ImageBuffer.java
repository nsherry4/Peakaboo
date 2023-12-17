package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;
import org.peakaboo.framework.stratus.api.Stratus;

public class ImageBuffer extends ScreenSurface implements Buffer
{

	private BufferedImage	image;
	
	private boolean 		dirty = false;
	private int[]			datasource;

 
	public ImageBuffer(int x, int y) {
		this(Stratus.acceleratedImage(x, y));
	}

	public ImageBuffer(BufferedImage image) {
		super((Graphics2D) image.getGraphics());
		this.image = image;
	}
	private synchronized void init()
	{
		if (datasource == null) {
			datasource = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		}
	}

	@Override
	public BufferedImage getImageSource()
	{
		commitChanges();
		return image;
	}

	private void commitChanges()
	{
		if (dirty) {
			image.setRGB(0, 0, image.getWidth(), image.getHeight(), datasource, 0, image.getWidth());
		}
		dirty = false;
	}
	
	@Override
	public void clear() {
		if (datasource == null) {
			init();
		}
		Arrays.fill(datasource, 0);
		commitChanges();
	}

	@Override
	public void setPixelValue(int x, int y, PaletteColour c)
	{
		dirty = true;
		int offset = (y * image.getWidth() + x);
		setPixelValue(offset, c);
	}

	@Override
	public void setPixelValue(int offset, PaletteColour c)
	{

		if (datasource == null) {
			init();
		}
		
		dirty = true;
		
		datasource[offset] = c.getARGB();

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
		
		return new PaletteColour(datasource[index]);
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
		return datasource[index];	
	}

	@Override
	public int getPixelARGB(int x, int y) {
		int index = (y * image.getWidth() + x);
		return getPixelARGB(index);
	}

}
