package peakaboo.datasource.plugin.sigray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.DataSourceLoader;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;

public class SigrayHDF5 extends AbstractDataSource {

	private List<Spectrum> scans;
	private String datasetName;
	private float maxEnergy;

	private SigrayHDF5Dimensions dimensions;

	static {
        System.load("/usr/lib/jni/libsis-jhdf5.so");
	}
	
	public static void main(String[] args) throws Exception {

		
		// SigrayHDF5 sigray = new SigrayHDF5();
		// sigray.read("/home/nathaniel/Downloads/9-16-16-QuartzonGla0018.h5");
		DataSourceLoader.getDSPs(); // initialize
		DataSourceLoader.loader.registerPlugin(SigrayHDF5.class);

		//Peakaboo.run();

	}

	private int index3(int x, int y, int z, int dx, int dy) {
		return x + y * dx + z * dx * dy;
	}

	// private Integer file_id = null;


	@Override
	public void read(String filename) throws Exception {

		datasetName = new File(filename).getName();
		
		
		IHDF5SimpleReader reader = HDF5Factory.openForReading(filename);

		HDF5DataSetInformation info = reader.getDataSetInformation("/MAPS/mca_arr");
		long size[] = info.getDimensions();
		dimensions = new SigrayHDF5Dimensions(this);
		dimensions.dz = (int) size[0];
		dimensions.dy = (int) size[1];
		dimensions.dx = (int) size[2];

		fn_getScanCountCallback.accept(dimensions.dx * dimensions.dy);

		float[] mca_arr = reader.readFloatArray("/MAPS/mca_arr");
		float[] scalers = reader.readFloatArray("/MAPS/scalers");

		// real scan dimensions;
		dimensions.coords = new Coord[dimensions.dx][dimensions.dy];
		for (int y = 0; y < dimensions.dy; y++) { // y-axis
			for (int x = 0; x < dimensions.dx; x++) { // x-axis

				int x_index = index3(x, y, 17, dimensions.dx, dimensions.dy);
				int y_index = index3(x, y, 18, dimensions.dx, dimensions.dy);

				Coord<Number> coord = new Coord<>(scalers[x_index], scalers[y_index]);
				dimensions.coords[x][y] = coord;
			}
		}
		// TODO: no such method exception in jni?
		dimensions.units = SISize.mm;
		// SISize.valueOf(reader.readStringArray("/MAPS/scalar_units")[17]);

		// max energy
		float[] energy = reader.readFloatArray("/MAPS/energy");
		maxEnergy = energy[dimensions.dz - 1];

		// real scan data
		scans = SpectrumList.create("sigray " + filename);

		/*
		 * data is stored im mca_arr in x, y, z order, but we're going through
		 * one spectrum at a time for speed. Because we don't want to store
		 * everything in memory, we're using a special kind of list which writes
		 * everything to disk. This means that if we `get` a spectrum from the
		 * list and write to it, this won't be reflected in the list of spectra,
		 * we have to explicitly write it back. Therefore, we do all the
		 * modifications to a spectrum at once, even though it probably means
		 * more cache misses in the source array.
		 */
		for (int y = 0; y < dimensions.dy; y++) { // y-axis
			for (int x = 0; x < dimensions.dx; x++) { // x-axis

				int scan_index = (x + y * dimensions.dx);
				Spectrum s = new Spectrum(dimensions.dz);

				for (int z = 0; z < dimensions.dz; z++) { // (z-axis, channels)

					int mca_index = index3(x, y, z, dimensions.dx, dimensions.dy);

					s.set((int) z, mca_arr[mca_index]);
				}
				scans.set(scan_index, s);
			}
			fn_readScanCallback.accept(dimensions.dx);
		}

		reader.close();
		System.gc();

	}

	@Override
	public void read(List<String> filenames) throws Exception {
		read(filenames.get(0));
	}

	@Override
	public Spectrum get(int index) throws IndexOutOfBoundsException {
		return scans.get(index);
	}

	@Override
	public int scanCount() {
		return scans.size();
	}

	@Override
	public List<String> scanNames() {
		List<String> names = new ArrayList<>();
		int count = 0;
		for (Spectrum scan : scans) {
			names.add("Scan #" + count);
			count++;
		}
		return names;
	}

	@Override
	public float maxEnergy() {
		return maxEnergy;
	}

	@Override
	public String datasetName() {
		return datasetName;
	}


	@Override
	public DataSourceMetadata getMetadata() {
		return null;
	}

	@Override
	public DataSourceDimensions getDimensions() {
		return dimensions;

	}


	@Override
	public DataSourceFileFormat getFileFormat() {
		return new SimpleFileFormat(
				true, 
				"Sigray HDF5", 
				"Sigray XRF scans in an HDF5 container", 
				Arrays.asList("h5"));
	}

}

class SigrayHDF5Dimensions implements DataSourceDimensions {

	protected int dx, dy, dz;
	protected Coord<Number> coords[][];
	protected SISize units;
	private SigrayHDF5 datasource;

	public SigrayHDF5Dimensions(SigrayHDF5 scan) {
		datasource = scan;
	}

	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		Coord<Integer> xy = getDataCoordinatesAtIndex(index);
		return coords[xy.x][xy.y];
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions() {

		Number x1 = getRealCoordinatesAtIndex(0).x;
		Number y1 = getRealCoordinatesAtIndex(0).y;
		Number x2 = getRealCoordinatesAtIndex(datasource.scanCount() - 1).x;
		Number y2 = getRealCoordinatesAtIndex(datasource.scanCount() - 1).y;

		Bounds<Number> xDim = new Bounds<Number>(x1, x2);
		Bounds<Number> yDim = new Bounds<Number>(y1, y2);
		return new Coord<Bounds<Number>>(xDim, yDim);

	}

	@Override
	public SISize getRealDimensionsUnit() {
		return units;
	}

	@Override
	public Coord<Integer> getDataDimensions() {
		return new Coord<Integer>(dx, dy);
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		int cx = index % dx;
		int cy = (index - cx) / dy;
		return new Coord<>(cx, cy);
	}

}
