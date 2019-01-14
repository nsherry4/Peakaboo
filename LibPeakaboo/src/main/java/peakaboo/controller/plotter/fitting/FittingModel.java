package peakaboo.controller.plotter.fitting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eventful.EventfulCache;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import peakaboo.curvefit.curve.fitting.solver.FittingSolver;
import peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.transition.ITransitionSeries;



public class FittingModel
{

	/**
	 * Existing TransitionSeries and their Fitting against raw data.
	 */
	public FittingSet			selections;
	
	/**
	 * Results of fitting existing selections
	 */
	public EventfulCache<FittingResultSet>		selectionResults;
	
	/**
	 * Proposed TransitionSeries and their Fitting against data after already being fit against current selections
	 */
	public FittingSet			proposals;
	
	/**
	 * Results of fitting proposed selections.
	 */
	public EventfulCache<FittingResultSet>		proposalResults;
	
	
	List<ITransitionSeries> highlighted;
	
	Map<ITransitionSeries, String> annotations;
	
	/**
	 * {@link CurveFitter} to use for all fitting of single curves to data
	 */
	public CurveFitter curveFitter;
	
	/**
	 * {@link FittingSolver} to use for solving for the intensities of competing curves
	 */
	public FittingSolver fittingSolver;
	
	
	public FittingModel()
	{
		selections = new FittingSet();
		proposals = new FittingSet();
		selections.getFittingParameters().setEscapeType(EscapePeakType.getDefault());
		proposals.getFittingParameters().setEscapeType(EscapePeakType.getDefault());
		selectionResults = null;
		proposalResults = null;
		highlighted = new ArrayList<>();
		curveFitter = new UnderCurveFitter();
		fittingSolver = new GreedyFittingSolver();
		annotations = new HashMap<>();
	}
	
}
