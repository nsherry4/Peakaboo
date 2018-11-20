package peakaboo.display.plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyclops.ReadOnlySpectrum;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;
import peakaboo.dataset.DataSet;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;

public class PlotData {

	public ReadOnlySpectrum raw;
	public ReadOnlySpectrum filtered;
	public Map<Filter, ReadOnlySpectrum> deltas;
	public boolean consistentScale = true;
	public DataSet dataset;
	
	public EnergyCalibration calibration;
	public EscapePeakType escape = EscapePeakType.SILICON;
	
	public FilterSet filters = new FilterSet();
	public FittingResultSet proposedResults;
	public FittingResultSet selectionResults;
	
	public List<ITransitionSeries> highlightedTransitionSeries = new ArrayList<>();
	public List<ITransitionSeries> proposedTransitionSeries = new ArrayList<>();
	
	public Map<ITransitionSeries, String> annotations = new HashMap<>();
	
}
