package org.peakaboo.datasource.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.PipelineScanData;
import org.peakaboo.datasource.model.datafile.DataFile;
import org.peakaboo.datasource.plugin.AbstractDataSource;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.bolt.plugin.core.AlphaNumericComparitor;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import ch.systemsx.cisd.hdf5.HDF5DataSetInformation;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;

public abstract class SimpleHDF5DataSource extends AbstractDataSource {

	private PipelineScanData scandata;
	protected DataSize dataSize;
	
	
	private String name, description;
	/**
	 * This is the official set of data paths as determined by the
	 * dataPaths or dataPathsFunction provided. It will become available
	 * (ie non-null) before calls to readFile. 
	 */
	protected List<String> dataPaths;
	/**
	 * If the dataPathPreset is provided with the constructor, the default
	 * implementation of getDataPaths will return it
	 */
	private String dataPathPreset = null;
	
	public SimpleHDF5DataSource(String dataPath, String name, String description) {
		this(name, description);
		this.dataPathPreset = dataPath;
	}
	
	public SimpleHDF5DataSource(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	@Override
	public Optional<Group> getParameters(List<DataFile> files) throws DataSourceReadException, IOException {
		return Optional.empty();
	}

	@Override
	public Optional<Metadata> getMetadata() {
		return Optional.empty();
	}

	@Override
	public Optional<PhysicalSize> getPhysicalSize() {
		return Optional.empty();
	}

	@Override
	public FileFormat getFileFormat() {
		return new SimpleHDF5FileFormat(this::getDataPaths, name, description);
	}


	@Override
	public void read(List<DataFile> datafiles) throws DataSourceReadException, IOException, InterruptedException {
		String datasetName = getDatasetTitle(datafiles);
		scandata = new PipelineScanData(datasetName);
		
		IHDF5SimpleReader reader = getMetadataReader(datafiles);
		dataPaths = getDataPaths(datafiles);
		HDF5DataSetInformation info = reader.getDataSetInformation(dataPaths.get(0));
		dataSize = getDataSize(datafiles, info);
		getInteraction().notifyScanCount(dataSize.size());

		
		Comparator<String> comparitor = new AlphaNumericComparitor(); 
		datafiles.sort((a, b) -> comparitor.compare(a.getFilename(), b.getFilename()));
		int filenum = 0;
		for (DataFile datafile : datafiles) {
			readFile(datafile, filenum++);
			if (getInteraction().checkReadAborted()) {
				scandata.finish();
				return;
			}
		}
		
		scandata.finish();
	}
	
	protected final void submitScan(int index, Spectrum scan) throws DataSourceReadException, InterruptedException {
		scandata.submit(index, scan);
		if (index > 0 && index % 50 == 0) {
			getInteraction().notifyScanRead(50);
		}
	}
	
	@Override
	public PipelineScanData getScanData() {
		return scandata;
	}
	
	@Override
	public Optional<DataSize> getDataSize() {
		return Optional.of(dataSize);
	}
	
	protected String getDatasetTitle(List<DataFile> paths) {
		return DataFile.getTitle(paths);
	}
	
	protected IHDF5SimpleReader getMetadataReader(List<DataFile> paths) throws IOException {
		DataFile firstPath = paths.get(0);
		IHDF5SimpleReader reader = HDF5Factory.openForReading(firstPath.getAndEnsurePath().toFile());
		return reader;
	}
	
	protected abstract void readFile(DataFile path, int filenum) throws DataSourceReadException, IOException, InterruptedException;
	protected abstract DataSize getDataSize(List<DataFile> paths, HDF5DataSetInformation datasetInfo);
	
	//TODO: make this mandatory (abstract) -- no default implementation
	protected List<String> getDataPaths(List<DataFile> paths) {
		if (dataPathPreset != null) {
			return Collections.singletonList(dataPathPreset);
		}
		throw new IllegalStateException("Data path not provided with constructor and getDataPaths not overridden");
	}

}
