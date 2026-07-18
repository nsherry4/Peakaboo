package org.peakaboo.curvefit.curve.fitting.solver;

import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter.CurveFitterContext;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

import java.util.ArrayList;
import java.util.List;

/**
 * Greedy solver with extra correction passes. A plain greedy fit works through
 * the curves in sequence, so earlier curves can eat signal that later ones had
 * a better claim to. After the initial greedy pass we keep revisiting each
 * curve's weight in the context of all the others until the weights settle,
 * which mostly removes that order-dependence. See {@link #correctionPasses}
 * for what the passes actually converge to.
 */
public class IterativeFittingSolver implements FittingSolver {

	private static final int MAX_CORRECTION_PASSES = 20;
	private static final float CONVERGENCE_TOLERANCE = 1e-4f;

	/**
	 * Per-curve intense-channel bounds and overlap ("contested") data. This
	 * depends only on {@code ctx.curves} -- not on the per-pixel
	 * {@code ctx.data} -- so it is cached in the context's {@link SolverCache}
	 * and may be shared (read-only) across the per-pixel solves of a map.
	 */
	record IterativeBounds(int[] firsts, int[] lasts, boolean[] hasRange, boolean[] contested) {

		/**
		 * The cached bounds for this context: built once per curve set and
		 * shared (read-only) across solves and threads.
		 */
		static IterativeBounds cached(FittingSolverContext ctx) {
			return ctx.cache.computeIfAbsent("iterative.bounds", () -> forContext(ctx));
		}

		/**
		 * Builds the per-curve bounds and overlap data from the context's
		 * curve list.
		 */
		static IterativeBounds forContext(FittingSolverContext ctx) {
			List<CurveView> curves = ctx.curves;
			int curveCount = curves.size();

			int[] firsts = new int[curveCount];
			int[] lasts = new int[curveCount];
			boolean[] hasRange = new boolean[curveCount];
			for (int i = 0; i < curveCount; i++) {
				int[] ic = curves.get(i).getIntenseChannelList();
				if (ic.length > 0) {
					firsts[i] = ic[0];
					lasts[i] = ic[ic.length - 1];
					hasRange[i] = true;
				}
			}

			// Curves that overlap no other curve get their exact weight in
			// the initial pass; only contested curves need correction passes.
			boolean[] contested = new boolean[curveCount];
			for (int i = 0; i < curveCount; i++) {
				if (!hasRange[i]) continue;
				for (int j = i + 1; j < curveCount; j++) {
					if (hasRange[j] && curves.get(i).isOverlapping(curves.get(j))) {
						contested[i] = true;
						contested[j] = true;
					}
				}
			}

			return new IterativeBounds(firsts, lasts, hasRange, contested);
		}

	}

	@Override
	public String pluginName() {
		return "Iterative";
	}
	
	@Override
	public String toString() {
		return pluginName();
	}
	
	@Override
	public String pluginDescription() {
		return "Greedy fitting with extra refinement passes to improve fit and order independence";
	}
	
	@Override
	public String pluginVersion() {
		return "1.1";
	}
	
	@Override
	public String pluginUUID() {
		return "dcaad64b-45a4-4271-b4e9-20d7528658d2";
	}
	
	@Override
	public FittingResultSetView solve(FittingSolverContext ctx) {

		List<CurveView> curves = ctx.curves;
		int curveCount = curves.size();

		Spectrum resultTotalFit = new ArraySpectrum(ctx.data.size());
		List<FittingResultView> resultFits = new ArrayList<>();
		FittingParametersView resultParameters = ctx.fittings.getFittingParameters().copy();

		if (curveCount == 0) {
			Spectrum residual = new ArraySpectrum(ctx.data);
			return new FittingResultSet(resultTotalFit, residual, resultFits, resultParameters);
		}

		Spectrum remainder = new ArraySpectrum(ctx.data);
		float[] weights = new float[curveCount];
		IterativeBounds bounds = IterativeBounds.cached(ctx);

		// Get our initial greedy guess, with remainder and weights being changed
		float maxWeight = initialPass(ctx, bounds, remainder, weights);
		// Iterative refinement passes, weights come back changed in-place
		correctionPasses(ctx, bounds, remainder, weights, maxWeight);

		// build final results -- we don't use bounded spectrum methods here
		// to avoid any small distortions they may introduce. Weights should
		// be clamped at zero at this point.
		Spectrum scaled = new ArraySpectrum(ctx.data.size());
		for (int i = 0; i < curveCount; i++) {
			CurveView curve = curves.get(i);
			resultFits.add(new FittingResult(curve, weights[i]));
			curve.scaleInto(weights[i], scaled);
			SpectrumCalculations.addLists_inplace(resultTotalFit, scaled);
		}

		Spectrum residual = SpectrumCalculations.subtractLists(ctx.data, resultTotalFit);
		return new FittingResultSet(resultTotalFit, residual, resultFits, resultParameters);
	}

