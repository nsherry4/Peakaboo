package peakaboo.fileio.xrf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import commonenvironment.IOOperations;

import fava.datatypes.Bounds;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.fileio.xrf.support.CLSXML;
import scitypes.Coord;
import scitypes.Spectrum;


public class ZipDataSource implements DataSource
{

	private ZipFile zip;
	private List<String> filenames;
	private List<Boolean> badScans;
	
	
	
	/**
	 * Creates a new ZipArchive from the zip file located at the given filename
	 * @param filename zip file location
	 * @return new ZipArchive
	 */
	public static ZipDataSource getArchiveFromFileName(String filename)
	{
		try {
			return new ZipDataSource(new ZipFile(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ZipDataSource();
		}
	}
	
	
	/**
	 * Create a new ZipArchive using this ZipFile as a source 
	 * @param zip zip file to use as source for ZipArchive
	 */
	public ZipDataSource(ZipFile zip)
	{
		this.zip = zip;
		
		filenames = getAllXMLFileNames();
		
		badScans = DataTypeFactory.<Boolean>list();
		for (int i = 0; i < filenames.size(); i++){
			badScans.add(false);
		}
		
	}
	
	

	private List<String> getAllXMLFileNames(){
		
		
		
		List<String> entryNames = DataTypeFactory.<String>list();		
		if (zip == null) return entryNames;
		
		Enumeration<? extends ZipEntry> entries = zip.entries();
		
		ZipEntry entry;
		String filename;
		
		int i = 0;
		while (entries.hasMoreElements()){
			
			entry = entries.nextElement();
			filename = entry.getName();
			if (IOOperations.checkFileExtension(filename, ".xml")) entryNames.add(  filename  );
			
			i++;
			
		}
		
		IOOperations.sortFilenames(entryNames);
		
		return entryNames;
	}
	
	
	/**
	 * Create a ZipArchive without a source
	 */
	protected ZipDataSource()
	{
		this.zip = null;
	}
	
	

	
	/**
	 * Calculates the number of files in this ZipArchive
	 * @return number of files
	 */
	public int getScanCount(){
		
		if (zip == null) return 0;
		/*
		Enumeration<? extends ZipEntry> entries = zip.entries();
		
		int count = 0;
		while( entries.hasMoreElements() ){ entries.nextElement(); count++;}
		return count;*/
		return filenames.size();
		
	}
	
	public int getExpectedScanCount()
	{
		return getScanCount();
	}
	
	
	/**
	 * Produces a list of file names for the files in this ZipArchive
	 * @return file name list
	 */
	public List<String> getScanNames(){
		
		
		/*
		List<String> entryNames = DataTypeFactory.<String>list();		
		if (zip == null) return entryNames;
		
		Enumeration<? extends ZipEntry> entries = zip.entries();
		
		ZipEntry entry;
		String filename;
		
		int i = 0;
		while (entries.hasMoreElements()){
			
			entry = entries.nextElement();
			filename = entry.getName();
			if (Common.checkFileExtension(filename, ".xml") && badScans.get(i) == false) entryNames.add(  filename  );
			
			i++;
			
		}
		
		Common.sortFilenames(entryNames);
		
		return entryNames;*/
		
		List<String> entryNames = DataTypeFactory.<String>list();	
		
		for (int i = 0; i < filenames.size(); i++)
		{
			if (badScans.get(i) == false) entryNames.add(  filenames.get(i)  );
		}
		
		return entryNames;
		
	}
	
	
	/**
	 * Returns a scan from a file in this ZipArchive
	 * @param filename name of the file in this ZipArchive to read from
	 * @return list of values for a single scan
	 */
	public Spectrum getScanAtIndex(int index)
	{
		if (zip == null) return null;
		
		String filename = filenames.get(index);
		
		try {
			
			InputStream in = zip.getInputStream(zip.getEntry(filename));
			String contents = IOOperations.readerToString(new BufferedReader(new InputStreamReader(in)));
			return CLSXML.readScanFromString(contents);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
	 * Reads the maximum energy from a file in this ZipArchive
	 * @param filename name of the file in this ZipArchive to read from
	 * @return maximum energy as specified in the given filename
	 */
	public float getMaxEnergy(){
		
		if (zip == null) return 20.48f;
		
		String filename = filenames.get(getFirstGoodScan());
		
		try {
			
			InputStream in = zip.getInputStream(zip.getEntry(filename));
			String contents = IOOperations.readerToString(new BufferedReader(new InputStreamReader(in)));
			return CLSXML.readMaxEnergy(contents);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 20.48f;
		}
		
	}

	
	public void markScanAsBad(int index)
	{
		badScans.set(index, true);
	}
	
	private int getFirstGoodScan(){
		
		int i = 0;
		for ( ; i < filenames.size(); i++){
			if (badScans.get(i) == false) return i;
		}
		
		return -1;
		
	}

	
	public String getDatasetName()
	{
		return new File(zip.getName()).getName();
	}
	
	
	
	
	

	public boolean providesDimensions()
	{
		return false;
	}
	
	public Coord<Integer> getDataDimensions()
	{
		return null;
	}
	

	public Coord<Number> getRealCoordinatesAtIndex(int index)
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

	
}
