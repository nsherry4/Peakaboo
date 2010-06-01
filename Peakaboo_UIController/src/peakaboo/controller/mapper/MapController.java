package peakaboo.controller.mapper;

import java.util.List;

import peakaboo.calculations.Interpolation;
import peakaboo.calculations.ListCalculations;
import peakaboo.controller.CanvasController;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.GridPerspective;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.common.Spectrums;
import peakaboo.drawing.map.Map;
import peakaboo.drawing.map.painters.MapPainter;
import peakaboo.drawing.map.painters.MapTechniqueFactory;
import peakaboo.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import peakaboo.drawing.map.painters.linearplot.ContainerAxisPainter;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.map.palettes.ThermalScalePalette;
import peakaboo.drawing.painters.axis.AxisPainter;
import peakaboo.drawing.painters.axis.LineAxisPainter;
import peakaboo.drawing.painters.axis.TitleAxisPainter;
import peakaboo.drawing.plot.Plot;
import peakaboo.drawing.plot.painters.axis.AxisFactory;
import peakaboo.drawing.plot.painters.plot.OriginalDataPainter;
import peakaboo.mapping.MapResultSet;


public class MapController extends CanvasController{


	private Coord<Integer> linearSampleStart, linearSampleStop; 
	
	private static final long serialVersionUID = 1L;
	
	private MapTabModel activeTabModel;

	private MapModel mapModel;
	
	private Map map;
	
	private String datasetTitle;

	

	public MapController(Object toyContext){
		super(toyContext);
		mapModel = new MapModel();
		map = new Map(this.toyContext, mapModel.dr);
	}
	
	
	public MapTabModel getActiveTabModel() {
		return activeTabModel;
	}

	public void setActiveTabModel(MapTabModel activeviewmodel) {
		this.activeTabModel = activeviewmodel;
		updateListeners();
	}
	
	public List<Double> getSummedVisibleMaps()
	{
		if (activeTabModel.summedVisibleMaps == null) activeTabModel.summedVisibleMaps = activeTabModel.mapResults.sumVisibleTransitionSeriesMaps();
		return activeTabModel.summedVisibleMaps;
	}
	
	public void setMapData(MapModel data, String datasetName, Coord<Integer> dataDimensions)
	{	
		
		this.mapModel = data;
		datasetTitle = datasetName;
		
		if (dataDimensions != null) mapModel.dataDimensions = dataDimensions;
		
		updateListeners();		
		
	}
	
	public MapResultSet getMapData(){ return activeTabModel.mapResults; }
	
	public int getMapSize(){ return activeTabModel.mapResults.size(); }	

	
	public int getMapDataPoints(){
		return activeTabModel.mapResults.getMap(0).data.size();
	}
	
	
	public Coord<Integer> getLinearSampleStart() {
		return linearSampleStart;
	}


	public void setLinearSampleStart(Coord<Integer> linearSampleStart) {
		this.linearSampleStart = linearSampleStart;
	}


	public Coord<Integer> getLinearSampleStop() {
		return linearSampleStop;
	}


	public void setLinearSampleStop(Coord<Integer> linearSampleStop) {
		this.linearSampleStop = linearSampleStop;
	}
	
	
	
	public Coord<Integer> getMapCoordinateAtPoint(double x, double y)
	{
		
		if (map == null) return null;
		
		Coord<Range<Double>> borders = map.calcAxisBorders();
		double topOffset, leftOffset;
		topOffset = borders.y.start;
		leftOffset = borders.x.start;
		
		double mapX, mapY;
		mapX = x - leftOffset;
		mapY = y - topOffset;
		
		Coord<Double> mapSize = map.calcMapSize();
		double percentX, percentY;
		percentX = mapX / mapSize.x;
		percentY = mapY / mapSize.y;
		
		if (mapModel.viewOptions.yflip) percentY = 1.0 - percentY;
		
		int indexX = (int)Math.floor(mapModel.dataDimensions.x * percentX);
		int indexY = (int)Math.floor(mapModel.dataDimensions.y * percentY);
		
		return new Coord<Integer>(indexX, indexY);
		
	}
	
