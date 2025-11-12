package org.peakaboo.framework.cyclops.visualization.backend.awt;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JPanel;

import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.visualization.ExportableSurface;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.graphics.ScreenSurface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;


/**
 * 
 * This is an abstract class for controllers which draw something using a supported graphics backend. The given graphics context will be wrapped in a related {@link Surface} to be drawn to
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public abstract class GraphicsPanel extends JPanel {

	private boolean buffer = false;
	private float bufferSlack = 1.2f;
	private VolatileImage bimage;

	protected GraphicsPanel() {
		System.setProperty("sun.java2d.opengl", "True");
	}
	
	protected GraphicsPanel(boolean buffered) {
		this();
		buffer = buffered;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Coord<Integer> size = new Coord<>(getWidth(), getHeight());
		
		if (buffer) {
			if (
					bimage == null || 
					bimage.getWidth() < getWidth() || bimage.getWidth() > getWidth() * (bufferSlack*bufferSlack) ||
					bimage.getHeight() < getHeight() || bimage.getHeight() > getHeight() * (bufferSlack*bufferSlack)
					){
				createBackBuffer();
			}
					
			Graphics bg = bimage.getGraphics();
			draw((Graphics2D) bg, size);
			g.drawImage(bimage, 0, 0, this);
		
		} else {

			draw((Graphics2D) g, size);
			
		}
	}
	
	private void createBackBuffer() {
		bimage = getGraphicsConfiguration().createCompatibleVolatileImage(
					(int)(getWidth()*bufferSlack), 
					(int)(getHeight()*bufferSlack), 
					Transparency.OPAQUE
				);
		bimage.setAccelerationPriority(1f);
	}
	
	private void draw(Graphics2D g, Coord<Integer> size) {
		Surface surface = new ScreenSurface(g);
		drawGraphics(surface, size);
	}

	public void write(SurfaceDescriptor descriptor, OutputStream out, Coord<Integer> size) throws IOException {		
		ExportableSurface surface = (ExportableSurface) descriptor.create(size);
		drawGraphics(surface, size);
		surface.write(out);
	}



	/**
	 * Draw to the given Surface
	 * @param backend the surface to draw to
	 * @param vector indicates if this drawing is to a vector surface
	 * @param size The dimensions of the image to draw. This is not the same as 
	 * the dimensions of the surface to draw on (eg window size)
	 */
	protected abstract void drawGraphics(Surface backend, Coord<Integer> size);


	/**
	 * Report on how wide the actual drawing is. Since drawings may not always fit 
	 * the window they are shown in, it is important for the export functionality to
	 * know how large the desired drawing actually is
	 * @return the actual width of the drawing
	 */
	public abstract float getUsedWidth();
	public abstract float getUsedWidth(float zoom);

	
	/**
	 * Report on how high the actual drawing is. Since drawings may not always fit 
	 * the window they are shown in, it is important for the export functionality to
	 * know how large the desired drawing actually is
	 * @return the actual height of the drawing
	 */
	public abstract float getUsedHeight();
	public abstract float getUsedHeight(float zoom);

}
