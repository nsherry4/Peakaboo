package org.peakaboo.curvefit.curve.fitting.fitter;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class OptimizingCurveFitter implements CurveFitter {

	protected float overfitPenalty = 5f;
	
	@Override
	public FittingResult fit(CurveFitterContext ctx) {
		float scale = this.findScale(ctx);
		FittingResult result = new FittingResult(ctx.curve(), scale);
		return result;
	}
	
	
	
	private float findScale(CurveFitterContext ctx) {

		UnivariateFunction score = scoringFunction(ctx.data(), ctx.curve());
		
		double guess = 0;
		for (int channel : ctx.curve().getIntenseChannelList()) {
			guess = Math.max(guess, ctx.data().get(channel));
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
	
	protected UnivariateFunction scoringFunction(SpectrumView data, CurveView curve) {
		return new UnivariateFunction() {
			
			Spectrum scaled = new ArraySpectrum(data.size());
			Spectrum residual = new ArraySpectrum(data.size());
			
			@Override
			public double value(double scale) {
				var intenseChannels = curve.getIntenseChannelList();
				
				//If there are no intense channels, we return a 0
				if (intenseChannels.isEmpty()) {
					return 0;
				}
				
				int firstChannel = intenseChannels.get(0);
				int lastChannel = intenseChannels.get(intenseChannels.size()-1);
				//curve.scaleInto((float) scale, scaled);
				//SpectrumCalculations.subtractLists_target(data, scaled, residual);
				curve.scaleOnto((float)-scale, data, residual, firstChannel, lastChannel);
				
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
	}
	
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
		return "Least squares curve fitting weighted against overfitting";
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "9e7caaf0-4684-4c50-bca7-e6a304a6fd6b";		
	}
	
}
