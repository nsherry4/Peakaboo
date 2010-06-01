package peakaboo.fileio.xrf;


import java.io.File;
import java.util.List;

import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.fileio.Common;
import peakaboo.fileio.xrf.support.CLSXML;


public class XMLDataSource implements DataSource
{

	private List<String>	filenames;
	private List<Boolean>	badScans;


	public static XMLDataSource getXMLFileSet(List<String> filenames)
	{

		XMLDataSource xml = new XMLDataSource();
		CLSXML.filterNonXMLFilesFromFileList(filenames);

		xml.filenames = filenames;
		xml.badScans = DataTypeFactory.<Boolean> list();
		for (int i = 0; i < xml.filenames.size(); i++) {
			xml.badScans.add(false);
		}

		return xml;

	}


	protected XMLDataSource()
	{
	}


	public double getMaxEnergy()
	{
		String fileContents = Common.fileToString(filenames.get(getFirstGoodScan()));
		return CLSXML.readMaxEnergy(fileContents);
	}


	public List<Double> getScanAtIndex(int index)
	{
		return CLSXML.readScanFromFile(filenames.get(index));
	}


	public int getScanCount()
	{
		return filenames.size();
	}


	public List<String> getScanNames()
	{
		List<String> scanNames = DataTypeFactory.<String>list();
		
		//collect the names of the good scans only
		for (int i = 0; i < filenames.size(); i++){
			if (badScans.get(i) == false) scanNames.add(filenames.get(i));
		}
		
		return scanNames;
	}


	public void markScanAsBad(int index)
	{
		badScans.set(index, true);
	}


	private int getFirstGoodScan()
	{

		int i = 0;
		for (; i < filenames.size(); i++) {
			if (badScans.get(i) == false) return i;
		}

		return -1;

	}

	
	public String getDatasetName()
	{
		String commonFileName = Common.getCommonFileName(getScanNames());
		String parentFolder = new File(filenames.get(getFirstGoodScan())).getParentFile().getName();
		
		
		return parentFolder + ": " + commonFileName;
	}

	
	public boolean providesDimensions()
	{
		return false;
	}


	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		return null;
	}


	public Coord<Integer> getDataDimensions()
	{
		return null;
	}


	

	public Coord<Range<Number>> getRealDimensions()
	{
		return null;
	}


	public String getRealDimensionsUnit()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
}
