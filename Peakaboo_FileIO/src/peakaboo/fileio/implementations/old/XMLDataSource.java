package peakaboo.fileio.implementations.old;


import java.util.List;

import commonenvironment.AbstractFile;
import commonenvironment.IOOperations;

import fava.datatypes.Bounds;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.fileio.DataSource;
import peakaboo.fileio.implementations.support.CLSXML;
import scitypes.Coord;
import scitypes.Spectrum;


public class XMLDataSource implements DataSource
{

	private List<AbstractFile>	filenames;
	private List<Boolean>		badScans;


	public static XMLDataSource getXMLFileSet(List<AbstractFile> filenames)
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


	public float getMaxEnergy()
	{
		String fileContents = IOOperations.readerToString(filenames.get(getFirstGoodScan()).getReader());
		return CLSXML.readMaxEnergy(fileContents);
	}


	public Spectrum getScanAtIndex(int index)
	{
		if (index < 0) return null;
		if (index >= filenames.size()) return null;
		
		return CLSXML.readScanFromFile(filenames.get(index));
	}


	public int getScanCount()
	{
		return filenames.size();
	}

	public int getExpectedScanCount()
	{
		return getScanCount();
	}

	public List<String> getScanNames()
	{
		List<String> scanNames = DataTypeFactory.<String>list();
		
		//collect the names of the good scans only
		for (int i = 0; i < filenames.size(); i++){
			if (badScans.get(i) == false) scanNames.add(
					IOOperations.getFileTitle( filenames.get(i).getFileName() )
			);
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
		String commonFileName = IOOperations.getCommonFileName(getScanNames());
		String parentFolder = IOOperations.getParentFolder(filenames.get(0).getFileName());
		
		
		
		if (parentFolder == null) return commonFileName;
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


	

	public Coord<Bounds<Number>> getRealDimensions()
	{
		return null;
	}


	public String getRealDimensionsUnit()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int estimateDataSourceSize()
	{
		Spectrum s = getScanAtIndex(0);
		return s.size() * getScanCount();
	}

	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		if (files.size() <= 1) return false;
		if (! files.get(0).getFileName().toLowerCase().endsWith(".xml")) return false;
		
		return true;
		
	}
	
	
	public static String extension()
	{
		return "xml";
	}
	
	
}
