package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
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


		try (HDFReader reader = getReader(path)) {

			int channels = reader.dimensions(dataPaths.get(0))[zIndex];

			//prep for iterating over all points in scan
			int height = dataSize.getDataDimensions().y;
			int width = dataSize.getDataDimensions().x;

			Map<String, Spectrum> livetimes = new HashMap<>();
			for (String dataPath : dataPaths) {
				Spectrum livetime = getDeadtimes(dataPath, reader);
				SpectrumCalculations.subtractListFrom_inplace(livetime, 1, 0);
				livetimes.put(dataPath, livetime);
			}

			// HDF5 stores chunked datasets as fixed-size blocks that are compressed and
			// decompressed as a unit. When a chunk spans several rows of the height (y) axis,
			// reading a single row at a time would make the library decompress that whole
			// chunk again for every row it covers. So we read a full chunk-tall band of y at
			// once, which lets each chunk be decompressed only once. For 2D or unchunked data
			// the band is just 1, which reduces to a plain single-row read.
			// We step through the x and y dimensions in blocks based on chunk size
			// Within each block, we have an inner loop with y1, x1 as relative offsets
			// inside these blocks.
			String firstPath = dataPaths.get(0);
			int yChunk = reader.chunkExtent(firstPath, yIndex);
			int yBand = (yIndex < 0) ? 1 : yChunk;
			// Choose the largest x band that will fit in our memory budget, or at least one.
			// Work in whole chunks, so we do integer division on our budget over our chosen
			// y band and the HDF file's x chunk sizes, then convert back to x columns for
			// our final xBlock size.
			int xChunk = reader.chunkExtent(firstPath, xIndex);
			int xBlock = xChunk * Math.max(1, BLOCK_READ_SIZE / yBand / xChunk);

			// Vectors representing the size (range) and position (offset) of the block we're reading
			int[] range = new int[(yIndex < 0) ? 2 : 3];
			long[] offset = new long[range.length];
			range[zIndex] = channels;

			for (int y = 0; y < height; y += yBand) {
				int ylen = Math.min(yBand, height - y);
				if (yIndex >= 0) {
					offset[yIndex] = y;
					range[yIndex] = ylen;
				}
				for (int x = 0; x < width; x += xBlock) {
					if (super.getInteraction().checkReadAborted()) { return; }

					int xlen = Math.min(xBlock, width - x);
					// When there is no y axis, the dataset is a flat list of scans that the
					// display grid (from getDataSize) folds row-major; offset into that flat
					// axis by whole rows. For data with a real y axis this is just x (the
					// y offset is applied separately via offset[yIndex] above). For data with
					// a *logical* y axis not represented in the data, this corrects for the
					// mismatch between the logical layout and the file layout
					offset[xIndex] = (yIndex < 0 ? y * width : 0) + x;
					offset[zIndex] = 0;
					range[xIndex] = xlen;

					// The block comes back as one flat array in row-major order, where the
					// last file dimension varies fastest -- so moving one position along a
					// dimension skips `stride` elements in the array. We precompute those strides
					// for the block (whose extents are `range`) so we can locate any scan within
					// it: for a scan at block-local (xl, yl) the first channel sits at
					// base = xl*xStride + yl*yStride, and successive channels are chStride apart.
					// When z is the last dimension chStride is 1 and a spectrum is contiguous;
					// otherwise its channels are interleaved with other scans, and the stride is
					// what lets us gather them back together.
					int[] stride = strides(range);
					int chStride = stride[zIndex];
					int xStride = stride[xIndex];
					int yStride = (yIndex < 0) ? 0 : stride[yIndex];

					// We read each data path and sum the deadtime-corrected spectra into the
					// aggregate (a single data path is the common case and is just the spectrum
					// itself). The slice + deadtime correction runs here on the read thread.
					// We do this on the inner (x1, y1) loop to fully build up the spectra from a
					// single block, submit it, and move on without having to cache the entire
					// dataset.
					Spectrum[] aggs = new Spectrum[ylen * xlen];
					for (String dataPath : dataPaths) {
						//TODO: this read (with decompression) is the dominant cost for chunked files
						float[] block = reader.readBlock(dataPath, offset, range);
						Spectrum livetime = livetimes.get(dataPath);
						for (int yl = 0; yl < ylen; yl++) {
							for (int xl = 0; xl < xlen; xl++) {
								int i = yl * xlen + xl;
								int scanIndex = (y + yl) * width + (x + xl);
								int base = xl * xStride + yl * yStride;
								Spectrum scan = extractSpectrum(block, base, chStride, channels, livetime.get(scanIndex));
								if (aggs[i] == null) {
									aggs[i] = scan;
								} else {
									SpectrumCalculations.addLists_inplace(aggs[i], scan);
								}
							}
						}
					}
					// Now that we've read this block fully across all dataPaths, we commit the
					// completed scans we've read from it before moving on to the next block
					for (int yl = 0; yl < ylen; yl++) {
						for (int xl = 0; xl < xlen; xl++) {
							submitScan((y + yl) * width + (x + xl), aggs[yl * xlen + xl]);
						}
					}
				}
			}

			readMatrixMetadata(reader, channels);

		}
	}
	
	/**
	 * Row-major strides for an array of the given dimensions: how many flat elements to
	 * skip to move one step along each dimension. The last dimension is contiguous
	 * (stride 1) and each earlier stride is the product of all the dimensions after it.
	 */
	private static int[] strides(int[] dimensions) {
		int[] stride = new int[dimensions.length];
		stride[dimensions.length - 1] = 1;
		for (int i = dimensions.length - 2; i >= 0; i--) {
			stride[i] = stride[i + 1] * dimensions[i + 1];
		}
		return stride;
	}

	/**
	 * Extracts one scan from a raw read block: copies {@code channels} values starting at
	 * {@code base} and spaced {@code stride} apart (stride 1 == contiguous, i.e. z is the
	 * fastest file dimension), then applies the deadtime correction.
	 */
	private static Spectrum extractSpectrum(float[] block, int base, int stride, int channels, float livetime) {
		float[] data = new float[channels];
		if (stride == 1) {
			// The common case where spectra are stored contiguously
			System.arraycopy(block, base, data, 0, channels);
		} else {
			// The case where spectra are not stored contiguously means that we must
			// manually extract each value based on the stride between values.
			for (int c = 0; c < channels; c++) {
				data[c] = block[base + c * stride];
			}
		}
		Spectrum scan = new ArraySpectrum(data, false);
		//deadtime correction (livetime == 1 means no correction, a cheap no-op to skip)
		if (livetime != 1f) {
			SpectrumCalculations.divideBy_inplace(scan, livetime);
		}
		return scan;
	}

	@Override
	protected DataSize getDataSize(List<DataInputAdapter> paths, HDFReader reader) {
		int[] dims = reader.dimensions(dataPaths.get(0));
		SimpleDataSize size = new SimpleDataSize();
		size.setDataHeight(yIndex == -1 ? 1 : dims[yIndex]);
		size.setDataWidth(dims[xIndex]);
		return size;
	}

	protected static HDFReader getReader(DataInputAdapter path) throws IOException {
		return HDFReaders.open(path);
	}

	/**
	 * Override this method as a hook into the tail end of the default readFile method.
	 */
	protected void readMatrixMetadata(HDFReader reader, int channels) {};

	protected Spectrum getDeadtimes(String dataPath, HDFReader reader) {
		return new ArraySpectrum(dataSize.size(), 0f);
	}
	protected String getAxisOrder() {
		return axisOrder;
	}
}
