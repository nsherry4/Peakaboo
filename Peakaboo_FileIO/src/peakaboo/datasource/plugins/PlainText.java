package peakaboo.datasource.plugins;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.functionable.Range;
import peakaboo.datasource.SpectrumList;
import peakaboo.datasource.components.DataSourceMetadata;
import peakaboo.datasource.internal.AbstractDataSource;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Spectrum;


public class PlainText extends AbstractDataSource
{

	String	datasetName;
	int 	size = 0;
	int		scanSize = -1;

	List<Spectrum> scans;
	
	
	public PlainText()
	{
		scans = SpectrumList.create(getDataFormat());
	}
	
	public String datasetName()
	{
		return datasetName;
	}

	public float maxEnergy()
	{
		return 0;
	}

	public Spectrum get(int index)
	{
		return scans.get(index);
	}

	public int scanCount()
	{
		return size;
	}

	public List<String> scanNames()
	{
		return new Range(0, size-1).stream().map(e -> "Scan #" + (e)).collect(toList());
	}

	

	
	
	
	
	
	
	
	//==============================================
	// PLUGIN METHODS
	//==============================================


	@Override
	public boolean canRead(String filename)
	{
		return 	filename.toLowerCase().endsWith(".txt") ||
				filename.toLowerCase().endsWith(".dat") ||
				filename.toLowerCase().endsWith(".csv") ||
				filename.toLowerCase().endsWith(".tsv");
	}

	@Override
	public boolean canRead(List<String> filenames)
	{
		if (filenames == null) return false;
		if (filenames.size() == 0) return false;
		if (filenames.size() > 1) return false;
		
		return canRead(filenames.get(0));
	}

	@Override
	public void read(String filename) throws Exception
	{

		datasetName = new File(filename).getName();
		
	
		//Split the input up by line
		FStringInput lines = FStringInput.lines(new File(filename));
		

		while (lines.hasNext())
		{
			String line = lines.next();
			
			if (line == null || isAborted()) break;
			
			if (line.trim().equals("") || line.trim().startsWith("#")) continue;
						
			//split on all non-digit characters
			Spectrum scan = new Spectrum(new FList<String>(line.trim().split("[, \\t]+")).stream().map(s -> {
				try { return Float.parseFloat(s); } 
				catch (Exception e) { return 0f; }
			}).collect(toList()));
			
			
			if (size > 0 && scan.size() != scanSize) 
			{
				throw new Exception("Spectra sizes are not equal");
			}
			else if (size == 0)
			{
				scanSize = scan.size();
			}
			
			
			scans.add(scan);
			size++;
			
			newScansRead(1);
			
		}
	}

	@Override
	public void read(List<String> filenames) throws Exception
	{
		
		if (filenames == null) throw new UnsupportedOperationException();
		if (filenames.size() == 0) throw new UnsupportedOperationException();
		if (filenames.size() > 1) throw new UnsupportedOperationException();
		
		read(filenames.get(0));
	}

	@Override
	public String getDataFormat()
	{
		return "Peakaboo Plain Text";
	}
	
	@Override
	public String getDataFormatDescription()
	{
		return "Peakaboo Plain Text format is a simple XRF format comprised of rows of space-separated numbers.";
	}


	@Override
	public List<String> getFileExtensions()
	{
		return new FList<String>("txt", "dat", "csv", "tsv");
	}
	
	
	
	
	
	//==============================================
	// UNSUPPORTED METHODS
	//==============================================
	
	
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


	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSourceMetadata getMetadata() {
		return null;
	}


	

}