	public Coord<Integer> getPointForMapCoordinate(Coord<Integer> coord)
	{
		if (map == null) return null;
		
		Coord<Range<Double>> borders = map.calcAxisBorders();
		double topOffset, leftOffset;
		topOffset = borders.y.start;
		leftOffset = borders.x.start;
		
		Coord<Double> mapSize = map.calcMapSize();
		int locX, locY;	
		locX = (int) (    leftOffset + (  ((float)coord.x / (float)mapModel.dataDimensions.x)  * mapSize.x  ) - (Map.calcCellSize(mapSize.x, mapSize.y, mapModel.dr)*0.5)  );
		locY = (int) (    topOffset  + (  ((float)coord.y / (float)mapModel.dataDimensions.y)  * mapSize.y  ) - (Map.calcCellSize(mapSize.x, mapSize.y, mapModel.dr)*0.5)  );
		
		return new Coord<Integer>(locX, locY);
	}
	
	
	//interpolation
	public void setInterpolation(int passes){
		if (passes < 0) passes = 0;
		if (passes > 3) passes = 3;
		mapModel.viewOptions.interpolation = passes;
		invalidateInterpolation();
	}
	public int getInterpolation(){
		return mapModel.viewOptions.interpolation;
	}
	
	
	//data height and width
	public void setDataHeight(int height){
		
		if ( getDataWidth() * height > getMapData().size()) height = getMapData().size() / getDataWidth();
		if (height < 1) height = 1;
		
		//datamodel is the 'official' place for storing data dimensions
		mapModel.dataDimensions.y = height;
		mapModel.dr.dataHeight = height;
		invalidateInterpolation();
	}


	public int getDataHeight(){ return mapModel.dataDimensions.y; }
	
	public void setDataWidth(int width){
		
		if (getDataHeight() * width > getMapData().size()) width = getMapData().size() / getDataHeight();
		if (width < 1) width = 1;
		
		//datamodel is the 'official' place for storing data dimensions
		mapModel.dataDimensions.x = width;
		mapModel.dr.dataWidth = width;
		invalidateInterpolation();
	}
	
	public void invalidateInterpolation()
	{
		activeTabModel.interpolatedData = null;
		updateListeners();
	}
	
	public int getDataWidth(){ return mapModel.dataDimensions.x; }
	
	
	/*
	public int getInterpolatedHeight(){
		
		int height = model.dr.dataHeight;
		
		for (int i = 0; i < model.viewOptions.interpolation; i++){
			height = height * 2 - 1;
		}
		
		return height;
	}*/
	/*public int getInterpolatedWidth(){
		
		int width = model.dr.dataWidth;
		
		for (int i = 0; i < model.viewOptions.interpolation; i++){
			width = width * 2 - 1;
		}
		
		return width;
	}*/
	
	
	//image height and width
	public void setImageHeight(double height){
		mapModel.dr.imageHeight = height;
	}
	public double getImageHeight(){ return mapModel.dr.imageHeight; }
	
	public void setImageWidth(double width){
		mapModel.dr.imageWidth = width;
	}
	public double getImageWidth()
	{ 
		return mapModel.dr.imageWidth; 
	}
	
	
	
	
	//contours
	public void setContours(boolean contours){
		mapModel.viewOptions.contour = contours;
		updateListeners();
	}
	public boolean getContours(){ return mapModel.viewOptions.contour; }
	
	
	//spectrum
	public void setSpectrumSteps(int steps){
		if (steps > 25) steps = 25;
		if (steps > 0){
			mapModel.viewOptions.spectrumSteps = steps;
		}
		updateListeners();
	}
	public int getSpectrumSteps(){ return mapModel.viewOptions.spectrumSteps; }

	
	public void setMonochrome(boolean mono){
		mapModel.viewOptions.monochrome = mono;
		updateListeners();
	}
	public boolean getMonochrome(){ return mapModel.viewOptions.monochrome; }
	
	
	public MapScaleMode getMapScaleMode(){ return mapModel.viewOptions.mapScaleMode; }
	