	/**
	 * The initial greedy pass: fit each curve in sequence against the
	 * remainder and subtract its contribution before moving on. Weights are
	 * clamped at zero, here and in the correction passes -- a negative weight
	 * would fabricate negative signal. Fills in {@code weights} and draws down
	 * {@code remainder} in place, and returns the largest weight seen, which
	 * the correction passes use to scale their convergence test.
	 */
	private float initialPass(FittingSolverContext ctx, IterativeBounds bounds, Spectrum remainder, float[] weights) {
		List<CurveView> curves = ctx.curves;
		int[] firsts = bounds.firsts();
		int[] lasts = bounds.lasts();
		boolean[] hasRange = bounds.hasRange();

		float maxWeight = 0f;
		for (int i = 0; i < curves.size(); i++) {
			if (!hasRange[i]) continue;
			CurveView curve = curves.get(i);

			// fit this curve against the remainder
			FittingResult result = ctx.fitter.fit(new CurveFitterContext(remainder, curve));
			weights[i] = Math.max(0f, result.getCurveScale());
			maxWeight = Math.max(maxWeight, weights[i]);

			// subtract the new fit from the remainder
			curve.scaleOnto(-weights[i], remainder, firsts[i], lasts[i]);
		}
		return maxWeight;
	}

	/**
	 * The correction passes: revisit each contested curve's weight given all
	 * the other curves' current contributions, until the largest relative
	 * weight change in a pass falls below tolerance (or we run out of passes).
	 * With the clamp at zero, a fitter that minimizes a per-channel loss (e.g.
	 * least squares) makes this projected cyclic coordinate descent, where the
	 * projection is just clamping the scale to >= 0. With other fitters (e.g.
	 * max-under-curve) the passes instead settle on a self-consistent fixed
	 * point -- every weight is the one the fitter would assign given all the
	 * other curves' contributions -- so the result still reflects the fitter's
	 * own strategy, just applied jointly rather than in sequence.
	 */
	private void correctionPasses(FittingSolverContext ctx, IterativeBounds bounds, Spectrum remainder, float[] weights, float maxWeight) {
		List<CurveView> curves = ctx.curves;
		int[] firsts = bounds.firsts();
		int[] lasts = bounds.lasts();
		boolean[] contested = bounds.contested();

		// Repeat this process up to a certain number of times
		for (int pass = 0; pass < MAX_CORRECTION_PASSES; pass++) {
			float maxRelDelta = 0f;
			
			// Examine each curve one at a time
			for (int i = 0; i < curves.size(); i++) {
				if (!contested[i]) continue;
				CurveView curve = curves.get(i);

				// add this curve's current contribution back into the remainder
				curve.scaleOnto(weights[i], remainder, firsts[i], lasts[i]);

				// then re-fit it against everything that's left
				float weightOld = weights[i];
				FittingResult result = ctx.fitter.fit(new CurveFitterContext(remainder, curve));
				weights[i] = Math.max(0f, result.getCurveScale());
				maxWeight = Math.max(maxWeight, weights[i]);

				// and subtract the new fit back out
				curve.scaleOnto(-weights[i], remainder, firsts[i], lasts[i]);

				// how big was the change, relative to the weight itself?
				// curves with negligible weight are skipped so their
				// oscillations can't keep the iteration alive
				float weightScale = Math.max(weightOld, weights[i]);
				if (weightScale > CONVERGENCE_TOLERANCE * maxWeight) {
					float relDelta = Math.abs(weights[i] - weightOld) / weightScale;
					if (relDelta > maxRelDelta) {
						maxRelDelta = relDelta;
					}
				}
			}
			// If the maximum relative change shrinks lower than the tolerance,
			// we consider the job done and exit the correction-passes loop.
			if (maxRelDelta < CONVERGENCE_TOLERANCE) {
				break;
			}
		}
	}
	
}
