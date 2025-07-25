package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverUtils.ScoringContext;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class OptimizingFittingSolver extends ApacheFittingSolver {

	protected double costFnPrecision = 0.015d;
	
	@Override
	public String pluginName() {
		return "Optimizing";
	}
	
	@Override
	public String toString() {
		return pluginName();
	}
	
	@Override
	public String pluginDescription() {
		return "Matches fits to signal using a least squares algorithm";
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "5d3b18d6-3b79-40f2-9cd2-54249aa376a0";
	}
	
	/**
	 * Takes a {@link FittingSolverContext} and a {@link PowellContext} and returns the 
	 * optimized weights for best fitting
	 */
	@Override
	protected double[] calculateWeights(FittingSolverContext ctx) {
		ScoringContext eval = new ScoringContext(ctx.data.size());
		MultivariateFunction cost = getCostFunction(ctx, eval);
		double[] guess = FittingSolverUtils.getInitialGuess(ctx);		
		return optimizeCostFunction(cost, guess, costFnPrecision);
	}
	
	
	protected MultivariateFunction getCostFunction(FittingSolverContext ctx, ScoringContext scoreCtx) {
		PowellContext powell = new PowellContext(ctx.curves.size(), ctx.data.size());
		return (double[] point) -> {

			//We really don't like negative scaling factors, they don't make any logical sense.
			float containsNegatives = 0;
			for (double v : point) {
				if (v < 0) {
					containsNegatives++;
				}
			}

			calculateResidualPowell(point, ctx, scoreCtx, powell);
			float score = FittingSolverUtils.scoreResidual(point, ctx, scoreCtx.residual);
			if (containsNegatives > 0) {
				return score * (1f+containsNegatives);
			}		
			return score;
			
		};
	}
	
	protected double[] optimizeCostFunction(MultivariateFunction cost, double[] guess, double tolerance) {

		//308 reps on test session
		return new PowellOptimizer(tolerance, 1d).optimize(
				new ObjectiveFunction(cost), 
				new InitialGuess(guess),
				new MaxIter(1000000),
				new MaxEval(1000000),
				new NonNegativeConstraint(true), 
				GoalType.MINIMIZE).getPoint();
				

	}
	

	/** 
	 * Calculate the residual from data (signal) and total (fittings). Store the result in residual.
	 * Note that this version of the test function has been tuned for the PowellOptimizer.
	 * Because PowellOptimizer tunes one weight at a time, we can save a lot of processing by caching
	 * the result of curves 1 through n-1 when it's tuning curve n.
	 */
	private void calculateResidualPowell(double[] weights, FittingSolverContext ctx, ScoringContext scoreCtx, PowellContext powell) {

		//When there are no intense channels to consider, the residual will be equal to the data
		if (ctx.channels.length == 0) {
			SpectrumCalculations.subtractFromList_target(ctx.data, scoreCtx.residual, 0f);
			return;
		}
		
		int curvesLength = weights.length;
		double[] lastWeights = powell.lastWeights;
		
		// First we check if the weights up to partialIndex are a match
		int lastMatchingIndex = -1;
		for (int i = 0; i < curvesLength; i++) {
			if (weights[i] == lastWeights[i]) {
				lastMatchingIndex = i;
			} else {
				break;
			}
		}
				
		int startingCurveIndex = 0;
		Spectrum total = scoreCtx.total;
		int first = ctx.channels[0];
		int last = ctx.channels[ctx.channels.length-1];
		if (lastMatchingIndex >= powell.partialIndex && powell.partialIndex > -1) {
			// If the weights are a match up to and including the index at which we have
			// cached a partial sum, then we can skip some of the work and jump part way
			// into the calculations
			total.copy(powell.partial, first, last);
			startingCurveIndex = powell.partialIndex+1;
		} else {
			// If the weights aren't a match up to and including the index at which we have
			// a cached partial value, then we must discard the cached value and start over
			powell.partialIndex = -1;
			total.zero(first, last);
		}
		
		// Do all the weights up to the current index match the last run through
		boolean matching = true;
		List<CurveView> curves = ctx.curves;
		for (int i = startingCurveIndex; i < curvesLength; i++) {
			double weight = weights[i];
			
			// Does this weight match the corresponding weight from the last run through?
			boolean match = weight == lastWeights[i];
			
			if (matching && !match && i > startingCurveIndex) {
				// This is the first weight which does not match the last run through, so we
				// save the totals up to but not including this point before adding this curve.
				// NB: We skip doing this if this is the first curve in this (partial?) loop, as
				// we'll have nothing to add to what's already cached
				powell.partial.copy(total, first, last);
				powell.partialIndex = i-1;
			}

			// Update matching with this rounds weight match check
			matching &= match;

			// Scale the curve by the weight and add it to the total
			curves.get(i).scaleOnto((float)weight, total, first, last);
			
		}
		
		// NB: We explicitly don't cache a full match here. The Optimizer will sometimes
		// repeat a set of weights. This is not often enough to merit caching every full
		// result but often enough that we don't want to invalidate our partial cached
		// results
		
		// Update the weights to the ones from this pass so that the next loop through
		// will be able to compare its values to these this runs values
		System.arraycopy(weights, 0, powell.lastWeights, 0, weights.length);
		
		SpectrumCalculations.subtractLists_target(ctx.data, scoreCtx.total, scoreCtx.residual, first, last);

	}


	
	// We extend FittingSolverContext so that it will have extra fields we need for optimizing the powell
	// fitting in particular
	public static class PowellContext {

		public Spectrum partial;
		double[] lastWeights;
		int partialIndex = -1;
		
		public PowellContext(int curveCount, int dataChannels) {
			this.lastWeights = new double[curveCount];
			this.partial = new ArraySpectrum(dataChannels);
		}
				
	}
	
}
