package org.peakaboo.framework.stratus.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

/**
 * Manages the allocation of a {@link BufferedImage} of a changing size. The
 * provided BufferedImage will be at least as large as the last call to
 * {@link ManagedImageBuffer#resize(int, int)}, and possibly somewhat larger.
 * This helps reduce the number of large buffer allocations by reusing the same
 * buffer when possible.
 */
public class ManagedImageBuffer {

	private SoftReference<BufferedImage> buffer = new SoftReference<>(null);
	private int width, height;
	private boolean unpainted = true;
	
	public ManagedImageBuffer() {
		this(100, 100);
	}

	public ManagedImageBuffer(int width, int height) {
		resize(width, height);
	}

	public void clear() {
		clear(new Color(255, 255, 255, 0));
	}

	public void clear(Color c) {
		BufferedImage image = buffer.get();
		if (image == null) {
			return;
		}
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(c);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
	}

	/**
	 * Sets new size requirements. If the buffer requires resizing, this will clear
	 * it.
	 */
	public void resize(int width, int height) {
		
		boolean resized = width != this.width || height != this.height;
		if (resized) {
			markUnpainted();
		}
		
		this.width = width;
		this.height = height;
				
		boolean createBuffer = false;
		BufferedImage image = buffer.get();
		if (image == null) {
			createBuffer = true;
		} else {
			createBuffer |= image.getWidth() > width * 1.2 || image.getHeight() > height * 1.2;
			createBuffer |= image.getWidth() < width || image.getHeight() < height;
		}
		
		if (createBuffer) {
			create();
		}
	}

	private BufferedImage create() {
		BufferedImage image = Stratus.acceleratedImage((int) (width * 1.2), (int) (height * 1.2));
		buffer = new SoftReference<BufferedImage>(image);
		markUnpainted();
		return image;
	}
	
	/**
	 * Gets the managed buffer. This buffer may or may not have been cleared.
	 */
	public BufferedImage get() {
		BufferedImage image = buffer.get();
		if (image == null) {
			image = create();
		}
		return image;
	}

	/**
	 * Gets the managed buffer, ensuring that it meets the given size requirements.
	 * This buffer may or may not have been cleared.
	 */
	public BufferedImage get(int width, int height) {
		resize(width, height);
		return get();
	}
	
	/**
	 * Mark the managed buffer as being in an unpainted state, needing to be
	 * repainted before it can be used. The buffer will be considered unpainted at
	 * initialization, or after a call to {@link #resize(int, int)} where the size
	 * actually changes. painted.
	 */
	public void markUnpainted() {
		unpainted = true;
	}
	
	/**
	 * Mark the managed buffer as being in a painted state. Clients should call this
	 * method after painting the buffer as desired.
	 */
	public void markPainted() {
		unpainted = false;
	}
	
	/**
	 * Indicates that this buffer is in a painted state, and does not need to be
	 * repainted before being used.
	 * 
	 * @return
	 */
	public boolean isPainted() {
		return !unpainted;
	}

}
