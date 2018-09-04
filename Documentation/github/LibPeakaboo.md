# LibPeakaboo

LibPeakaboo is the Java XRF analysis library behind [Peakaboo](https://github.com/nsherry4/Peakaboo). The goal is to create a library which can be used more broadly, rather than just as part of a Swing GUI.

# Examples

## Basic Plot
Below is a basic example of how to load an XRF dataset from a single file and produce a plot showing the averaged spectrum. The input file is in tsv (tab separated value) format.

```java
private static void example1(Path inputFile, Path plotFile) throws Exception {
	
	//Load Data
	DataSource source = new PlainText();
	source.read(Collections.singletonList(inputFile));
	DataSet dataset = new StandardDataSet(source);
	
	//Build Plot of Averaged Spectrum
	PlotData data = new PlotData();
	data.calibration = new EnergyCalibration(0f, 20.58f, dataset.getAnalysis().channelsPerScan());
	data.dataset = dataset;
	data.filtered = dataset.getAnalysis().averagePlot();
	
	//Render & Write Plot
	Plotter plot = new Plotter();
	plot.write(data, null, SurfaceType.RASTER, new Dimension(1000, 400), plotFile);
	
}
```

![Example Output](https://github.com/nsherry4/LibPeakaboo/blob/master/docs/example1plot.png "Example Output")

## Peak Fittings

This example builds off of the previous one, and adds both peak fitting and data filtering. In this case, it fits Iron and Zinc, and applies a weighted average smoothing filter.

```java
private static void example2(Path inputFile, Path plotFile) throws Exception {
	
	//Load Data
	DataSource source = new PlainText();
	source.read(Collections.singletonList(inputFile));
	DataSet dataset = new StandardDataSet(source);
	
	
	//Add Peak Fittings
	EnergyCalibration calibration = new EnergyCalibration(0f, 20.58f, dataset.getAnalysis().channelsPerScan());
	FittingSet fittings = new FittingSet();
	fittings.getFittingParameters().setEscapeType(EscapePeakType.SILICON);
	fittings.getFittingParameters().setCalibration(calibration);
	fittings.addTransitionSeries(PeakTable.SYSTEM.get(Element.Fe, TransitionSeriesType.K));
	fittings.addTransitionSeries(PeakTable.SYSTEM.get(Element.Zn, TransitionSeriesType.K));
	
	
	//Add Data Filters
	FilterSet filters = new FilterSet();
	Filter filter = new WeightedAverageNoiseFilter();
	filter.initialize();
	filters.add(filter);
	
	
	//Apply Fittings to Filtered Average Plot
	CurveFitter fitter = new OptimizingCurveFitter();
	FittingSolver solver = new GreedyFittingSolver();
	ReadOnlySpectrum filtered = filters.applyFilters(dataset.getAnalysis().averagePlot());
	FittingResultSet fittingResults = solver.solve(filtered, fittings, fitter);
	
	
	//Build Plot of Averaged Spectrum
	PlotData data = new PlotData();
	data.calibration = calibration;
	data.dataset = dataset;
	data.filtered = filtered;
	data.selectionResults = fittingResults;
	
	
	//Render & Write Plot
	Plotter plot = new Plotter();
	plot.write(data, null, SurfaceType.RASTER, new Dimension(1000, 400), plotFile);
	
}
```

![Example Output](https://github.com/nsherry4/LibPeakaboo/blob/master/docs/example2plot.png "Example Output")
