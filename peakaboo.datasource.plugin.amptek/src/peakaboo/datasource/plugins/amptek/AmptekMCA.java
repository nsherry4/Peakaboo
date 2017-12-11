package peakaboo.datasource.plugins.amptek;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import fava.functionable.FStringInput;
import peakaboo.datasource.AbstractDataSource;
import peakaboo.datasource.components.datasize.DataSize;
import peakaboo.datasource.components.fileformat.FileFormat;
import peakaboo.datasource.components.fileformat.SimpleFileFormat;
import peakaboo.datasource.components.metadata.Metadata;
import peakaboo.datasource.components.physicalsize.PhysicalSize;
import peakaboo.datasource.components.scandata.ScanData;
import scitypes.Spectrum;


public class AmptekMCA extends AbstractDataSource implements ScanData {

	private Spectrum spectrum;
	private String scanName; 
	
	
	public AmptekMCA() {

	}
	
	private Spectrum readMCA(String filename) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		List<String> lines = FStringInput.lines(r).toSink();
		
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
	public String scanName(int index) {
		return "Scan #" + (index+1);
	}


	@Override
	public float maxEnergy() {
		return 0;
	}

	@Override
	public String datasetName() {
		return scanName;
	}

	
	@Override
	public FileFormat getFileFormat() {
		return new SimpleFileFormat(true, "Amptek MCA", "Amptek MCA XRF data format", Arrays.asList("mca"));
	}
	
	@Override
	public ScanData getScanData() {
		return this;
	}

	
	

	@Override
	public void read(File file) throws Exception
	{
		spectrum = readMCA(file.getAbsolutePath());
		scanName = file.getName();
	}

	@Override
	public void read(List<File> files) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	

	
	//==============================================
	// UNSUPPORTED FEATURES
	//==============================================
	

	@Override
	public DataSize getDataSize() {
		return null;
	}

	
	@Override
	public Metadata getMetadata() {
		return null;
	}

	@Override
	public PhysicalSize getPhysicalSize() {
		return null;
	}

	


	

	
}
