package peakaboo.curvefit.curve.fitting.solver;

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

import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

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
		List<Curve> curves = fittings.getVisibleCurves();
		int size = curves.size();
		
		
		if (size == 0) {
			return new FittingResultSet(
					new ISpectrum(data.size()), 
					new ISpectrum(data), 
					Collections.emptyList(), 
					FittingParameters.copy(fittings.getFittingParameters().copy())
				);
		}
		
		Set<Integer> intenseChannels = new LinkedHashSet<>();
		for (Curve curve : curves) {
			for (int channel : curve.getIntenseRanges()) {
				intenseChannels.add(channel);
			}
		}
		
		EvaluationContext context = new EvaluationContext(data, fittings, curves);
		
		
		MultivariateFunction cost = new MultivariateFunction() {
			
			@Override
			public double value(double[] point) {
				
				//We really don't like negative scaling factors, they don't make any logical sense.
				for (double v : point) {
					if (v < 0) {
						return Math.abs(v) * 1000000;
					}
				}
				
				
				ReadOnlySpectrum residual = test(point, context);
				
				float score = 0;
				for (int i : intenseChannels) {
					float channel = residual.get(i);
					
					//Negative values mean that we've fit more signal than exists
					//We penalize this to prevent making up data where none exists.
					if (channel < 0) {
						channel = Math.abs(channel);
						channel *= 5;
					}
					score += channel;
				}
				
				return score;
			}
		};
		

		double[] guess = new double[size];
		for (int i = 0; i < size; i++) {
			Curve curve = curves.get(i);
			FittingResult guessFittingResult = fitter.fit(data, curve);
			guess[i] = guessFittingResult.getCurveScale();
		}
				
		
		
		
		//1358 reps on test session
		//optimizer = new SimplexOptimizer(-1d, 1d);
		
		//308 reps on test session
		//optimizer = new PowellOptimizer(0.001d, 1d);
		
		//265 reps on test session but occasionally dies?
		//optimizer = new BOBYQAOptimizer(Math.max(size+2, size*2));
		
		PointValuePair result = new PowellOptimizer(0.001d, 1d).optimize(
				new ObjectiveFunction(cost), 
				new InitialGuess(guess),
				new MaxIter(10000),
				new MaxEval(10000),
				new NonNegativeConstraint(true), 
				GoalType.MINIMIZE);


		
		double[] scalings = result.getPoint();
		
		return evaluate(scalings, context);
		
		
	}

	private Spectrum test(double[] point, EvaluationContext context) {
		int index = 0;
		List<FittingResult> fits = new ArrayList<>();
		context.scratch.zero();
		context.total.zero();
		for (Curve curve : context.curves) {
			float scale = (float) point[index++];
			curve.scaleInto(scale, context.scratch);
			fits.add(new FittingResult(context.scratch, curve, scale));
			SpectrumCalculations.addLists_inplace(context.total, context.scratch);
		}
		Spectrum residual = SpectrumCalculations.subtractLists(context.data, context.total);
		
		return residual;
	}
	
	private FittingResultSet evaluate(double[] point, EvaluationContext context) {
		int index = 0;
		List<FittingResult> fits = new ArrayList<>();
		Spectrum total = new ISpectrum(context.data.size());
		for (Curve curve : context.curves) {
			float scale = (float) point[index++];
			Spectrum scaled = curve.scale(scale);
			fits.add(new FittingResult(scaled, curve, scale));
			SpectrumCalculations.addLists_inplace(total, scaled);
		}
		Spectrum residual = SpectrumCalculations.subtractLists(context.data, total);
		
		return new FittingResultSet(total, residual, fits, context.fittings.getFittingParameters().copy());
}
	
	private class EvaluationContext {
		public ReadOnlySpectrum data;
		public FittingSet fittings;
		public List<Curve> curves;
		public Spectrum scratch;
		public Spectrum total;
		public EvaluationContext(ReadOnlySpectrum data, FittingSet fittings, List<Curve> curves) {
			this.data = data;
			this.fittings = fittings;
			this.curves = curves;
			this.scratch = new ISpectrum(data.size());
			this.total = new ISpectrum(data.size());
		}
	}
	
}
