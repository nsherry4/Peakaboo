package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter.CurveFitterContext;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public abstract class FittingSolverUtils implements FittingSolver {

	public static class ScoringContext {
		public Spectrum total;
		public Spectrum residual;
		public ScoringContext(int size) {
			this.total = new ArraySpectrum(size);
			this.residual = new ArraySpectrum(size);
		}
	}
	


	/** 
	 * Calculate the residual from data (signal) and total (fittings). Store the result in residual.
	 * This is a generic version of the test method, untuned for any specific optimizing method
	 */
	public static void calculateResidual(double[] weights, FittingSolverContext ctx, ScoringContext eval) {
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
		// For each curve, scale the curve by the weight and add the result to total
		// Only process this between the first and last important channels for performance
		for (int i = 0; i < curvesLength; i++) {
			curves.get(i).scaleOnto((float) weights[i], total, first, last);
		}
		SpectrumCalculations.subtractLists_target(ctx.data, eval.total, eval.residual, first, last);
	}
	
	/**
	 * Score the context's residual spectrum. The less spectrum remaining unfit, the better. 
	 * The exception is negative values, which we strongly penalize
	 */
	// NB: Bytecode-optimized function. Take care making changes
	public static float scoreResidual(double[] point, FittingSolverContext ctx, Spectrum residual) {
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
	public static FittingResultSetView generateFinalResults(double[] point, FittingSolverContext ctx) {
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
	
	
	public static FittingResultSet getEmptyResult(FittingSolverContext ctx) {
		return new FittingResultSet(
				new ArraySpectrum(ctx.data.size()), 
				new ArraySpectrum(ctx.data), 
				Collections.emptyList(), 
				ctx.fittings.getFittingParameters().copy()
			);
	}
		
	/**
	 * Generates an initial guess for how the curves should be scaled to consume the proper amount of signal.
	 * @return a double array containing initial scaling weights for each curve
	 */
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
	
}
