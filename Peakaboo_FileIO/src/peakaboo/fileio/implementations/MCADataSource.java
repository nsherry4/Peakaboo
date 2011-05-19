package peakaboo.fileio.implementations;

import java.io.BufferedReader;
import java.util.List;

import commonenvironment.AbstractFile;
import commonenvironment.IOOperations;
import fava.functionable.FList;
import fava.signatures.FnMap;

import peakaboo.fileio.DataSource;
import scitypes.Spectrum;

public class MCADataSource implements DataSource {

	private Spectrum spectrum;
	private String scanName; 
	
	public MCADataSource(AbstractFile file) {
		spectrum = readMCA(file);
		scanName = IOOperations.getFileTitle(file.getFileName());
	}
	
	private Spectrum readMCA(AbstractFile file)
	{
		BufferedReader r = file.getReader();
		FList<String> lines = IOOperations.readerToLines(r);
		
		int startIndex = lines.indexOf("<<DATA>>") + 1;
		int endIndex = lines.indexOf("<<END>>");
		
		return new Spectrum(lines.subList(startIndex, endIndex).map(new FnMap<String, Float>(){

			@Override
			public Float f(String line) {
				return Float.parseFloat(line);
			}}));
		
	}
	
	@Override
	public Spectrum getScanAtIndex(int index) {
		if (index != 0) return null;
		return spectrum;
	}

	@Override
	public int getScanCount() {
		return 1;
	}

	@Override
	public int getExpectedScanCount() {
		return 1;
	}

	@Override
	public List<String> getScanNames() {
		return new FList<String>("Scan");
	}

	@Override
	public float getMaxEnergy() {
		return 0;
	}

	@Override
	public String getDatasetName() {
		return scanName;
	}

	@Override
	public int estimateDataSourceSize() {
		return 1;
	}
	
	public static boolean filesMatchCriteria(AbstractFile file)
	{
		return (file.getFileName().toLowerCase().endsWith(".mca"));		
	}

}
