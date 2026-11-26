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
			// A properly oversized buffer is already width * oversize large. We want to
			// check for something that's larger than *that* by a factor of `oversize`.
			float slack = oversize * oversize;
			needsResize |= image.getWidth() > width * slack || image.getHeight() > height * slack;
			needsResize |= image.getWidth() < width || image.getHeight() < height;
		}

		if (needsResize) {
			invalidate();
		}

		return needsResize;
	}

	/** Discards the buffer so calls to {@link #get(int, int)} return null **/
	public void invalidate() {
		buffer = new SoftReference<>(null);
	}

	/**
	 * Creates (and stores) a new {@link Buffer} sized *beyond* the current
	 * size requirements. Subsequent calls to {@link ManagedBuffer#get(int, int)}
	 * will return the Buffer created here so long as it continues to satisfy the
	 * size requirements.
	 */
	public Buffer create(Surface forSurface) {
		Buffer image = forSurface.getImageBuffer(pad(width), pad(height));
		buffer = new SoftReference<>(image);
		return image;
	}

	private int pad(int size) {
		return Math.max(1, (int) Math.ceil(size * oversize));
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
