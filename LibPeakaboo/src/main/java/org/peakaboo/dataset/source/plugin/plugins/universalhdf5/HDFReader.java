package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Backend-neutral view of an open HDF5 file, exposing just the I/O operations the
 * universalhdf5 data sources need. This lets the same block-read algo run against
 * either the native or pure-Java library; pick a backend with {@link HDFReaders}.
 *
 * @author NAS
 */
public interface HDFReader extends Closeable {

	
	// PATH OPERATIONS
	
	/** Whether a node (group or dataset) exists at the given path. */
	boolean exists(String path);
	
	/** Whether the node at the given path is a group (as opposed to a dataset). */
	boolean isGroup(String path);
	
	/** The names of the immediate children of the group at the given path. */
	List<String> groupMembers(String path);
	
	
	
	// DATASET DIMENSIONS & EXTENTS
	
	/** The file dimensions (extents) of the dataset at the given path. */
	int[] dimensions(String dataPath);
	
	/**
	 * The chunk extent along the given file axis, or 1 if
	 * <ul>
	 * <li>the axis is not present</li>
	 * <li>the dataset is not chunked</li>
	 * <li>no chunk size is reported.</li>
	 * </ul>
	 * Reading whole chunks lets compressed chunks be decompressed exactly once.
	 */
	int chunkExtent(String dataPath, int axis);
	
	/** The number of dimensions of the dataset at the given path. */
	default int rank(String dataPath) {
		return dimensions(dataPath).length;
	}
	
	/** The total number of elements in the dataset at the given path. */
	default long numberOfElements(String dataPath) {
		long count = 1;
		for (int dim : dimensions(dataPath)) {
			count *= dim;
		}
		return count;
	}
	
	
	
	// IO OPERATIONS
	
	/**
	 * Reads a slice of the dataset (offset + extents in file-dimension order).
	 * Returns it as a single flat array in row-major order (the last file dimension varies
	 * fastest). Ideally, the backend will decompress only the chunks overlapping the
	 * requested region.
	 */
	float[] readBlock(String dataPath, long[] offset, int[] range);
	
	/**
	 * Reads an entire dataset as a single, flat float array in  row-major order (the last
	 * file dimension varies fastest). This is a very memory intensive way to read a
	 * dataset and is only recommended for smaller, supporting data like eg deadtime values.
	 */
	float[] readFloatArray(String dataPath);
	
	/**
	 * Reads an entire dataset as a single, flat double array in  row-major order (the last
	 * file dimension varies fastest). This is a very memory intensive way to read a
	 * dataset and is only recommended for smaller, supporting data like eg deadtime values.
	 */
	double[] readDoubleArray(String dataPath);
	
	@Override
	void close() throws IOException;



}
