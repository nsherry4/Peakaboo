package org.peakaboo.datasource.model.internal;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.datasource.model.components.datasize.DataSize;
import org.peakaboo.datasource.model.components.fileformat.FileFormat;
import org.peakaboo.datasource.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.components.metadata.Metadata;
import org.peakaboo.datasource.model.components.physicalsize.PhysicalSize;
import org.peakaboo.datasource.model.components.scandata.ScanData;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.GridPerspective;
import cyclops.Range;
import cyclops.ReadOnlySpectrum;
import cyclops.SISize;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Group;


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
	

	@Override
	public String datasetName() {
		return "Crop of " + originalDataSource.getScanData().datasetName();
	}



	@Override
	public float maxEnergy()
	{
		return originalDataSource.getScanData().maxEnergy();
	}

	@Override
	public float minEnergy() {
		return originalDataSource.getScanData().minEnergy();
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
		if (!originalDataSource.getPhysicalSize().isPresent()) { throw new UnsupportedOperationException(); }
		return originalDataSource.getPhysicalSize().get().getPhysicalCoordinatesAtIndex(getOriginalIndex(index));
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
		if (!originalDataSource.getPhysicalSize().isPresent()) { throw new UnsupportedOperationException(); }
		return originalDataSource.getPhysicalSize().get().getPhysicalUnit();
	}




	@Override
	public void read(List<Path> files) throws Exception
	{
		//This should never be called, since the data source this one copies from
		//should already have been initialized
		throw new UnsupportedOperationException("Cannot read in derived DataSource");
	}


	@Override
	public Optional<Metadata> getMetadata() {
		return originalDataSource.getMetadata();
	}


	@Override
	public Optional<DataSize> getDataSize() {
		return Optional.of(this);
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
	public Optional<PhysicalSize> getPhysicalSize() {
		if (originalDataSource.getPhysicalSize().isPresent()) {
			return Optional.of(this);
		} else {
			return Optional.empty();
		}
		
	}


	@Override
	public Optional<Group> getParameters(List<Path> paths) {
		return Optional.empty();
	}




	
	
}
