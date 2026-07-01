package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.util.List;

import org.peakaboo.dataset.io.DataInputAdapter;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.HDF5StorageLayout;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import hdf.hdf5lib.H5;

/**
 * {@link HDFReader} backed by the native CISD HDF5 library. This is the preferred backend
 * where it loads, but it is unavailable on platforms without the native binary (eg Android,
 * non-x86/Apple), in which case {@link HDFReaders} falls back to {@link JHDFReader}.
 *
 * @author NAS
 */
public class NativeHDF5Reader implements HDFReader {
	
	private IHDF5Reader reader;
	
	public NativeHDF5Reader(DataInputAdapter path) throws IOException {
		this.reader = HDF5Factory.openForReading(path.getAndEnsurePath().toFile());
	}
	
	/**
	 * Forces the native HDF5 library to load, throwing if it can't (eg the native binary is
	 * missing on this platform). {@link HDFReaders} uses this to decide whether the native
	 * backend is usable without needing a file to open.
	 */
	static void probeNativeLibrary() {
		H5.loadH5Lib();
	}
	
	@Override
	public int[] dimensions(String dataPath) {
		long[] dims = reader.getDataSetInformation(dataPath).getDimensions();
		int[] result = new int[dims.length];
		for (int i = 0; i < dims.length; i++) {
			result[i] = (int) dims[i];
		}
		return result;
	}
	
	@Override
	public int chunkExtent(String dataPath, int axis) {
		if (axis < 0) { return 1; }
		HDF5DataSetInformation info = reader.getDataSetInformation(dataPath);
		if (info.getStorageLayout() != HDF5StorageLayout.CHUNKED) { return 1; }
		int[] chunks = info.tryGetChunkSizes();
		if (chunks == null || axis >= chunks.length) { return 1; }
		int axisChunk = chunks[axis];
		return axisChunk > 0 ? axisChunk : 1;
	}
	
	@Override
	public float[] readBlock(String dataPath, long[] offset, int[] range) {
		return reader.float32().readMDArrayBlockWithOffset(dataPath, range, offset).getAsFlatArray();
	}
	
	@Override
	public float[] readFloatArray(String dataPath) {
		return reader.readFloatArray(dataPath);
	}
	
	@Override
	public double[] readDoubleArray(String dataPath) {
		return reader.readDoubleArray(dataPath);
	}
	
	@Override
	public boolean exists(String path) {
		return reader.exists(path);
	}
	
	@Override
	public boolean isGroup(String path) {
		return reader.isGroup(path);
	}
	
	@Override
	public List<String> groupMembers(String path) {
		return reader.getGroupMembers(path);
	}
	
	@Override
	public void close() throws IOException {
		reader.close();
	}
	
}
