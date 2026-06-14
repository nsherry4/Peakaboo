package org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics;


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * A {@link Buffer} backed by a {@link BufferedImage}.
 * <p>
 * The backing image is always a {@link BufferedImage#TYPE_INT_ARGB} image, so pixel
 * access reads and writes the image's live {@code int[]} raster directly. This avoids
 * the full-image {@code getRGB}/{@code setRGB} copies the old implementation paid on
 * every buffer build, which matters a great deal for large maps.
 * <p>
 * {@code TYPE_INT_ARGB} is <em>non-premultiplied</em>, so the stored {@code int}
 * values are exactly the straight ARGB values passed to {@link #setPixelARGB}, which
 * keeps semi-transparent pixels (e.g. selection masks) correct.
 * <p>
 * Grabbing the live raster array disables Java2D's managed-image acceleration for that
 * image, which is fine for buffers we fill pixel-by-pixel on the CPU. Buffers that are
 * only ever drawn to via {@link Graphics2D} (e.g. a large compositing buffer) never
 * touch the pixel array — {@link #clear()} uses {@link AlphaComposite#Clear} in that
 * case — so they remain managed and accelerated.
 */
public class ImageBuffer extends ScreenSurface implements Buffer
{

	private final BufferedImage	image;

	/**
	 * The image's live raster array, lazily fetched on first pixel access. Null until
	 * then, so Graphics2D-only buffers never grab it (and stay managed/accelerated).
	 */
	private int[]				datasource;


	public ImageBuffer(int x, int y) {
		this(new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB));
	}

	private ImageBuffer(BufferedImage image) {
		super((Graphics2D) image.getGraphics());
		this.image = image;
	}

	/**
	 * Lazily obtains the image's live raster array. Writes through it are immediately
	 * visible on the image.
	 */
	private int[] pixels() {
		if (datasource == null) {
			datasource = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		}
		return datasource;
	}

	@Override
	public BufferedImage getImageSource()
	{
		// The live raster is the source of truth - nothing to flush.
		return image;
	}

	@Override
	public void clear() {
		if (datasource != null) {
			// We've already taken the pixel array; clear it in place.
			Arrays.fill(datasource, 0);
		} else {
			// Clear via the graphics context so the image stays managed/accelerated.
			// This is the path large compositing buffers take, since they are never
			// pixel-poked and so never grab the raster array.
			Graphics2D g = (Graphics2D) image.getGraphics();
			try {
				g.setComposite(AlphaComposite.Clear);
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
			} finally {
				g.dispose();
			}
		}
	}

	@Override
	public void setPixelValue(int x, int y, PaletteColour c) {
		setPixelValue(y * image.getWidth() + x, c);
	}

	@Override
	public void setPixelValue(int offset, PaletteColour c) {
		setPixelARGB(offset, c.getARGB());
	}

	@Override
	public void setPixelARGB(int offset, int c) {
		pixels()[offset] = c;
	}


	@Override
	public PaletteColour getPixelValue(int x, int y) {
		int index = (y * image.getWidth() + x);
		return getPixelValue(index);
	}

	@Override
	public PaletteColour getPixelValue(int index) {
		return new PaletteColour(pixels()[index]);
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
		return pixels()[index];
	}

	@Override
	public int getPixelARGB(int x, int y) {
		int index = (y * image.getWidth() + x);
		return getPixelARGB(index);
	}

}
