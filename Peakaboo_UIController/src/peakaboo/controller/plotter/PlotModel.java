package peakaboo.controller.plotter;


import java.util.List;

import peakaboo.controller.Model;
import peakaboo.curvefit.fitting.FittingSet;
import peakaboo.curvefit.results.FittingResultSet;
import peakaboo.dataset.DataSetProvider;
import peakaboo.dataset.EmptyDataSetProvider;
import peakaboo.dataset.ScanContainer;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.PeakTable;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.plot.PlotDrawingRequestFactory;
import peakaboo.fileio.PeakTableReader;
import peakaboo.filters.FilterSet;

public class PlotModel extends Model
{


	// raw data, and conveniences such as averaged data...
	public DataSetProvider		dataset;


	// Data related to applying filters. Filters manipulate the dataset
	public FilterSet			filters;
	public List<Double>			filteredPlot;
	//public List<List<Double>>	filteredDataSet;


	// peak table holds all information on transitions/elements
	public PeakTable			peakTable;


	// Data related to fitting elemental curves to the data post-filtering
	public FittingSet			fittingSelections;
	public FittingResultSet		fittingSelectionResults;
	public FittingSet			fittingProposals;
	public FittingResultSet		fittingProposalResults;

	
	public List<Integer>		badScans;

	// Holds settings related to the way the data is presented to the user.
	// This is here, rather than in the view because the drawing of the plot
	// is actually executed in the controller so that it may set up the
	// appropriate DrawingExtensions. Does not include information regarding
	// how to draw the plot, although it does contain settings about which
	// data to plot
	public PlotViewOptions		viewOptions;

	// Holds settings related to how the plot should be drawn. Separate from
	// PlotViewOptins because this relates not to how the controller/view should
	// behave, but rather to the way that the data plot itself should be drawn.
	public DrawingRequest		dr;



	// folder this data was taken from
	//public String				dataSourceFolder;



	public PlotModel()
	{

		super();

		// data
		dataset = new EmptyDataSetProvider();

		// peak table read
		peakTable = PeakTableReader.readPeakTable();
		
		// Filter Data
		filters = new FilterSet();
		filteredPlot = null;
		//filteredDataSet = DataTypeFactory.<Double> dataset();

		// fittings
		fittingSelections = new FittingSet(peakTable);
		fittingProposals = new FittingSet(peakTable);
		fittingSelectionResults = null;
		fittingProposalResults = null;


		// view/plot options
		dr = PlotDrawingRequestFactory.getDrawingRequest();
		viewOptions = new PlotViewOptions();
		
		badScans = DataTypeFactory.<Integer>list();

	}
	
	
	/**
	 * Get the scan that should currently be shown.
	 * @return a ScanContainer which either contains a scan, or indicates that it is not ready yet
	 */
	public ScanContainer currentScan()
	{
		ScanContainer originalData = null;
		
		if (viewOptions.channelComposite == ChannelCompositeMode.AVERAGE) {
			originalData = dataset.averagePlot(badScans);
		} else if (viewOptions.channelComposite == ChannelCompositeMode.MAXIMUM) {
			originalData = dataset.maximumPlot();
		} else {
			originalData = dataset.getScan(viewOptions.scanNumber);
		}
		
		return originalData;
		
	}

}
