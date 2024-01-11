package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.app.PeakabooConfiguration;
import org.peakaboo.app.PeakabooConfiguration.MemorySize;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.model.components.datasize.DataSize;
import org.peakaboo.dataset.source.model.components.datasize.SimpleDataSize;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

import ch.systemsx.cisd.base.mdarray.MDFloatArray;
import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5FloatReader;
import ch.systemsx.cisd.hdf5.IHDF5Reader;


/**
 * This abstract HDF5 {@link DataSource} is designed to make reading single-file
 * HDF5 data easier. Data stored in a single HDF5 file can be accessed by
 * specifying the path to the data and the axis ordering (e.g. "xyz").
 */
public abstract class FloatMatrixHDF5DataSource extends SimpleHDF5DataSource {

	private String axisOrder;
	private int xIndex = -1;
	private int yIndex = -1;
	private int zIndex = -1;
	
	private static final int BLOCK_READ_SIZE = 	PeakabooConfiguration.memorySize == MemorySize.TINY ? 20 : 
												PeakabooConfiguration.memorySize == MemorySize.SMALL ? 100 : 
												PeakabooConfiguration.memorySize == MemorySize.MEDIUM ? 800 : 3200;
	
	public FloatMatrixHDF5DataSource(String axisOrder, String dataPath, String name, String description) {
		super(dataPath, name, description);
		this.axisOrder = axisOrder;
	}

	public FloatMatrixHDF5DataSource(String axisOrder, String name, String description) {
		super(name, description);
		this.axisOrder = axisOrder;
	}
	
	protected FloatMatrixHDF5DataSource(String name, String description) {
		super(name, description);
	}
	
	private void readAxisOrder() {
		//X and Y represent x and y positions on a raster scan/map
		//Z represents channels in a single scan
		String order = getAxisOrder().toLowerCase();
		xIndex = order.indexOf("x");
		yIndex = order.indexOf("y");
		zIndex = order.indexOf("z");
	}

	@Override
	public void read(DataSourceContext ctx) throws DataSourceReadException, IOException, InterruptedException {
		readAxisOrder();
		super.read(ctx);
	}
	
