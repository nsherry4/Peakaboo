package org.peakaboo.controller.plotter.fitting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterPlugin;
import org.peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import org.peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;



public class FittingModel
{

	/**
	 * Existing TransitionSeries and their Fitting against raw data.
	 */
	public FittingSet			selections;
	
	/**
	 * Results of fitting existing selections
	 */
	public EventfulNullableCache<FittingResultSetView>		selectionResults;
	
	/**
	 * Proposed TransitionSeries and their Fitting against data after already being fit against current selections
	 */
	public FittingSet			proposals;
	
	/**
	 * Results of fitting proposed selections.
	 */
	public EventfulNullableCache<FittingResultSetView>		proposalResults;
	
	
	List<ITransitionSeries> highlighted;
	
	Map<ITransitionSeries, String> annotations;
	
	/**
	 * {@link CurveFitterPlugin} to use for all fitting of single curves to data
	 */
	public CurveFitterPlugin curveFitter;
	
	/**
	 * {@link FittingSolver} to use for solving for the intensities of competing curves
	 */
	public FittingSolver fittingSolver;

	
	public FittingModel()
	{
		selections = new FittingSet();
		proposals = new FittingSet();
		selections.getFittingParameters().setDetectorMaterial(DetectorMaterialType.getDefault());
		proposals.getFittingParameters().setDetectorMaterial(DetectorMaterialType.getDefault());
		selectionResults = null;
		proposalResults = null;
		highlighted = new ArrayList<>();
		curveFitter = new UnderCurveFitter();
		fittingSolver = new GreedyFittingSolver();
		annotations = new HashMap<>();
	}
	
}
