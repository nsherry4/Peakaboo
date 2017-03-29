package peakaboo.datasource.plugins.amptek;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import commonenvironment.AbstractFile;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.dimensions.DataSourceDimensions;
import peakaboo.datasource.components.fileformat.DataSourceFileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.DataSourceMetadata;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;


public class AmptekMCA extends AbstractDataSource {

	private Spectrum spectrum;
	private String scanName; 
	
	public AmptekMCA() {

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


	@Override
	public DataSourceFileFormat getFileFormat() {
		return new SimpleFileFormat(true, "Amptek MCA", "Amptek MCA XRF data format", Arrays.asList("mca"));
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
	


	

	
}
