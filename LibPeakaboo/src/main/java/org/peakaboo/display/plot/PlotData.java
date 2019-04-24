package org.peakaboo.display.plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;

public class PlotData {

	public ReadOnlySpectrum raw;
	public ReadOnlySpectrum filtered;
	public Map<Filter, ReadOnlySpectrum> deltas;
	public boolean consistentScale = true;
	public DataSet dataset;
	
	public EnergyCalibration calibration;
	public DetectorMaterialType detectorMaterial = DetectorMaterialType.SILICON;
	
	public FilterSet filters = new FilterSet();
	public FittingResultSet proposedResults;
	public FittingResultSet selectionResults;
	
	public List<ITransitionSeries> highlightedTransitionSeries = new ArrayList<>();
	public List<ITransitionSeries> proposedTransitionSeries = new ArrayList<>();
	
	public Map<ITransitionSeries, String> annotations = new HashMap<>();
	
}
