package peakaboo.fileio.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commonenvironment.AbstractFile;

import peakaboo.fileio.DataSource;
import peakaboo.fileio.DataSourceDimensions;
import peakaboo.fileio.DataSourceExtendedInformation;

import fava.Fn;
import fava.Functions;
import fava.datatypes.Bounds;
import fava.functionable.Range;

import scitypes.Coord;
import scitypes.GridPerspective;
import scitypes.Spectrum;
import scitypes.filebacked.FileBackedList;


public class CopiedDataSource implements DataSource, DataSourceDimensions, DataSourceExtendedInformation
{

	private List<String>				scannames = new ArrayList<String>();
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
		
		GridPerspective<Spectrum> dsGrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int counter = 0;
		for (Integer y : rangeY)
		{
			for (Integer x : rangeX )
			{
				scannames.add("Scan " + counter);
			}
			counter++;
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
		return Fn.map(scannames, Functions.<String>id());
	}


	public Coord<Integer> getDataDimensions()
	{
		return new Coord<Integer>(rangeX.size(), rangeY.size());
	}


	public Coord<Number> getRealCoordinatesAtIndex(int index)
	{
		
		if (!(originalDataSource instanceof DataSourceDimensions)) return null;
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		GridPerspective<Spectrum> origgrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int x = grid.getXYFromIndex(index).first;
		int y = grid.getXYFromIndex(index).second;
		
		x += rangeX.getStart();
		y += rangeY.getStart();
		
		int realIndex = origgrid.getIndexFromXY(x, y);
				
		return ((DataSourceDimensions)originalDataSource).getRealCoordinatesAtIndex(realIndex);
	
	}


	public Coord<Bounds<Number>> getRealDimensions()
	{
		
		if (!(originalDataSource instanceof DataSourceDimensions)) return null;
		
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		Coord<Number> bottomLeft, bottomRight, topLeft, topRight;
		
		bottomLeft 	= getRealCoordinatesAtIndex(0);
		topLeft 	= getRealCoordinatesAtIndex(grid.getIndexFromXY( 0, 				rangeY.size()-1	));
		bottomRight = getRealCoordinatesAtIndex(grid.getIndexFromXY( rangeX.size()-1, 	0				));
		topRight	= getRealCoordinatesAtIndex(grid.getIndexFromXY( rangeX.size()-1, 	rangeY.size()-1	));
				
		Bounds<Number> bx = new Bounds<Number>(bottomLeft.x, bottomRight.x);
		Bounds<Number> by = new Bounds<Number>(bottomLeft.y, topLeft.y);
		
		/*
		
		
		if (!(originalDataSource instanceof DataSourceDimensions)) return null;
		Coord<Bounds<Number>> origDims = ((DataSourceDimensions)originalDataSource).getRealDimensions();
		
		Double deltaX = origDims.x.end.doubleValue() - origDims.x.start.doubleValue();
		Double deltaY = origDims.y.end.doubleValue() - origDims.y.start.doubleValue();
		
		Double startX = origDims.x.start.doubleValue();
		Double startY = origDims.y.start.doubleValue();
		
		
		Double percentStartX = rangeX.getStart() / (double)sizeX;
		Double percentEndX = rangeX.getStop() / (double)sizeX;
		Double percentStartY = rangeY.getStart() / (double)sizeY;
		Double percentEndY = rangeY.getStop() / (double)sizeY;
		
		
		
		
		Bounds<Number> bx = new Bounds<Number>(startX + deltaX*percentStartX, startX + deltaX*percentEndX);
		Bounds<Number> by = new Bounds<Number>(startY + deltaY*percentStartY, startY + deltaX*percentEndY);
		*/
		return new Coord<Bounds<Number>>(bx, by);
		
	}


	public String getRealDimensionsUnit()
	{
		if (!(originalDataSource instanceof DataSourceDimensions)) return null;
		return ((DataSourceDimensions)originalDataSource).getRealDimensionsUnit();
	}


	public boolean hasRealDimensions()
	{
		if (!(originalDataSource instanceof DataSourceDimensions)) return false;
		return ((DataSourceDimensions)originalDataSource).hasRealDimensions();
	}


	public String getCreationTime()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getCreationTime();
	}


	public String getCreator()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getCreator();
	}


	public String getEndTime()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getEndTime();
	}


	public String getExperimentName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getExperimentName();
	}


	public String getFacilityName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getFacilityName();
	}


	public String getInstrumentName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getInstrumentName();
	}


	public String getLaboratoryName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getLaboratoryName();
	}


	public String getProjectName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getProjectName();
	}


	public String getSampleName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getSampleName();
	}


	public String getScanName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getScanName();
	}


	public String getSessionName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getSessionName();
	}


	public String getStartTime()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getStartTime();
	}


	public String getTechniqueName()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return null;
		return ((DataSourceExtendedInformation)originalDataSource).getTechniqueName();
	}


	public boolean hasExtendedInformation()
	{
		if (!(originalDataSource instanceof DataSourceExtendedInformation)) return false;
		return ((DataSourceExtendedInformation)originalDataSource).hasExtendedInformation();
	}

	public static boolean filesMatchCriteria(List<AbstractFile> files)
	{
		return false;
	}
	
	
}