	public void setMapScaleMode(MapScaleMode mode)
	{
		mapModel.viewOptions.mapScaleMode = mode;
		updateListeners();
	}
	
	//set flip y axis - useful for other programmes that want to use this window.
	//default is to flip the y axis since that is the order in which the scan data
	//is taken
	public void setFlipY(boolean flip){
		mapModel.viewOptions.yflip = flip;
		updateListeners();
	}
	public boolean getFlipY(){ return mapModel.viewOptions.yflip; }
	
	
	public void setShowSpectrum(boolean show)
	{
		mapModel.viewOptions.drawSpectrum = show;
		updateListeners();
	}
	public boolean getShowSpectrum()
	{
		return mapModel.viewOptions.drawSpectrum;
	}
	
	public void setShowTitle(boolean show){
		mapModel.viewOptions.drawTitle = show;
		updateListeners();
	}
	
	public boolean getShowTitle(){
		return mapModel.viewOptions.drawTitle;
	}
	
	public void setShowDatasetTitle(boolean show)
	{
		mapModel.viewOptions.showDataSetTitle = show;	
	}
	public boolean getShowDatasetTitle()
	{
		return mapModel.viewOptions.showDataSetTitle;	
	}
	
	public void setShowCoords(boolean show)
	{
		mapModel.viewOptions.drawCoordinates = show;
		updateListeners();
	}
	public boolean getShowCoords()
	{
		return mapModel.viewOptions.drawCoordinates;
	}
	
	
	
