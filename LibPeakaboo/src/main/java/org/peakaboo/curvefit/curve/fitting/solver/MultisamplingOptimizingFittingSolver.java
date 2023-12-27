package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterPlugin;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public class MultisamplingOptimizingFittingSolver extends OptimizingFittingSolver {

	@Override
	public String pluginName() {
		return "MultiSampling";
	}
	
	@Override
	public String toString() {
		return pluginName();
	}
	
	@Override
	public String pluginDescription() {
		return "Optimizing solver run on several permutations of fittings order";
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "87eeb1e0-6c4e-4f80-9cf7-8c19a07423b5";
	}

	@Override
	public FittingResultSetView solve(ReadOnlySpectrum data, FittingSetView fittings, CurveFitterPlugin fitter) {
		
		int size = fittings.getVisibleCurves().size();
		if (size == 0) {
			return getEmptyResult(data, fittings);
		}
		
		List<CurveView> curves = fittings.getVisibleCurves();
		sortCurves(curves);
		List<Integer> intenseChannels = getIntenseChannels(curves);
		
		List<CurveView> perm = new ArrayList<>(curves);
		int counter = 0;
		double[] scalings = new double[size];
		while (counter <= 10) {
			Collections.shuffle(perm, new Random(12345654321l));
			
			
			double[] guess = getInitialGuess(perm, fitter, data);
			EvaluationContext context = new EvaluationContext(data, fittings, perm);
			MultivariateFunction cost = getCostFunction(context, intenseChannels);
			PointValuePair result = optimizeCostFunction(cost, guess, 0.02d);
			double[] permScalings = result.getPoint();
			
			//DON'T DO THIS, IT CAUSES ALL THE REST OF THE FITS TO BE BIASED TOWARDS THE FIRST ONE
			//next iteration's guess will be this iterations results
			//this improves performance like crazy over the first initial guess
			//guess = permScalings;
			
			for (int i = 0; i < scalings.length; i++) {
				CurveView c = perm.get(i);
				int j = curves.indexOf(c);
				scalings[j] += permScalings[i];
			}
			counter++;
			
		}
		
		for (int i = 0; i < scalings.length; i++) {
			scalings[i] /= counter;
		}

		EvaluationContext context = new EvaluationContext(data, fittings, curves);
		
		return evaluate(scalings, context);
		
		
	}
	
}
