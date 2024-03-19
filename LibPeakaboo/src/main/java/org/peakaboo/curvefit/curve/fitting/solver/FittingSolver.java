package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * Defines a method by which a {@link FittingSet} of {@link Curve}s are fit to a given {@link Spectrum}
 * @author NAS
 *
 */
public interface FittingSolver extends BoltJavaPlugin {

	public static class FittingSolverContext {
		
		// Given Values
		/**
		 * The spectrum for which we're performing fit solving
		 */
		public SpectrumView data;

		/**
		 * The fittings to be solved
		 */
		public FittingSetView fittings;
		
		/**
		 * The curve fitter which will perform single-curve fitting
		 */
		public CurveFitter fitter;
		
		
		
		// Derived Values
		
		/**
		 *  Sorted list of channels
		 */
		public List<CurveView> curves;

		/**
		 *  Subset of channels to focus on
		 */
		int[] channels;
		
		public FittingSolverContext(SpectrumView data, FittingSetView fittings, CurveFitter fitter) {
			this.data = data;
			this.fittings = fittings;
			this.fitter = fitter;
			
			// Generate sorted list of curves from visible fittings
			curves = new ArrayList<>(fittings.getVisibleCurves());
			sortCurves(curves);
			
			// Calculate a list of channels with enough curve signal to matter
			channels = getIntenseChannels(curves);
		}
		
		/**
		 * Performs a shallow copy of another {@link FittingSolverContext}
		 */
		public FittingSolverContext(FittingSolverContext copy) {
			this.data = copy.data;
			this.fittings = copy.fittings;
			this.fitter = copy.fitter;
			this.curves = copy.curves;
			this.channels = copy.channels;
		}
		
	}
	
	FittingResultSetView solve(FittingSolverContext ctx);

	/**
	 * Given a list of curves, sort them by by shell first, and then by element
	 */
	static void sortCurves(List<CurveView> curves) {
		curves.sort((a, b) -> {
			TransitionShell as, bs;
			as = a.getTransitionSeries().getShell();
			bs = b.getTransitionSeries().getShell();
			Element ae, be;
			ae = a.getTransitionSeries().getElement();
			be = b.getTransitionSeries().getElement();
			if (as.equals(bs)) {
				return ae.compareTo(be);
			} else {
				return as.compareTo(bs);
			}
		});
	}
	
	static int[] getIntenseChannels(List<CurveView> curves) {
		Set<Integer> intenseChannels = new LinkedHashSet<>();
		for (CurveView curve : curves) {
			intenseChannels.addAll(curve.getIntenseChannels());
		}
		List<Integer> asList = new ArrayList<>(intenseChannels);
		asList.sort(Integer::compare);
		int[] asArr = new int[asList.size()];
		for (int i = 0; i < asArr.length; i++) {
			asArr[i] = asList.get(i);
		}
		return asArr;
	}
	
	
	
}
