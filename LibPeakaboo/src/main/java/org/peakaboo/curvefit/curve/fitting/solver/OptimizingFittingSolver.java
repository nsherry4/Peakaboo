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
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter.CurveFitterContext;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class OptimizingFittingSolver implements FittingSolver {

	protected double costFnPrecision =  0.01d;
	
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

		EvaluationSpace eval = new EvaluationSpace(ctx.data.size());
		MultivariateFunction cost = getCostFunction(ctx, eval);
		double[] guess = getInitialGuess(ctx);
			
		PointValuePair result = optimizeCostFunction(cost, guess, costFnPrecision);
		
		double[] scalings = result.getPoint();
		return evaluate(scalings, ctx);
		
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
		
		
		//265 reps on test session but occasionally dies?
		//optimizer = new BOBYQAOptimizer(Math.max(size+2, size*2));
		
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
	

	
	
	protected MultivariateFunction getCostFunction(FittingSolverContext ctx, EvaluationSpace eval) {
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

				test(point, ctx, eval);
				float score = score(point, ctx, eval.residual);
				if (containsNegatives > 0) {
					return score * (1f+containsNegatives);
				}
				return score;
				
			}
		};
	}
	
	


	/** 
	 * Calculate the residual from data (signal) and total (fittings). Store the result in residual 
	 */
	private void test(double[] weights, FittingSolverContext ctx, EvaluationSpace eval) {
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
	 * Score the context's residual spectrum
	 */
	// NB: Bytecode-optimized function. Take care making changes
	private float score(double[] point, FittingSolverContext ctx, Spectrum residual) {
		float[] ra = residual.backingArray();
		float score = 0;
		int length = ctx.channels.length;
		for (int i = 0; i < length; i++) {
			float value = ra[ctx.channels[i]];
			
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
		public Spectrum scratch;
		public Spectrum total;
		public Spectrum residual;
		public EvaluationSpace(int size) {
			this.scratch = new ArraySpectrum(size);
			this.total = new ArraySpectrum(size);
			this.residual = new ArraySpectrum(size);
		}
	}
	
}
