package org.peakaboo.framework.cyclops.visualization;

import java.lang.ref.SoftReference;

/**
 * Manages the allocation of a {@link Buffer} of a changing size. The provided
 * Buffer will be at least as large as the last call to
 * {@link ManagedBuffer#resize(int, int)}, and possibly somewhat larger.
 */
public class ManagedBuffer {

	private SoftReference<Buffer> buffer = new SoftReference<>(null);
	private int width, height;
	private float oversize;
	
	public ManagedBuffer(float oversize) {
		width = 1;
		height = 1;
		this.oversize = oversize;
	}

	public ManagedBuffer(float oversize, int width, int height) {
		this.width = width;
		this.height = height;
		this.oversize = oversize;
	}

	/**
	 * Sets new size requirements. If the buffer requires resizing, this will clear
	 * it and return true, otherwise it will do nothing and return false.
	 */
	public boolean resize(int width, int height) {
		this.width = width;
		this.height = height;

		boolean needsResize = false;
		Buffer image = buffer.get();
		if (image == null) {
			needsResize = true;
		} else {
			needsResize |= image.getWidth() > width * oversize || image.getHeight() > height * oversize;
			needsResize |= image.getWidth() < width || image.getHeight() < height;
		}

		if (needsResize) {
			buffer = new SoftReference<>(null);
		}
		
		return needsResize;
	}

	/**
	 * Creates (and stores) a new {@link Buffer}. Subsequent calls to
	 * {@link ManagedBuffer#get(Surface)} will return the Buffer created here so
	 * long as it continues to satisfy the size requirements.
	 */
	public Buffer create(Surface forSurface) {
		Buffer image = forSurface.getImageBuffer(width, height);
		buffer = new SoftReference<>(image);
		return image;
	}
	
	/**
	 * Gets the managed buffer if one exists, otherwise returns null.
	 */
	public Buffer get() {
		return buffer.get();
	}

	/**
	 * Gets the managed buffer if one exists and meets the size requirements, otherwise returns null.
	 */
	public Buffer get(int width, int height) {
		resize(width, height);
		return get();
	}

	
	

}
