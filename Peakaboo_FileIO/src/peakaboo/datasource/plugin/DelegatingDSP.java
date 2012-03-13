package peakaboo.datasource.plugin;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.internal.EmptyDS;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

/**
 * 
 * @author maxweld
 *
 */
public abstract class DelegatingDSP extends AbstractDSP {

	private DataSource dataSource;
	
	public DelegatingDSP() {
		this(new EmptyDS());
	}
	
	public DelegatingDSP(DataSource dataSource) {
		setDataSource(dataSource);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	protected void setDataSource(DataSource dataSource) {
		if(dataSource == null) {
			throw new NullPointerException();
		}
		this.dataSource = dataSource;
	}
	
	// DataSource //
	
	@Override
	public boolean hasMetadata() {
		return dataSource.hasMetadata();
	}

	@Override
	public boolean hasScanDimensions() {
		return dataSource.hasScanDimensions();
	}

	@Override
	public boolean canRead(String filename) {
		return dataSource.canRead(filename);
	}

	@Override
	public boolean canRead(List<String> filenames) {
		return dataSource.canRead(filenames);
	}

	@Override
	public List<String> getFileExtensions() {
		return dataSource.getFileExtensions();
	}

	@Override
	public void read(String filename) throws Exception {
		dataSource.read(filename);
	}

	@Override
	public void read(List<String> filenames) throws Exception {
		dataSource.read(filenames);
	}
	
	// DSScanData //
	
	@Override
	public Spectrum get(int index) {
		return dataSource.get(index);
	}

	@Override
	public int scanCount() {
		return dataSource.scanCount();
	}

	@Override
	public List<String> scanNames() {
		return dataSource.scanNames();
	}

	@Override
	public float maxEnergy() {
		return dataSource.maxEnergy();
	}

	@Override
	public String datasetName() {
		return dataSource.datasetName();
	}

	// DSRealDimensions //
	
	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int index) {
		return dataSource.getRealCoordinatesAtIndex(index);
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions() {
		return dataSource.getRealDimensions();
	}

	@Override
	public String getRealDimensionsUnit() {
		return dataSource.getRealDimensionsUnit();
	}

	@Override
	public Coord<Integer> getDataDimensions() {
		return dataSource.getDataDimensions();
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) {
		return dataSource.getDataCoordinatesAtIndex(index);
	}
	
	// DSMetaData //

	@Override
	public String getCreationTime() {
		return dataSource.getCreationTime();
	}

	@Override
	public String getCreator() {
		return dataSource.getCreator();
	}

	@Override
	public String getProjectName() {
		return dataSource.getProjectName();
	}

	@Override
	public String getSessionName() {
		return dataSource.getSessionName();
	}

	@Override
	public String getFacilityName() {
		return dataSource.getFacilityName();
	}

	@Override
	public String getLaboratoryName() {
		return dataSource.getLaboratoryName();
	}

	@Override
	public String getExperimentName() {
		return dataSource.getLaboratoryName();
	}

	@Override
	public String getInstrumentName() {
		return dataSource.getInstrumentName();
	}

	@Override
	public String getTechniqueName() {
		return dataSource.getTechniqueName();
	}

	@Override
	public String getSampleName() {
		return dataSource.getSampleName();
	}

	@Override
	public String getScanName() {
		return dataSource.getScanName();
	}

	@Override
	public String getStartTime() {
		return dataSource.getStartTime();
	}

	@Override
	public String getEndTime() {
		return dataSource.getEndTime();
	}
}