	@Override
	protected void drawBackend(Surface backend, boolean vector){
				
		GridPerspective<Double> grid = new GridPerspective<Double>(mapModel.dataDimensions.x, mapModel.dataDimensions.y, 0.0);
		List<Double> data;
		
		if (activeTabModel.interpolatedData == null){
			
			
			data = activeTabModel.mapResults.sumVisibleTransitionSeriesMaps();
			Interpolation.interpolateBadPoints(grid, data, mapModel.badPoints);
			activeTabModel.summedVisibleMaps = DataTypeFactory.<Double>listInit(data);
			
			//interpolation of data
		    Pair<GridPerspective<Double>, List<Double>> interpolationResult;
		    int count = 0;
		    while(count < mapModel.viewOptions.interpolation){
		    	interpolationResult = Interpolation.interpolateGridLinear(grid, data);
		    	grid = interpolationResult.first;
		    	data = interpolationResult.second;
		    	count++;
		    }
		    mapModel.interpolatedSize.x= grid.width;
		    mapModel.interpolatedSize.y = grid.height;
		
		    activeTabModel.interpolatedData = data;
		    
		}
		
		int originalWidth = mapModel.dataDimensions.x;
		int originalHeight = mapModel.dataDimensions.y;
		
		mapModel.dr.dataWidth = mapModel.interpolatedSize.x;
		mapModel.dr.dataHeight = mapModel.interpolatedSize.y;
		
		if (mapModel.viewOptions.mapScaleMode == MapScaleMode.VISIBLE_ELEMENTS) {
			mapModel.dr.maxYIntensity = ListCalculations.max(activeTabModel.interpolatedData);
		} else {
			mapModel.dr.maxYIntensity = ListCalculations.max(activeTabModel.mapResults.sumAllTransitionSeriesMaps());
		}
		
		if (mapModel.dimensionsProvided) {
			
			mapModel.viewOptions.bottomLeftCoord = new Coord<Number>(mapModel.realDimensions.x.start, mapModel.realDimensions.y.start);
			mapModel.viewOptions.bottomRightCoord = new Coord<Number>(mapModel.realDimensions.x.end, mapModel.realDimensions.y.start);
			mapModel.viewOptions.topRightCoord = new Coord<Number>(mapModel.realDimensions.x.end, mapModel.realDimensions.y.end);
			mapModel.viewOptions.topLeftCoord = new Coord<Number>(mapModel.realDimensions.x.start, mapModel.realDimensions.y.end);
			
		} else {
			
			mapModel.viewOptions.bottomLeftCoord = new Coord<Number>(1, 1);
			mapModel.viewOptions.bottomRightCoord = new Coord<Number>(originalWidth, 1);
			mapModel.viewOptions.topRightCoord = new Coord<Number>(originalWidth, originalHeight);
			mapModel.viewOptions.topLeftCoord = new Coord<Number>(1, originalHeight);
			
		}
		
		
		AbstractPalette palette;
		MapPainter mapPainter;
		List<AbstractPalette> paletteList;
		
		int spectrumSteps = mapModel.viewOptions.spectrumSteps;
		if (! mapModel.viewOptions.contour)
		{
			spectrumSteps = Spectrums.DEFAULT_STEPS;
			palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);
			paletteList = DataTypeFactory.<AbstractPalette>list();
			paletteList.add(palette);
			mapPainter = MapTechniqueFactory.getTechnique(paletteList, activeTabModel.interpolatedData, false, 0);
			
		} else {
			
			spectrumSteps = mapModel.viewOptions.spectrumSteps;
			palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);
			paletteList = DataTypeFactory.<AbstractPalette>list();
			paletteList.add(palette);
			mapPainter = MapTechniqueFactory.getTechnique(paletteList, activeTabModel.interpolatedData, true, spectrumSteps);
			
		}
		
		
		List<AxisPainter> axisPainters = DataTypeFactory.<AxisPainter>list();
		
		//Plot p = new Plot(activeTabModel.interpolatedData);
		//p.setAxisPainters(new LineAxisPainter(true, true, false, true));
		//ContainerAxisPainter linearPlot = new ContainerAxisPainter(p, 0.2d, ContainerAxisPainter.Side.BOTTOM);
		//axisPainters.add(linearPlot);
		
		if (mapModel.viewOptions.showDataSetTitle)
		{
			axisPainters.add(new TitleAxisPainter(1.0, null, null, datasetTitle, null));
		}
		
		if (mapModel.viewOptions.drawTitle){
			
			String mapTitle = "Map of " + activeTabModel.mapResults.getDatasetTitle(null);

			axisPainters.add(new TitleAxisPainter(1.0, null, null, null, mapTitle));
		}
			
		axisPainters.add(new SpectrumCoordsAxisPainter(
				
				mapModel.viewOptions.drawCoordinates, 
				mapModel.viewOptions.topLeftCoord, 
				mapModel.viewOptions.topRightCoord, 
				mapModel.viewOptions.bottomLeftCoord, 
				mapModel.viewOptions.bottomRightCoord,
				mapModel.realDimensionsUnits,
				
				mapModel.viewOptions.drawSpectrum, 
				mapModel.viewOptions.spectrumHeight, 
				mapModel.viewOptions.spectrumSteps, 
				paletteList,
				
				mapModel.dimensionsProvided
				
		));
		
		
		
		boolean oldVector = mapModel.dr.drawToVectorSurface;
		mapModel.dr.drawToVectorSurface = vector;
		
		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(mapModel.dr);
		map.setPainters(mapPainter);
		map.setData(activeTabModel.interpolatedData);
		map.draw();	
		
		mapModel.dr.drawToVectorSurface = oldVector;	
		
	}
		
	
	@Override
	public void setOutputIsPDF(boolean isPDF)
	{
		mapModel.dr.drawToVectorSurface = isPDF;
	}


	@Override
	public double getUsedHeight()
	{
		return map.calculateMapDimensions().y;
	}


	@Override
	public double getUsedWidth()
	{
		return map.calculateMapDimensions().x;
	}

	
	public boolean dimensionsProvided()
	{
		return mapModel.dimensionsProvided;
	}


	public void setNeedsRedraw() {
		map.needsMapRepaint();
	}
	
}
