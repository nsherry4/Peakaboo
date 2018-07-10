package peakaboo.display.plot;

import java.util.List;

import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.dataset.DataSet;
import peakaboo.filter.model.FilterSet;
import scitypes.Pair;
import scitypes.ReadOnlySpectrum;

public class PlotData {

	public ReadOnlySpectrum raw;
	public ReadOnlySpectrum filtered;
	public DataSet dataset;
	
	public EnergyCalibration calibration;
	public EscapePeakType escape;
	
	public FilterSet filters;
	public FittingResultSet proposedResults, selectionResults;
	
	public List<TransitionSeries> highlightedTransitionSeries, proposedTransitionSeries;
	
}
