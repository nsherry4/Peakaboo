package peakaboo.datasource.plugin.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import peakaboo.datasource.plugin.AbstractDSP;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;

import commonenvironment.AbstractFile;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;


public class MCA_DSP extends AbstractDSP {

	private Spectrum spectrum;
	private String scanName; 
	
	public MCA_DSP() {

	}
	
	private Spectrum readMCA(String filename) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		FList<String> lines = FStringInput.lines(r).toSink();
		
		int startIndex = lines.indexOf("<<DATA>>") + 1;
		int endIndex = lines.indexOf("<<END>>");
		
		Spectrum s = new Spectrum(lines.subList(startIndex, endIndex).map(new FnMap<String, Float>(){

			@Override
			public Float f(String line) {
				return Float.parseFloat(line);
			}}));
		
		r.close();
		
		return s;
		
	}
	
	@Override
	public Spectrum get(int index) {
		if (index != 0) return null;
		return spectrum;
	}

	@Override
	public int scanCount() {
		return 1;
	}

	@Override
	public List<String> scanNames() {
		return new FList<String>("Scan");
	}

	@Override
	public float maxEnergy() {
		return 0;
	}

	@Override
	public String datasetName() {
		return scanName;
	}

	
	public static boolean filesMatchCriteria(AbstractFile file)
	{
		return (file.getFileName().toLowerCase().endsWith(".mca"));		
	}

	
	
	
	
	//==============================================
	// UNSUPPORTED METHODS
	//==============================================
	
	@Override
	public boolean hasMetadata()
	{
		return false;
	}

	@Override
	public String getCreationTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCreator()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProjectName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSessionName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFacilityName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLaboratoryName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getExperimentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInstrumentName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTechniqueName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSampleName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScanName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStartTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEndTime()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Bounds<Number>> getRealDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealDimensionsUnit()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Coord<Integer> getDataDimensions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasScanDimensions()
	{
		return false;
	}

	
	
	
	
	
	
	//==============================================
	// PLUGIN METHODS
	//==============================================

	@Override
	public boolean canRead(String filename)
	{
		return filename.toLowerCase().endsWith(".mca");
	}

	@Override
	public boolean canRead(List<String> filenames)
	{
		return false;
	}

	@Override
	public void read(String filename) throws Exception
	{
		spectrum = readMCA(filename);
		scanName = new File(filename).getName();
	}

	@Override
	public void read(List<String> filenames) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDataFormat()
	{
		return "MCA";
	}

	@Override
	public String getDataFormatDescription()
	{
		return "MCA XRF data format";
	}
	
	@Override
	public List<String> getFileExtensions()
	{
		return new FList<String>("mca");
	}

	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index)
	{
		if (index != 0) throw new IndexOutOfBoundsException();
		return new Coord<Integer>(1, 1);
	}
	
}
