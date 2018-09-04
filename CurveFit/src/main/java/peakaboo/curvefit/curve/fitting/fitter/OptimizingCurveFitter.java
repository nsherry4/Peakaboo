package peakaboo.curvefit.curve.fitting.fitter;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingResult;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class OptimizingCurveFitter implements CurveFitter {

	protected float overfitPenalty = 5f;
	
	@Override
	public FittingResult fit(ReadOnlySpectrum data, Curve curve) {
		float scale = this.findScale(data, curve);
		ReadOnlySpectrum scaledData = curve.scale(scale);
		FittingResult result = new FittingResult(scaledData, curve, scale);
		return result;
	}
	
	
	
	private float findScale(ReadOnlySpectrum data, Curve curve) {

		Set<Integer> intenseChannels = new LinkedHashSet<>();
		for (int channel : curve.getIntenseRanges()) {
			intenseChannels.add(channel);
		}
		
		
		UnivariateFunction score = new UnivariateFunction() {
			
			Spectrum scaled = new ISpectrum(data.size());
			Spectrum residual = new ISpectrum(data.size());
			
			@Override
			public double value(double scale) {
				curve.scaleInto((float) scale, scaled);
				residual.copy(data);
				SpectrumCalculations.subtractLists_inplace(residual, scaled);
				
				float score = 0;
				for (int i : intenseChannels) {
					float value = residual.get(i);
					if (value < 0) {
						value *= overfitPenalty;
					}
					value *= value;
					score += value;
				}
				return score;
			}
		};
		
		
		double guess = 0;
		for (int channel : intenseChannels) {
			guess = Math.max(guess, data.get(channel));
		}
		
		UnivariateOptimizer optimizer = new BrentOptimizer(0.0001, 0.00001);
		UnivariatePointValuePair result = optimizer.optimize(
				new UnivariateObjectiveFunction(score), 
				new SearchInterval(0, 1+guess*2, guess),
				new MaxIter(10000),
				new MaxEval(10000),
				GoalType.MINIMIZE
			);


	
		return (float) result.getPoint();
		
		
		
	}

	@Override
	public String name() {
		return "Optimizing";
	}
	
	@Override
	public String toString() {
		return name();
	}

}
