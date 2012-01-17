package peakaboo.datasource.internal;

import java.util.List;

import commonenvironment.AbstractFile;

import peakaboo.datasource.DataSource;

import fava.functionable.FList;
import fava.functionable.Range;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.GridPerspective;
import scitypes.Spectrum;


public class CopiedDataSource implements DataSource
{

	private FList<String>				scannames = new FList<String>();
	private DataSource					originalDataSource;
	
	private int							sizeX, sizeY;
	private Range						rangeX, rangeY;
	
	public CopiedDataSource(DataSource ds, int sizeX, int sizeY, Coord<Integer> cstart, Coord<Integer> cend)
	{
		
		originalDataSource = ds;
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		
		this.rangeX = new Range(cstart.x, cend.x);
		this.rangeY = new Range(cstart.y, cend.y);
		
		scannames = new FList<String>();
		for (Integer c : new Range(0, getScanCount()))
		{
			scannames.add("Scan " + c);			
		}
		
	}
	
	
	public int estimateDataSourceSize()
	{
		return (  rangeX.size() * rangeY.size()  ) * originalDataSource.getScanAtIndex(0).size();
	}


	public String getDatasetName()
	{
		return originalDataSource.getDatasetName() + " Subset";
	}


	public int getExpectedScanCount()
	{
		return rangeX.size() * rangeY.size();
	}


	public float getMaxEnergy()
	{
		return originalDataSource.getMaxEnergy();
	}


	public Spectrum getScanAtIndex(int index)
	{
		
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		GridPerspective<Spectrum> origgrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int x = grid.getXYFromIndex(index).first;
		int y = grid.getXYFromIndex(index).second;
		
		x += rangeX.getStart();
		y += rangeY.getStart();
				
		int realIndex = origgrid.getIndexFromXY(x, y);
				
		return originalDataSource.getScanAtIndex(realIndex);
	}


	public int getScanCount()
	{
		return rangeX.size() * rangeY.size();
	}


	public List<String> getScanNames()
	{
		return scannames.toSink();
	}


	public Coord<Integer> getDataDimensions()
	{
		return new Coord<Integer>(rangeX.size(), rangeY.size());
	}


	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
				
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		GridPerspective<Spectrum> origgrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int x = grid.getXYFromIndex(index).first;
		int y = grid.getXYFromIndex(index).second;
		
		x += rangeX.getStart();
		y += rangeY.getStart();
		
		int realIndex = origgrid.getIndexFromXY(x, y);
				
		return originalDataSource.getRealCoordinatesAtIndex(realIndex);
	
	}


	public Coord<Bounds<Number>> getRealDimensions()
	{		
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		Coord<Number> bottomLeft, bottomRight, topLeft;
		
		bottomLeft 	= getRealCoordinatesAtIndex(0);
		topLeft 	= getRealCoordinatesAtIndex(grid.getIndexFromXY( 0, 				rangeY.size()-1	));
		bottomRight = getRealCoordinatesAtIndex(grid.getIndexFromXY( rangeX.size()-1, 	0				));
		//topRight	= getRealCoordinatesAtIndex(grid.getIndexFromXY( rangeX.size()-1, 	rangeY.size()-1	));
				
		Bounds<Number> bx = new Bounds<Number>(bottomLeft.x, bottomRight.x);
		Bounds<Number> by = new Bounds<Number>(bottomLeft.y, topLeft.y);
		
	
		return new Coord<Bounds<Number>>(bx, by);
		
	}


	public String getRealDimensionsUnit()
	{
		return originalDataSource.getRealDimensionsUnit();
	}


	public boolean hasRealDimensions()
	{
		return originalDataSource.hasRealDimensions();
	}


	public String getCreationTime()
	{
		return originalDataSource.getCreationTime();
	}


	public String getCreator()
	{
		return originalDataSource.getCreator();
	}


	public String getEndTime()
	{
		
		return originalDataSource.getEndTime();
	}


	public String getExperimentName()
	{
		
		return originalDataSource.getExperimentName();
	}


	public String getFacilityName()
	{
		
		return originalDataSource.getFacilityName();
	}


	public String getInstrumentName()
	{
		
		return originalDataSource.getInstrumentName();
	}


	public String getLaboratoryName()
	{
		
		return originalDataSource.getLaboratoryName();
	}


	public String getProjectName()
	{
		
		return originalDataSource.getProjectName();
	}


	public String getSampleName()
	{
		
		return originalDataSource.getSampleName();
	}


	public String getScanName()
	{
		
		return originalDataSource.getScanName();
	}


	public String getSessionName()
	{
		
		return originalDataSource.getSessionName();
	}


	public String getStartTime()
	{
		
		return originalDataSource.getStartTime();
	}


	public String getTechniqueName()
	{
		return originalDataSource.getTechniqueName();
	}


	public boolean hasMetadata()
	{
		return originalDataSource.hasMetadata();
	}

	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		return false;
	}


	@Override
	public boolean canRead(String filename)
	{
		return originalDataSource.canRead(filename);
	}


	@Override
	public boolean canRead(List<String> filenames)
	{
		return originalDataSource.canRead(filenames);
	}


	@Override
	public List<String> getFileExtensions()
	{
		return originalDataSource.getFileExtensions();
	}


	@Override
	public void read(String filename) throws Exception
	{
		//This should never be called, since the data source this one copies from
		//should already have been initialized
		throw new UnsupportedOperationException();
	}


	@Override
	public void read(List<String> filenames) throws Exception
	{
		//This should never be called, since the data source this one copies from
		//should already have been initialized
		throw new UnsupportedOperationException();
	}
	
	
}
