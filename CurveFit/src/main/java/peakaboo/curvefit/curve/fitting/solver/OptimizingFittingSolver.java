package peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.MultiDirectionalSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

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
		return "Optimizing (Beta)";
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
					FittingParameters.copy(fittings.getFittingParameters())
				);
		}
		
		Set<Integer> intenseChannels = new LinkedHashSet<>();
		for (Curve curve : curves) {
			for (int channel : curve.getIntenseRanges()) {
				intenseChannels.add(channel);
			}
		}
		
		MultivariateFunction cost = new MultivariateFunction() {
			
			@Override
			public double value(double[] point) {
				

				
				FittingResultSet result = evaluate(point, data, fittings);
				ReadOnlySpectrum residual = result.getResidual();
				
				double score = 0;
				for (int i = 0; i < residual.size(); i++) {
					if (! intenseChannels.contains(i)) { continue; }
					double channel = residual.get(i);
					
					//Negative values mean that we've fit more signal than exists
					//We penalize this to prevent making up data where none exists.
					if (channel < 0) {
						channel = Math.abs(channel);
						channel *= 1;
					}
					score += channel;
				}
				
				//System.out.println(Arrays.toString(point));
				//System.out.println(score);
				
				//We really don't like negative scaling factors, they don't make any logical sense.
				for (double v : point) {
					if (v < 0) {
						return Math.abs(v) * 1000000;
					}
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
		
		SimplexOptimizer solver = new SimplexOptimizer(-1d, 1d);
		PointValuePair result = solver.optimize(
				new ObjectiveFunction(cost), 
				new MultiDirectionalSimplex(size),
				new InitialGuess(guess),
				new MaxIter(10000),
				new MaxEval(10000),
				new NonNegativeConstraint(true), 
				GoalType.MINIMIZE
			);
		
		double[] scalings = result.getPoint();
		
		return evaluate(scalings, data, fittings);
		
		
	}

	private FittingResultSet evaluate(double[] point, ReadOnlySpectrum data, FittingSet fittings) {
		int index = 0;
		List<FittingResult> fits = new ArrayList<>();
		Spectrum total = new ISpectrum(data.size());
		for (Curve curve : fittings.getVisibleCurves()) {
			float scale = (float) point[index++];
			Spectrum scaled = curve.scale(scale);
			fits.add(new FittingResult(scaled, curve, scale));
			SpectrumCalculations.addLists_inplace(total, scaled);
		}
		Spectrum residual = SpectrumCalculations.subtractLists(data, total);
		
		return new FittingResultSet(total, residual, fits, FittingParameters.copy(fittings.getFittingParameters()));
	}
	
}
