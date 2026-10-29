package org.peakaboo.display;

import java.util.function.Predicate;

import org.peakaboo.app.PeakabooConfiguration;
import org.peakaboo.app.PeakabooConfiguration.MemorySize;
import org.peakaboo.app.Settings;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.visualization.ExportableSurface;
import org.peakaboo.framework.cyclops.visualization.Surface;

public class Display {

	public static final float OVERSIZE = 1.1f;

	// Pluggable policy for when to use /skip buffering.
	private static Predicate<Coord<Integer>> bufferPolicy = Display::heapBufferPolicy;

	private Display() {
		//Not Constructable
	}

	// We force a light background on exports if the setting is enabled
	public static boolean forceLightBackground(Surface context) {
		return context instanceof ExportableSurface && Settings.isLightExports();
	}

	/**
	 * Returns true if a drawing of the given size should be rendered via a
	 * buffer, as determined by the current buffer policy
	 */
	public static boolean useBuffer(Coord<Integer> size) {
		return bufferPolicy.test(size);
	}

	/**
	 * Returns the policy used by {@link #useBuffer(Coord)}
	 */
	public static Predicate<Coord<Integer>> getBufferPolicy() {
		return bufferPolicy;
	}

	/**
	 * Sets the policy used by {@link #useBuffer(Coord)}. Platforms where graphics
	 * memory isn't on-heap should replace this. Passing null resets to default.
	 * {@link #heapBufferPolicy(Coord)}.
	 */
	public static void setBufferPolicy(Predicate<Coord<Integer>> policy) {
		bufferPolicy = policy == null ? Display::heapBufferPolicy : policy;
	}

	/**
	 * The default buffer policy, which assumes buffers are allocated on the heap and
	 * declines to buffer when the buffer would be large relative to the available heap.
	 */
	public static boolean heapBufferPolicy(Coord<Integer> size) {

		boolean doBuffer = true;

		int bufferSpace = (int)((size.x * OVERSIZE * size.y * OVERSIZE * 4) / 1024f / 1024f);
		if (bufferSpace > 10 && PeakabooConfiguration.memorySize == MemorySize.TINY) {
			doBuffer = false;
		}
		if (bufferSpace > 25 && PeakabooConfiguration.memorySize == MemorySize.SMALL) {
			doBuffer = false;
		}
		if (bufferSpace > 100 && PeakabooConfiguration.memorySize == MemorySize.MEDIUM) {
			doBuffer = false;
		}
		if (bufferSpace > 400 && PeakabooConfiguration.memorySize == MemorySize.LARGE) {
			doBuffer = false;
		}

		Runtime rt = Runtime.getRuntime();
		long used = rt.totalMemory() - rt.freeMemory();
		int headroom = (int) ((rt.maxMemory() - used) / 1024f / 1024f);
		if (bufferSpace * 1.5f > headroom) {
			doBuffer = false;
		}

		return doBuffer;

	}

}
