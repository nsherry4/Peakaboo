package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingParameters;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class OptimizingFittingSolver implements FittingSolver {

	@Override
	public String name() {
		return "Optimizing";
	}
	
	@Override
	public String toString() {
		return name();
	}

	@Override
	public FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings, CurveFitter fitter) {
		int size = fittings.getVisibleCurves().size();
		if (size == 0) {
			return getEmptyResult(data, fittings);
		}
		
		List<Curve> curves = new ArrayList<>(fittings.getVisibleCurves());
		sortCurves(curves);
		Set<Integer> intenseChannels = getIntenseChannels(curves);
		EvaluationContext context = new EvaluationContext(data, fittings, curves);
		MultivariateFunction cost = getCostFunction(context, intenseChannels);
		double[] guess = getInitialGuess(size, curves, fitter, data);
				
			
		PointValuePair result = optimizeCostFunction(cost, guess, 0.01d);

		
		double[] scalings = result.getPoint();
		return evaluate(scalings, context);
		
		
	}
	
	protected FittingResultSet getEmptyResult(ReadOnlySpectrum data, FittingSet fittings) {
		return new FittingResultSet(
				new ISpectrum(data.size()), 
				new ISpectrum(data), 
				Collections.emptyList(), 
				FittingParameters.copy(fittings.getFittingParameters().copy())
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
	
	protected double[] getInitialGuess(int size, List<Curve> curves, CurveFitter fitter, ReadOnlySpectrum data) {
		double[] guess = new double[size];
		for (int i = 0; i < size; i++) {
			Curve curve = curves.get(i);
			FittingResult guessFittingResult = fitter.fit(data, curve);
			
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
	
	protected Set<Integer> getIntenseChannels(List<Curve> curves) {
		Set<Integer> intenseChannels = new LinkedHashSet<>();
		for (Curve curve : curves) {
			intenseChannels.addAll(curve.getIntenseChannels());
		}
		return intenseChannels;
	}
	
	protected MultivariateFunction getCostFunction(EvaluationContext context, Set<Integer> intenseChannels) {
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

				test(point, context);
				float score = score(point, intenseChannels, context.residual);
				if (containsNegatives > 0) {
					return score * (1f+containsNegatives);
				}
				return score;
				
			}
		};
	}
	
	protected void sortCurves(List<Curve> curves) {
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

	private void test(double[] point, EvaluationContext context) {
		int index = 0;
		context.scratch.zero();
		context.total.zero();
		for (Curve curve : context.curves) {
			float scale = (float) point[index++];
			curve.scaleInto(scale, context.scratch);
			SpectrumCalculations.addLists_inplace(context.total, context.scratch);
		}
		SpectrumCalculations.subtractLists_target(context.data, context.total, context.residual);

	}
	
	private float score(double[] point, Set<Integer> intenseChannels, Spectrum residual) {
		float[] ra = residual.backingArray();
		float score = 0;
		for (int i : intenseChannels) {
			float channelValue = ra[i];
			
			//Negative values mean that we've fit more signal than exists
			//We penalize this to prevent making up data where none exists.
			if (channelValue < 0) {
				channelValue = (-channelValue)*50;
			}
			score += channelValue;
		}
		
//		for (double p : point) {
//			System.out.print(p + ", ");
//		}
//		System.out.println("");
//		System.out.println(score);
		return score;
	}
	
	protected FittingResultSet evaluate(double[] point, EvaluationContext context) {
		int index = 0;
		List<FittingResult> fits = new ArrayList<>();
		Spectrum total = new ISpectrum(context.data.size());
		Spectrum scaled = new ISpectrum(context.data.size());
		for (Curve curve : context.curves) {
			float scale = (float) point[index++];
			curve.scaleInto(scale, scaled);
			fits.add(new FittingResult(curve, scale));
			SpectrumCalculations.addLists_inplace(total, scaled);
		}
		Spectrum residual = SpectrumCalculations.subtractLists(context.data, total);
		
		return new FittingResultSet(total, residual, fits, context.fittings.getFittingParameters().copy());
	}
	
	protected class EvaluationContext {
		public ReadOnlySpectrum data;
		public FittingSet fittings;
		public List<Curve> curves;
		public Spectrum scratch;
		public Spectrum total;
		public Spectrum residual;
		public EvaluationContext(ReadOnlySpectrum data, FittingSet fittings, List<Curve> curves) {
			this.data = data;
			this.fittings = fittings;
			this.curves = curves;
			this.scratch = new ISpectrum(data.size());
			this.total = new ISpectrum(data.size());
			this.residual = new ISpectrum(data.size());
		}
	}
	
}
