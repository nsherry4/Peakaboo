package org.peakaboo.curvefit.curve.fitting.solver;

import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;

/**
 * Thin wrapper for Apache Math based fitting solvers. Takes care of 
 * boilerplate where possible
 */
public abstract class ApacheFittingSolver implements FittingSolver {

	
	@Override
	public FittingResultSetView solve(FittingSolverContext ctx) {
		
		int size = ctx.fittings.getVisibleCurves().size();
		if (size == 0) {
			return FittingSolverUtils.getEmptyResult(ctx);
		}

		FittingSolverContext octx = new FittingSolverContext(ctx);
		double[] weights = calculateWeights(octx);
		return FittingSolverUtils.generateFinalResults(weights, ctx);
		
	}

	protected abstract double[] calculateWeights(FittingSolverContext ctx);
	
}
