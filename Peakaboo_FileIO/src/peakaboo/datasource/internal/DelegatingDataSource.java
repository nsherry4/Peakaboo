package peakaboo.datasource.internal;

import java.util.List;

import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

/**
 * 
 * @author maxweld
 *
 */
public abstract class DelegatingDataSource extends AbstractDataSource {

	private DataSource dataSource;
	
	public DelegatingDataSource() {
		this(new EmptyDataSource());
	}
	
	public DelegatingDataSource(DataSource dataSource) {
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
	public DataSourceMetadata getMetadata() {
		return dataSource.getMetadata();
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
	
}
