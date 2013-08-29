package peakaboo.datasource.internal;

import java.util.Collections;
import java.util.List;

import peakaboo.datasource.DataSource;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

/**
 * @author maxweld
 * 
 */
public class EmptyDS implements DataSource {

	// Data Source //
	
	@Override
	public boolean hasMetadata() {
		return false;
	}

	@Override
	public boolean hasScanDimensions() {
		return false;
	}

	@Override
	public boolean canRead(String filename) {
		return false;
	}

	@Override
	public boolean canRead(List<String> filenames) {
		return false;
	}

	@Override
	public List<String> getFileExtensions() {
		return Collections.emptyList();
	}

	@Override
	public void read(String filename) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void read(List<String> filenames) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Spectrum get(int index) {
		throw new IndexOutOfBoundsException(); 
	}
	
	// DSScanData //
	

	@Override
	public int scanCount() {
		return 0;
	}

	@Override
	public List<String> scanNames() {
		return Collections.emptyList();
	}

	@Override
	public float maxEnergy() {
		return 0;
	}

	@Override
	public String datasetName() {
		return "";
	}

	// DSRealDimensions //
	
	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealDimensionsUnit() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Integer> getDataDimensions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) {
		throw new UnsupportedOperationException();
	}
	
	// DSMetaData //
	
	@Override
	public String getCreationTime() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCreator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProjectName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSessionName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacilityName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLaboratoryName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExperimentName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInstrumentName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTechniqueName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSampleName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScanName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStartTime() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEndTime() {
		throw new UnsupportedOperationException();
	}
}
