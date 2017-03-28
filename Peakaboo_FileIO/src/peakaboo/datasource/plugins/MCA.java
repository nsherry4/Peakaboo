package peakaboo.datasource.plugins;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import commonenvironment.AbstractFile;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;


public class MCA extends AbstractDataSource {

	private Spectrum spectrum;
	private String scanName; 
	
	public MCA() {

	}
	
	private Spectrum readMCA(String filename) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		FList<String> lines = FStringInput.lines(r).toSink();
		
		int startIndex = lines.indexOf("<<DATA>>") + 1;
		int endIndex = lines.indexOf("<<END>>");
		
		Spectrum s = new Spectrum(lines.subList(startIndex, endIndex).stream().map(line -> Float.parseFloat(line)).collect(toList()));
		
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
	// UNSUPPORTED FEATURES
	//==============================================
	

	@Override
	public DataSourceDimensions getDimensions() {
		return null;
	}

	
	@Override
	public DataSourceMetadata getMetadata() {
		return null;
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


	
}
