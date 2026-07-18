package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.EnergyCalibration;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolver.FittingSolverContext;
import org.peakaboo.curvefit.peak.table.CombinedPeakTable;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.KrausePeakTable;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.table.XrayLibPeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * Builds synthetic spectra from real element emission curves at known true
 * weights, for testing and benchmarking fitting solvers against ground truth.
 */
public final class SyntheticSpectrumFixture {

	public static final EnergyCalibration CALIBRATION = new EnergyCalibration(0f, 20f, 2048);
	public static final PeakTable PEAK_TABLE = new CombinedPeakTable(new XrayLibPeakTable(), new KrausePeakTable());

	private SyntheticSpectrumFixture() {}

	/**
	 * A set of fittings along with the true per-series weights used to
	 * synthesize data spectra.
	 */
	public static class Scenario {
		public final String name;
		public final FittingSet fittings;
		public final Map<ITransitionSeries, Double> trueWeights;

		public Scenario(String name, Map<ITransitionSeries, Double> trueWeights) {
			this.name = name;
			this.trueWeights = trueWeights;
			this.fittings = new FittingSet();
			this.fittings.getFittingParameters().setCalibration(CALIBRATION);
			for (ITransitionSeries ts : trueWeights.keySet()) {
				this.fittings.addTransitionSeries(ts);
			}
		}

		/**
		 * Synthesizes a data spectrum: sum of curves at their true weights, plus an
		 * unmodelled constant background, plus Poisson-approximate noise.
		 * @param background constant counts added to every channel (not part of any curve)
		 * @param seed RNG seed for noise; negative for a noiseless spectrum
		 */
		public Spectrum synthesize(float background, long seed) {
			return synthesize(trueWeights, background, seed);
		}

		/**
		 * Synthesizes a data spectrum from an alternate weight map (e.g. randomized
		 * draws around the scenario's base weights).
		 */
		public Spectrum synthesize(Map<ITransitionSeries, Double> weights, float background, long seed) {
			Spectrum data = new ArraySpectrum(CALIBRATION.getDataWidth(), background);
			for (CurveView curve : fittings.getVisibleCurves()) {
				double weight = weights.get(curve.getTransitionSeries());
				curve.scaleOnto((float) weight, data);
			}
			if (seed >= 0) {
				Random rng = new Random(seed);
				float[] backing = data.backingArray();
				for (int i = 0; i < backing.length; i++) {
					// Poisson noise approximated as gaussian with sigma = sqrt(counts)
					float noisy = backing[i] + (float) (Math.sqrt(Math.max(backing[i], 1f)) * rng.nextGaussian());
					backing[i] = Math.max(noisy, 0f);
				}
			}
			return data;
		}

		public FittingSolverContext context(SpectrumView data) {
			return new FittingSolverContext(data, fittings, new UnderCurveFitter());
		}
	}

	public static ITransitionSeries series(Element element, TransitionShell shell) {
		ITransitionSeries ts = PEAK_TABLE.get(element, shell);
		if (ts == null) {
			throw new IllegalArgumentException("Peak table has no entry for " + element + " " + shell);
		}
		return ts;
	}

	private static Scenario scenario(String name, Object... seriesAndWeights) {
		Map<ITransitionSeries, Double> weights = new LinkedHashMap<>();
		for (int i = 0; i < seriesAndWeights.length; i += 3) {
			Element element = (Element) seriesAndWeights[i];
			TransitionShell shell = (TransitionShell) seriesAndWeights[i + 1];
			double weight = ((Number) seriesAndWeights[i + 2]).doubleValue();
			weights.put(series(element, shell), weight);
		}
		return new Scenario(name, weights);
	}

	/** Well-separated peaks: no meaningful overlap between curves. */
	public static Scenario wellSeparated() {
		return scenario("Well-separated (Fe K, Sr K, Pb L)",
				Element.Fe, TransitionShell.K, 800.0,
				Element.Sr, TransitionShell.K, 350.0,
				Element.Pb, TransitionShell.L, 150.0);
	}

	/** Adjacent transition metals: each element's K-beta overlaps the next element's K-alpha. */
	public static Scenario overlapChain() {
		return scenario("Overlap chain (Mn, Fe, Co, Ni K)",
				Element.Mn, TransitionShell.K, 400.0,
				Element.Fe, TransitionShell.K, 900.0,
				Element.Co, TransitionShell.K, 120.0,
				Element.Ni, TransitionShell.K, 500.0);
	}

	/** Classic pathological XRF overlap: Pb L-alpha and As K-alpha both sit near 10.5 keV. */
	public static Scenario pathologicalOverlap() {
		return scenario("Pathological overlap (As K, Pb L)",
				Element.As, TransitionShell.K, 300.0,
				Element.Pb, TransitionShell.L, 450.0);
	}

	/** Overlap chain plus elements that are not actually present in the data. */
	public static Scenario absentElements() {
		return scenario("Absent elements (Mn, Fe, Co, Ni present; Cu, Zn absent)",
				Element.Mn, TransitionShell.K, 400.0,
				Element.Fe, TransitionShell.K, 900.0,
				Element.Co, TransitionShell.K, 120.0,
				Element.Ni, TransitionShell.K, 500.0,
				Element.Cu, TransitionShell.K, 0.0,
				Element.Zn, TransitionShell.K, 0.0);
	}

	/** A realistically-sized fitting: a dozen K-series plus heavy-element L-series. */
	public static Scenario manyElements() {
		return scenario("Many elements (12 K-series + 2 L-series)",
				Element.K,  TransitionShell.K, 150.0,
				Element.Ca, TransitionShell.K, 600.0,
				Element.Ti, TransitionShell.K, 80.0,
				Element.Cr, TransitionShell.K, 40.0,
				Element.Mn, TransitionShell.K, 400.0,
				Element.Fe, TransitionShell.K, 900.0,
				Element.Co, TransitionShell.K, 120.0,
				Element.Ni, TransitionShell.K, 500.0,
				Element.Cu, TransitionShell.K, 250.0,
				Element.Zn, TransitionShell.K, 180.0,
				Element.As, TransitionShell.K, 300.0,
				Element.Sr, TransitionShell.K, 350.0,
				Element.Pb, TransitionShell.L, 450.0,
				Element.Au, TransitionShell.L, 90.0);
	}

	/**
	 * Maps a solver's results back to per-series recovered weights.
	 */
	public static Map<ITransitionSeries, Double> recoveredWeights(FittingResultSetView results) {
		Map<ITransitionSeries, Double> recovered = new LinkedHashMap<>();
		for (FittingResultView fit : results.getFits()) {
			recovered.put(fit.getTransitionSeries(), (double) fit.getCurveScale());
		}
		return recovered;
	}

	/**
	 * Total fabricated signal: sum over intense channels of max(0, fit - data),
	 * measuring how much signal the solver explained that does not exist.
	 */
	public static double overfitSum(FittingResultSetView results, SpectrumView data, int[] channels) {
		SpectrumView fit = results.getTotalFit();
		double sum = 0;
		for (int ch : channels) {
			sum += Math.max(0, fit.get(ch) - data.get(ch));
		}
		return sum;
	}

}