	@Override
	protected void readFile(DataInputAdapter path, int filenum) throws DataSourceReadException, IOException, InterruptedException {
		if (filenum > 0) {
			throw new IllegalArgumentException(getFileFormat().getFormatName() + " requires exactly 1 file");
		}
		
		
		IHDF5Reader reader = getReader(path);
		HDF5DataSetInformation info = reader.getDataSetInformation(dataPaths.get(0));
		int channels = (int) info.getDimensions()[zIndex];
		
		
		//prep for iterating over all points in scan
		IHDF5FloatReader floatreader = reader.float32();
		int height = dataSize.getDataDimensions().y;
		int width = dataSize.getDataDimensions().x;
		int[] range;
		if (yIndex >= 0) {
			range = new int[] {-1, -1, -1};
			range[xIndex] = 1;
			range[yIndex] = 1;
			range[zIndex] = channels;
		} else {
			//There is no y axis, only scans (x) and channels (z)
			range = new int[] {-1, -1};
			range[xIndex] = 1;
			range[zIndex] = channels;
		}


		Map<String, Spectrum> livetimes = new HashMap<>();
		for (String dataPath : dataPaths) {
			Spectrum livetime = getDeadtimes(dataPath, reader);
			SpectrumCalculations.subtractListFrom_inplace(livetime, 1, 0);
			livetimes.put(dataPath, livetime);
		}
			
		
		long[] offset = yIndex == -1 ? new long[] {0, 0} : new long[] {0, 0, 0};
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width;) {
				if (super.getInteraction().checkReadAborted()) { return; }
				
				int index = (y*width+x);
				int blocksize = blockReadSize(x, width);
				
				//Initialize aggregate spectra, we will read from each data path and sum the results into the aggregate
				List<Spectrum> aggs = new ArrayList<>();
				for (int i = 0; i < blocksize; i++) {
					aggs.add(new ArraySpectrum(channels));
				}
				
				//read scan
				if (yIndex > -1) { 
					offset[yIndex] = y;
					offset[xIndex] = x;
				} else {
					offset[xIndex] = y*width+x;
				}
				offset[zIndex] = 0;
				
				
				//For block reads, we try to read more than one scan in a row
				range[xIndex] = blocksize;

				
				for (String dataPath : dataPaths) {

				    /*
				     * Reads a block from a multi-dimensional <code>float</code> array from the data set 
				     * <var>objectPath</var>.
				     * 
				     * @param objectPath The name (including path information) of the data set object in the file.
				     * @param blockDimensions The extent of the block in each dimension.
				     * @param blockNumber The block number in each dimension (offset: multiply with the
				     *            <var>blockDimensions</var> in the according dimension).
				     * @return The data block read from the data set.
				     */
					//MDFloatArray mdarray = floatreader.readMDArrayBlock(dataPath, range, offset);
					
					
				    /*
				     * Reads a block from a multi-dimensional <code>float</code> array from the data set
				     * <var>objectPath</var>.
				     * 
				     * @param objectPath The name (including path information) of the data set object in the file.
				     * @param blockDimensions The extent of the block in each dimension.
				     * @param offset The offset in the data set to start reading from in each dimension.
				     * @return The data block read from the data set.
				     */
					//TODO: ~89% of loading time is this call
					MDFloatArray mdarray = floatreader.readMDArrayBlockWithOffset(dataPath, range, offset);
					List<Spectrum> spectra = floatsToSpectra(mdarray.getAsFlatArray(), channels);
					
					
					//For each spectrum returned, do some post-processing
					for (int i = 0; i < spectra.size(); i++) {
						Spectrum scan = spectra.get(i);
						//deadtime correction
						SpectrumCalculations.divideBy_inplace(scan, livetimes.get(dataPath).get(index + i));
						//add scan to aggregate
						SpectrumCalculations.addLists_inplace(aggs.get(i), scan);
					}
				}
				for (int i = 0; i < aggs.size(); i++) {
					super.submitScan(index + i, aggs.get(i));
				}
				
				//Increment x manually here so that it always reflects the actual size of the read
				x += range[xIndex];
				
			}
		}

		
		readMatrixMetadata(reader, channels);
		
	}


	
	/**
	 * Given a float[] of length count*channels, return `count` Spectrum objects of length `channels`
	 */
	protected List<Spectrum> floatsToSpectra(float[] array, int channels) {
		List<Spectrum> spectra = new ArrayList<>();
		for (int i = 0; i < array.length; i+=channels) {
			float[] spectrumData = Arrays.copyOfRange(array, i, i+channels);
			Spectrum spectrum = new ArraySpectrum(spectrumData, false);
			spectra.add(spectrum);
		}
		return spectra;
	}
	
	
	/**
	 * Given the current position `x` in a row of scans and the width of the row,
	 * calculate the size of the x component of the range array to be passed to
	 * readMDArrayBlock.
	 */
	private int blockReadSize(int x, int width) {
		//We want to read the block size, but we'll read less if we're at the end of a row
		return Math.min(BLOCK_READ_SIZE, width - x);
		
	}
	
	
	@Override
	protected final DataSize getDataSize(List<DataInputAdapter> paths, HDF5DataSetInformation datasetInfo) {
		DataInputAdapter path = paths.get(0);
		return getDataSize(path, datasetInfo);
	}
	protected DataSize getDataSize(DataInputAdapter path, HDF5DataSetInformation datasetInfo) {
		SimpleDataSize size = new SimpleDataSize();
		size.setDataHeight(yIndex == -1 ? 1 : (int) datasetInfo.getDimensions()[yIndex]);
		size.setDataWidth((int) datasetInfo.getDimensions()[xIndex]);
		return size;
	}
	
	protected static IHDF5Reader getReader(DataInputAdapter path) throws IOException {
		return HDF5Factory.openForReading(path.getAndEnsurePath().toFile());
	}
	
	/**
	 * Override this method as a hook into the tail end of the default readFile method.
	 */
	protected void readMatrixMetadata(IHDF5Reader reader, int channels) {};
	
	protected Spectrum getDeadtimes(String dataPath, IHDF5Reader reader) {
		return new ArraySpectrum(dataSize.size(), 0f);
	}
	protected String getAxisOrder() {
		return axisOrder;
	}
}
