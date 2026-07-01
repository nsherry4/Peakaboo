package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.peakaboo.dataset.io.DataInputAdapter;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import io.jhdf.api.dataset.ChunkedDataset;
import io.jhdf.exceptions.HdfInvalidPathException;

/**
 * {@link HDFReader} backed by the pure-Java jhdf library. This is the fallback backend
 * used when the native HDF5 library is unavailable (eg Android, non-x86/Apple)
 *
 * @author NAS
 */
public class JHDFReader implements HDFReader {
	
	private HdfFile file;
	
	public JHDFReader(DataInputAdapter path) throws IOException {
		this.file = new HdfFile(path.getAndEnsurePath());
	}
	
	private Dataset getDataset(String path) {
		return file.getDatasetByPath(normalizePath(path));
	}
	
	/**
	 * Resolves a path to its node, accepting the lenient path forms the native reader
	 * tolerates: jhdf rejects a trailing slash and the bare root "/", so we strip the slash
	 * and return the file itself (which is the root group) for the root.
	 */
	private Node node(String path) {
		String p = normalizePath(path);
		return p.equals("/") ? file : file.getByPath(p);
	}
	
	private static String normalizePath(String path) {
		if (path == null || path.isEmpty()) { return "/"; }
		if (path.length() > 1 && path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		}
		return path;
	}
	
	@Override
	public int[] dimensions(String dataPath) {
		return getDataset(dataPath).getDimensions();
	}
	
	@Override
	public int chunkExtent(String dataPath, int axis) {
		if (axis < 0) { return 1; }
		Dataset ds = getDataset(dataPath);
		if (!(ds instanceof ChunkedDataset cds)) { return 1; }
		int[] chunks = cds.getChunkDimensions();
		if (chunks == null || axis >= chunks.length) { return 1; }
		int axisChunk = chunks[axis];
		return axisChunk > 0 ? axisChunk : 1;
	}
	
	@Override
	public float[] readBlock(String dataPath, long[] offset, int[] range) {
		int size = 1;
		for (int r : range) { size *= r; }
		float[] result = new float[size];
		// jhdf returns slices as nested Java arrays, so we flatten them here, coercing the
		// dataset's native numeric type to float.
		Object data = getDataset(dataPath).getData(offset, range);
		flattenInto(data, result, 0);
		return result;
	}
	
	@Override
	public float[] readFloatArray(String dataPath) {
		Object flat = getDataset(dataPath).getDataFlat();
		float[] result = new float[Array.getLength(flat)];
		flattenInto(flat, result, 0);
		return result;
	}
	
	@Override
	public double[] readDoubleArray(String dataPath) {
		Object flat = getDataset(dataPath).getDataFlat();
		double[] result = new double[Array.getLength(flat)];
		flattenInto(flat, result, 0);
		return result;
	}
	
	@Override
	public boolean exists(String path) {
		try {
			node(path);
			return true;
		} catch (HdfInvalidPathException e) {
			return false;
		}
	}
	
	@Override
	public boolean isGroup(String path) {
		try {
			return node(path) instanceof Group;
		} catch (HdfInvalidPathException e) {
			return false;
		}
	}
	
	@Override
	public List<String> groupMembers(String path) {
		if (node(path) instanceof Group group) {
			return new ArrayList<>(group.getChildren().keySet());
		}
		return List.of();
	}
	
	@Override
	public void close() throws IOException {
		file.close();
	}
	
	/**
	 * Recursively copies a (possibly nested) jhdf array into a flat float array in row-major
	 * order, coercing the leaf numeric type to float. Returns the next write position.
	 */
	private static int flattenInto(Object data, float[] dest, int pos) {
		if (data instanceof Object[] arrays) {
			for (Object child : arrays) {
				pos = flattenInto(child, dest, pos);
			}
			return pos;
		}
		if (data instanceof float[] a) {
			// Do a bulk copy when the types already match
			System.arraycopy(a, 0, dest, pos, a.length);
			pos += a.length;
		} else if (data instanceof double[] a) {
			for (double v : a) { dest[pos++] = (float) v; }
		} else if (data instanceof int[] a) {
			for (int v : a) { dest[pos++] = v; }
		} else if (data instanceof long[] a) {
			for (long v : a) { dest[pos++] = v; }
		} else if (data instanceof short[] a) {
			for (short v : a) { dest[pos++] = v; }
		} else if (data instanceof byte[] a) {
			for (byte v : a) { dest[pos++] = v; }
		} else {
			throw new IllegalArgumentException("Unsupported HDF numeric array type: " + data.getClass());
		}
		return pos;
	}
	
	/** As {@link #flattenInto(Object, float[], int)} but into a double array. */
	private static int flattenInto(Object data, double[] dest, int pos) {
		if (data instanceof Object[] arrays) {
			for (Object child : arrays) {
				pos = flattenInto(child, dest, pos);
			}
			return pos;
		}
		if (data instanceof double[] a) {
			for (double v : a) { dest[pos++] = v; }
		} else if (data instanceof float[] a) {
			for (float v : a) { dest[pos++] = v; }
		} else if (data instanceof int[] a) {
			for (int v : a) { dest[pos++] = v; }
		} else if (data instanceof long[] a) {
			for (long v : a) { dest[pos++] = v; }
		} else if (data instanceof short[] a) {
			for (short v : a) { dest[pos++] = v; }
		} else if (data instanceof byte[] a) {
			for (byte v : a) { dest[pos++] = v; }
		} else {
			throw new IllegalArgumentException("Unsupported HDF numeric array type: " + data.getClass());
		}
		return pos;
	}
	
}
