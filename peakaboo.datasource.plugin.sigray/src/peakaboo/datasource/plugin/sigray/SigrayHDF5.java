package peakaboo.datasource.plugin.sigray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import peakaboo.datasource.DataSourceLoader;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.components.DataSourceMetadata;
import peakaboo.datasource.internal.AbstractDataSource;
import peakaboo.ui.swing.Peakaboo;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

public class SigrayHDF5 extends AbstractDataSource {

	private List<Spectrum> scans;
	private String datasetName;
	
	private String scanTimestamp;
	
	private Coord<Number> coords[][];
	private String units;
	
	private int dx, dy, dz;
	
	private float maxEnergy;
	
	
	public static void main(String[] args) throws Exception {
		
		//SigrayHDF5 sigray = new SigrayHDF5();
		//sigray.read("/home/nathaniel/Downloads/9-16-16-QuartzonGla0018.h5");
		DataSourceLoader.getDSPs(); //initialize
		DataSourceLoader.loader.registerPlugin(SigrayHDF5.class);
		
		Peakaboo.run();
		
	}
	
	
	private int index3(int x, int y, int z, int dx, int dy) {
		return x + y*dx + z*dx*dy;
	}
	
	//private Integer file_id = null;
	

	@Override
	public boolean hasScanDimensions() {
		return true;
	}

	@Override
	public List<String> getFileExtensions() {
		return Collections.singletonList("h5");
	}

	@Override
	public boolean canRead(String filename) {
		return filename.endsWith("h5");
	}

	@Override
	public boolean canRead(List<String> filenames) {
		if (filenames.size() == 1) { return true; }
		return false;
	}

	@Override
	public void read(String filename) throws Exception {

		datasetName = new File(filename).getName();
		
		IHDF5SimpleReader reader = HDF5Factory.openForReading(filename);
		
		
		scanTimestamp = reader.readString("/MAPS/scan_time_stamp");
		
		
		HDF5DataSetInformation info = reader.getDataSetInformation("/MAPS/mca_arr");
		long size[] = info.getDimensions();
		dz = (int)size[0];
		dy = (int)size[1];
		dx = (int)size[2];
		
		fn_getScanCountCallback.accept(dx*dy);
				
		float[] mca_arr = reader.readFloatArray("/MAPS/mca_arr");
		float[] scalers = reader.readFloatArray("/MAPS/scalers");
		
			
		
		//real scan dimensions;
		coords = new Coord[dx][dy];
		for (int y = 0; y < dy; y++) { //y-axis
			for (int x = 0; x < dx; x++) { //x-axis
				
				int x_index = index3(x, y, 17, dx, dy);
				int y_index = index3(x, y, 18, dx, dy);
				
				Coord<Number> coord = new Coord<>(scalers[x_index], scalers[y_index]);
				coords[x][y] = coord;
			}
		}
		//TODO: no such method exceptio in jni?
		units = "mm"; //reader.readStringArray("/MAPS/scalar_units")[17];
		
		
		
		//max energy
		float[] energy = reader.readFloatArray("/MAPS/energy");
		maxEnergy = energy[dz-1];
		
		
		
		
		
		
		//real scan data
		scans = SpectrumList.create("sigray " + filename);


		/* data is stored im mca_arr in x, y, z order, but we're going through one 
		 * spectrum at a time for speed. Because we don't want to store everything 
		 * in memory, we're using a special kind of list which writes everything to 
		 * disk. This means that if we `get` a spectrum from the list and write to
		 * it, this won't be reflected in the list of spectra, we have to 
		 * explicitly write it back. Therefore, we do all the modifications to a 
		 * spectrum at once, even though it probably means more cache misses in the 
		 * source array.
		 */
		for (int y = 0; y < dy; y++) { //y-axis
			for (int x = 0; x < dx; x++) { //x-axis
				
				int scan_index = (x + y*dx);
				Spectrum s = new Spectrum(dz);
				
				for (int z = 0; z < dz; z++) { //(z-axis, channels)

					int mca_index = index3(x, y, z, dx, dy);
					
					s.set((int)z, mca_arr[mca_index]);
				}
				scans.set(scan_index, s);
			}
			fn_readScanCallback.accept(dx);
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
	public Coord<Number> getRealCoordinatesAtIndex(int index) throws IndexOutOfBoundsException {
		Coord<Integer> xy = getDataCoordinatesAtIndex(index);
		return coords[xy.x][xy.y];
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions() {
		
		Number x1 = getRealCoordinatesAtIndex(0).x;
		Number y1 = getRealCoordinatesAtIndex(0).y;
		Number x2 = getRealCoordinatesAtIndex(scanCount()-1).x;
		Number y2 = getRealCoordinatesAtIndex(scanCount()-1).y;
		
		
		Bounds<Number> xDim = new Bounds<Number>(x1, x2);
		Bounds<Number> yDim = new Bounds<Number>(y1, y2);
		return new Coord<Bounds<Number>>(xDim, yDim);
		
	}

	@Override
	public String getRealDimensionsUnit() {
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

	@Override
	public String getDataFormat() {
		return "Sigray HDF5";
	}

	@Override
	public String getDataFormatDescription() {
		return "Sigray XRF scans in an HDF5 container";
	}


	@Override
	public DataSourceMetadata getMetadata() {
		return null;
	}

	
	
}
