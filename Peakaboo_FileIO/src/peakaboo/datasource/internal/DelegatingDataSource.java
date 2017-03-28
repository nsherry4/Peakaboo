package peakaboo.datasource.internal;

import java.util.List;

import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.DataSource;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;
import scitypes.Spectrum;

/**
 * 
 * @author maxweld
 *
 */
public abstract class DelegatingDataSource extends AbstractDataSource implements DataSourceDimensions {

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
		if (!dataSource.hasDimensions()) { throw new UnsupportedOperationException(); }
		return dataSource.getDimensions().getRealCoordinatesAtIndex(index);
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions() {
		if (!dataSource.hasDimensions()) { throw new UnsupportedOperationException(); }
		return dataSource.getDimensions().getRealDimensions();
	}

	@Override
	public SISize getRealDimensionsUnit() {
		if (!dataSource.hasDimensions()) { throw new UnsupportedOperationException(); }
		return dataSource.getDimensions().getRealDimensionsUnit();
	}

	@Override
	public Coord<Integer> getDataDimensions() {
		if (!dataSource.hasDimensions()) { throw new UnsupportedOperationException(); }
		return dataSource.getDimensions().getDataDimensions();
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) {
		if (!dataSource.hasDimensions()) { throw new UnsupportedOperationException(); }
		return dataSource.getDimensions().getDataCoordinatesAtIndex(index);
	}
	

	@Override
	public DataSourceDimensions getDimensions() {
		if (!dataSource.hasDimensions()) { return null; }
		return this;
	}
	
}
