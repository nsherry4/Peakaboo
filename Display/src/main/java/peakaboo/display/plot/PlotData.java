package peakaboo.display.plot;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.dataset.DataSet;
import peakaboo.filter.model.FilterSet;
import scitypes.ReadOnlySpectrum;

public class PlotData {

	public ReadOnlySpectrum raw;
	public ReadOnlySpectrum filtered;
	public boolean consistentScale = true;
	public DataSet dataset;
	
	public EnergyCalibration calibration;
	public EscapePeakType escape = EscapePeakType.SILICON;
	
	public FilterSet filters = new FilterSet();
	public FittingResultSet proposedResults;
	public FittingResultSet selectionResults;
	
	public List<TransitionSeries> highlightedTransitionSeries = new ArrayList<>();
	public List<TransitionSeries> proposedTransitionSeries = new ArrayList<>();
	
}
