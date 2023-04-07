package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.peakaboo.dataset.source.model.datafile.DataFile;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;

public abstract class ManagedHDF5DataSource extends SimpleHDF5DataSource {

	private String dataPath;
	private int spectrumSize;

	public ManagedHDF5DataSource(String dataPath, String name, String description) {
		super(dataPath, name, description);
		this.dataPath = dataPath;
	}
	
	@Override
	protected void readFile(DataFile path, int filenum) throws DataSourceReadException, IOException, InterruptedException {
		
		//Read the contents of the file as a float array
		HDF5DataSetInformation info = readDatasetInfo(path);
		float[] data = readSpectralData(path);
		

		
		/*
		 * We don't know what format the data is stored in, but we are sure 
		 * that any spectrum contained aren't already in our dataset.
		 */
		Map<Integer, Spectrum> scans = new HashMap<>();
		
		
		for (int i = 0; i < data.length; i++) {
			int scan = scanAtIndex(filenum, i, info);
			int channel = channelAtIndex(filenum, i, info);
			
			if (!scans.containsKey(scan)) {
				scans.put(scan, new ISpectrum(spectrumSize));
			}
			Spectrum spectrum = scans.get(scan);
			spectrum.set(channel, data[i]);
			
			if (i % spectrumSize == 0) {
				getInteraction().notifyScanRead(1);	
			}
			
		}
		
		for (int scan : scans.keySet()) {
			super.submitScan(scan, scans.get(scan));
		}

		System.gc();
	}
	

	
	
	
	
	
	protected abstract int scanAtIndex(int file, int index, HDF5DataSetInformation datasetInfo);
	protected abstract int channelAtIndex(int file, int index, HDF5DataSetInformation datasetInfo);
	

	
	protected float[] readSpectralData(DataFile path) throws IOException {
		IHDF5Reader reader = HDF5Factory.openForReading(path.getAndEnsurePath().toFile());	
		float[] data = readSpectralData(reader, dataPath);
		reader.close();
		return data;
	}
	
	protected float[] readSpectralData(IHDF5Reader reader, String entry) {
		return reader.readFloatArray(dataPath);
	}
	
	protected HDF5DataSetInformation readDatasetInfo(DataFile path) throws IOException {
		IHDF5SimpleReader reader = HDF5Factory.openForReading(path.getAndEnsurePath().toFile());	
		HDF5DataSetInformation info = reader.getDataSetInformation(dataPath);
		reader.close();
		return info;
	}

	
}
