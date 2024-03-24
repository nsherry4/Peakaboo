package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter.CurveFitterContext;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class OptimizingFittingSolver implements FittingSolver {

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

	@Override
	public FittingResultSetView solve(FittingSolverContext ctx) {
		
		int size = ctx.fittings.getVisibleCurves().size();
		if (size == 0) {
			return getEmptyResult(ctx);
		}

		Context octx = new Context(ctx);
		double[] weights = calculateWeights(octx);
		return evaluate(weights, ctx);
		
	}
	
	public double[] calculateWeights(Context ctx) {
		EvaluationSpace eval = new EvaluationSpace(ctx.data.size());
		MultivariateFunction cost = getCostFunction(ctx, eval);
		double[] guess = getInitialGuess(ctx);
		
		PointValuePair result = optimizeCostFunction(cost, guess, costFnPrecision);
		
		return result.getPoint();
	}
	
	protected FittingResultSet getEmptyResult(FittingSolverContext ctx) {
		return new FittingResultSet(
				new ArraySpectrum(ctx.data.size()), 
				new ArraySpectrum(ctx.data), 
				Collections.emptyList(), 
				ctx.fittings.getFittingParameters().copy()
			);
	}
	
	protected PointValuePair optimizeCostFunction(MultivariateFunction cost, double[] guess, double tolerance) {
		//1358 reps on test session
		//optimizer = new SimplexOptimizer(-1d, 1d);
		
		//308 reps on test session
		return new PowellOptimizer(tolerance, 1d).optimize(
				new ObjectiveFunction(cost), 
				new InitialGuess(guess),
				new MaxIter(1000000),
				new MaxEval(1000000),
				new NonNegativeConstraint(true), 
				GoalType.MINIMIZE);
		
		
		//? reps on test session but occasionally dies?
//		int size = guess.length;
//		var optimizer = new BOBYQAOptimizer(Math.max(size+2, size*2));
//		return optimizer.optimize(
//				new ObjectiveFunction(cost),
//				new InitialGuess(guess),
//				new MaxIter(1000000),
//				new MaxEval(1000000),
//				new NonNegativeConstraint(true),
//				SimpleBounds.unbounded(size),
//				GoalType.MINIMIZE
//			);
		
		//-1 for rel means don't use rel, just use abs difference
//		PointValuePair result = new SimplexOptimizer(-1d, 1d).optimize(
//				new ObjectiveFunction(cost), 
//				new InitialGuess(guess),
//				new MaxIter(100000),
//				new MaxEval(100000),
//				new NonNegativeConstraint(true), 
//				GoalType.MINIMIZE,
//				new MultiDirectionalSimplex(guess)
//				);
		
		
	}
	
	public static double[] getInitialGuess(FittingSolverContext ctx) {
		int curveCount = ctx.curves.size();
		double[] guess = new double[curveCount];
		for (int i = 0; i < curveCount; i++) {
			CurveView curve = ctx.curves.get(i);
			FittingResultView guessFittingResult = ctx.fitter.fit(new CurveFitterContext(ctx.data, curve));
			
			//there will usually be some overlap between elements, so
			//we use 80% of the independently fitted guess.
			guess[i] = guessFittingResult.getCurveScale() * 0.80f;
			
			//guesses shouldn't be zero
			if (guess[i] == 0) {
				guess[i] = 0.00001d;
			}
		}
		return guess;
	}
	

	
	
	protected MultivariateFunction getCostFunction(Context ctx, EvaluationSpace eval) {
		return new MultivariateFunction() {
					
			@Override
			public double value(double[] point) {
				
				//We really don't like negative scaling factors, they don't make any logical sense.
				float containsNegatives = 0;
				for (double v : point) {
					if (v < 0) {
						containsNegatives++;
					}
				}

				testPowell(point, ctx, eval);
				float score = score(point, ctx, eval.residual);
				if (containsNegatives > 0) {
					return score * (1f+containsNegatives);
				}		
				return score;
				
			}
		};
	}
	
	


	/** 
	 * Calculate the residual from data (signal) and total (fittings). Store the result in residual.
	 * This is a generic version of the test method, untuned for any specific optimizing method
	 */
	private void testGeneric(double[] weights, FittingSolverContext ctx, EvaluationSpace eval) {
		Spectrum total = eval.total;
		total.zero();
		
		//When there are no intense channels to consider, the residual will be equal to the data
		if (ctx.channels.length == 0) {
			SpectrumCalculations.subtractFromList_target(ctx.data, eval.residual, 0f);
			return;
		}

		List<CurveView> curves = ctx.curves;
		int curvesLength = weights.length;
		int first = ctx.channels[0];
		int last = ctx.channels[ctx.channels.length-1];
		for (int i = 0; i < curvesLength; i++) {
			curves.get(i).scaleOnto((float) weights[i], total, first, last);
		}
		SpectrumCalculations.subtractLists_target(ctx.data, eval.total, eval.residual, first, last);

	}

	/** 
	 * Calculate the residual from data (signal) and total (fittings). Store the result in residual.
	 * Note that this version of the test function has been tuned for the PowellOptimizer. 
	 */
	private void testPowell(double[] weights, Context ctx, EvaluationSpace eval) {

		//When there are no intense channels to consider, the residual will be equal to the data
		if (ctx.channels.length == 0) {
			SpectrumCalculations.subtractFromList_target(ctx.data, eval.residual, 0f);
			return;
		}
		
		int curvesLength = weights.length;
		double[] lastWeights = ctx.lastWeights;
		
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
		Spectrum total = eval.total;
		int first = ctx.channels[0];
		int last = ctx.channels[ctx.channels.length-1];
		if (lastMatchingIndex >= ctx.partialIndex && ctx.partialIndex > -1) {
			// If the weights are a match up to and including the index at which we have
			// cached a partial sum, then we can skip some of the work and jump part way
			// into the calculations
			total.copy(ctx.partial, first, last);
			startingCurveIndex = ctx.partialIndex+1;
		} else {
			// If the weights aren't a match up to and including the index at which we have
			// a cached partial value, then we must discard the cached value and start over
			ctx.partialIndex = -1;
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
				ctx.partial.copy(total, first, last);
				ctx.partialIndex = i-1;
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
		System.arraycopy(weights, 0, ctx.lastWeights, 0, weights.length);
		
		SpectrumCalculations.subtractLists_target(ctx.data, eval.total, eval.residual, first, last);

	}
	
	
	/**
	 * Score the context's residual spectrum
	 */
	// NB: Bytecode-optimized function. Take care making changes
	private float score(double[] point, FittingSolverContext ctx, Spectrum residual) {
		float[] ra = residual.backingArray();
		float score = 0;
		int length = ctx.channels.length;
		int[] channels = ctx.channels;
		for (int i = 0; i < length; i++) {
			float value = ra[channels[i]];
			
			//Negative values mean that we've fit more signal than exists
			//We penalize this to prevent making up data where none exists.
			if (value < 0) {
				value = value*-50f;
			}
			
			score += value;	
		}
		
		return score;
	}
	
	/**
	 * Accepts an array of doubles (the weights from the solver, one per fitting)
	 * and a context. Scales the context.curves by the weights. Returns a new
	 * FittingResultSet containing the fitted curves and other totals.
	 */
	public static FittingResultSetView evaluate(double[] point, FittingSolverContext ctx) {
		int index = 0;
		List<FittingResultView> fits = new ArrayList<>();
		Spectrum total = new ArraySpectrum(ctx.data.size());
		Spectrum scaled = new ArraySpectrum(ctx.data.size());
		for (CurveView curve : ctx.curves) {
			float scale = (float) point[index++];
			curve.scaleInto(scale, scaled);
			fits.add(new FittingResult(curve, scale));
			SpectrumCalculations.addLists_inplace(total, scaled);
		}
		Spectrum residual = SpectrumCalculations.subtractLists(ctx.data, total);
		
		return new FittingResultSet(total, residual, fits, ctx.fittings.getFittingParameters().copy());
	}
	
	public static class EvaluationSpace {
		public Spectrum total;
		public Spectrum residual;
		public EvaluationSpace(int size) {
			this.total = new ArraySpectrum(size);
			this.residual = new ArraySpectrum(size);
		}
	}
	
	
	public static class Context extends FittingSolverContext {

		public Spectrum partial;
		double[] lastWeights;
		int partialIndex = -1;
		
		public Context(SpectrumView data, FittingSetView fittings, CurveFitter fitter) {
			super(data, fittings, fitter);
			init();
		}
		
		public Context(FittingSolverContext copy) {
			super(copy);
			init();
		}
		
		private void init() {
			this.lastWeights = new double[curves.size()];
			this.partial = new ArraySpectrum(data.size());
		}
		
	}
	
}
