package peakaboo.datasource.model.internal;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.model.components.datasize.DataSize;
import peakaboo.datasource.model.components.fileformat.FileFormat;
import peakaboo.datasource.model.components.interaction.Interaction;
import peakaboo.datasource.model.components.metadata.Metadata;
import peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import peakaboo.datasource.model.components.scandata.ScanData;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.GridPerspective;
import scitypes.Range;
import scitypes.ReadOnlySpectrum;
import scitypes.SISize;
import scitypes.Spectrum;


public class CroppedDataSource implements SubsetDataSource, DataSize, PhysicalSize, ScanData
{

	private DataSource					originalDataSource;
	
	private int							sizeX, sizeY;
	private Range						rangeX, rangeY;
	
	public CroppedDataSource(DataSource ds, int sizeX, int sizeY, Coord<Integer> cstart, Coord<Integer> cend)
	{
		
		originalDataSource = ds;
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		
		this.rangeX = new Range(cstart.x, cend.x);
		this.rangeY = new Range(cstart.y, cend.y);
				
	}
	

	public String datasetName()
	{
		return originalDataSource.getScanData().datasetName() + " Subset";
	}



	public float maxEnergy()
	{
		return originalDataSource.getScanData().maxEnergy();
	}


	public ReadOnlySpectrum get(int index)
	{
		
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		GridPerspective<Spectrum> origgrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int x = grid.getXYFromIndex(index).first;
		int y = grid.getXYFromIndex(index).second;
		
		x += rangeX.getStart();
		y += rangeY.getStart();
				
		int realIndex = origgrid.getIndexFromXY(x, y);
				
		return originalDataSource.getScanData().get(realIndex);
	}


	@Override
	public Coord<Integer> getDataCoordinatesAtIndex(int index)
	{
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		int x = grid.getXYFromIndex(index).first;
		int y = grid.getXYFromIndex(index).second;
		return new Coord<Integer>(x, y);
	}
	
	

	@Override
	public int getOriginalIndex(int index) {
		
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		GridPerspective<Spectrum> origgrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int x = grid.getXYFromIndex(index).first;
		int y = grid.getXYFromIndex(index).second;
		
		x += rangeX.getStart();
		y += rangeY.getStart();
		
		return origgrid.getIndexFromXY(x, y);
	}
	
	
	@Override
	public int getUpdatedIndex(int originalIndex) {
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		GridPerspective<Spectrum> origgrid = new GridPerspective<Spectrum>(sizeX, sizeY, null);
		
		int x = origgrid.getXYFromIndex(originalIndex).first;
		int y = origgrid.getXYFromIndex(originalIndex).second;
		
		x -= rangeX.getStart();
		y -= rangeY.getStart();
		
		return grid.getIndexFromXY(x, y);
	}
	
	
	public int scanCount()
	{
		return rangeX.size() * rangeY.size();
	}


	public String scanName(int index) {
		return originalDataSource.getScanData().scanName(getOriginalIndex(index));
	}


	public Coord<Integer> getDataDimensions()
	{
		return new Coord<Integer>(rangeX.size(), rangeY.size());
	}


	public Coord<Number> getPhysicalCoordinatesAtIndex(int index)
	{
		if (!originalDataSource.hasDataSize()) { throw new UnsupportedOperationException(); }
		return originalDataSource.getPhysicalSize().getPhysicalCoordinatesAtIndex(getOriginalIndex(index));
	}


	public Coord<Bounds<Number>> getPhysicalDimensions()
	{		
		
		GridPerspective<Spectrum> grid = new GridPerspective<Spectrum>(rangeX.size(), rangeY.size(), null);
		Coord<Number> bottomLeft, bottomRight, topLeft;
		
		bottomLeft 	= getPhysicalCoordinatesAtIndex(0);
		topLeft 	= getPhysicalCoordinatesAtIndex(grid.getIndexFromXY( 0, 				rangeY.size()-1	));
		bottomRight = getPhysicalCoordinatesAtIndex(grid.getIndexFromXY( rangeX.size()-1, 	0				));
		//topRight	= getRealCoordinatesAtIndex(grid.getIndexFromXY( rangeX.size()-1, 	rangeY.size()-1	));
				
		Bounds<Number> bx = new Bounds<Number>(bottomLeft.x, bottomRight.x);
		Bounds<Number> by = new Bounds<Number>(bottomLeft.y, topLeft.y);
		
	
		return new Coord<Bounds<Number>>(bx, by);
		
	}


	public SISize getPhysicalUnit()
	{
		if (!originalDataSource.hasDataSize()) { throw new UnsupportedOperationException(); }
		return originalDataSource.getPhysicalSize().getPhysicalUnit();
	}



	@Override
	public void read(Path file) throws Exception
	{
		//This should never be called, since the data source this one copies from
		//should already have been initialized
		throw new UnsupportedOperationException("Cannot read in derived DataSource");
	}


	@Override
	public void read(List<Path> files) throws Exception
	{
		//This should never be called, since the data source this one copies from
		//should already have been initialized
		throw new UnsupportedOperationException("Cannot read in derived DataSource");
	}


	@Override
	public Metadata getMetadata() {
		return originalDataSource.getMetadata();
	}


	@Override
	public DataSize getDataSize() {
		return this;
	}


	@Override
	public FileFormat getFileFormat() {
		return originalDataSource.getFileFormat();
	}


	@Override
	public void setInteraction(Interaction interaction) {
		throw new UnsupportedOperationException("Cannot set interaction in derived DataSource");
	}

	@Override
	public Interaction getInteraction() {
		return originalDataSource.getInteraction();
	}


	@Override
	public ScanData getScanData() {
		return this;
	}


	@Override
	public PhysicalSize getPhysicalSize() {
		if (originalDataSource.hasPhysicalSize()) {
			return this;	
		} else {
			return null;
		}
		
	}

	
	
}
