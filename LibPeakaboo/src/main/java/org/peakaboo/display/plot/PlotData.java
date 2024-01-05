package org.peakaboo.display.plot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class PlotData {

	public static record PlotDataSpectra(
			SpectrumView raw,
			SpectrumView filtered,
			Map<Filter, SpectrumView> deltas,
			Map<String, SpectrumView> store
		) {
		
		public PlotDataSpectra(SpectrumView raw, SpectrumView filtered, Map<Filter, SpectrumView> deltas) {
			this(raw, filtered, deltas, new HashMap<>());
		}
		
	};
			
	
	public PlotDataSpectra spectra;
	
	public boolean consistentScale = true;
	public DataSet dataset;
	
	public EnergyCalibration calibration;
	public DetectorMaterialType detectorMaterial = DetectorMaterialType.SILICON;
	
	public FilterSet filters = new FilterSet();
	public FittingResultSetView proposedResults;
	public FittingResultSetView selectionResults;
	
	public List<ITransitionSeries> highlightedTransitionSeries = new ArrayList<>();
	public List<ITransitionSeries> proposedTransitionSeries = new ArrayList<>();
	
	public Map<ITransitionSeries, String> annotations = new HashMap<>();
	
}

