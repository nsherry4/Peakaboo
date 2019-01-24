package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;

import cyclops.ReadOnlySpectrum;

public class MultisamplingOptimizingFittingSolver extends OptimizingFittingSolver {

	@Override
	public String name() {
		return "MultiSampling";
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
		
		List<Curve> perm = new ArrayList<>(curves);
		int counter = 0;
		double[] scalings = new double[size];
		while (counter <= 10) {
			Collections.shuffle(perm, new Random(12345654321l));
			
			
			double[] guess = getInitialGuess(size, perm, fitter, data);
			EvaluationContext context = new EvaluationContext(data, fittings, perm);
			MultivariateFunction cost = getCostFunction(context, intenseChannels);
			PointValuePair result = optimizeCostFunction(cost, guess, 0.02d);
			double[] permScalings = result.getPoint();
			
			//DON'T DO THIS, IT CAUSES ALL THE REST OF THE FITS TO BE BIASED TOWARDS THE FIRST ONE
			//next iteration's guess will be this iterations results
			//this improves performance like crazy over the first initial guess
			//guess = permScalings;
			
			for (int i = 0; i < scalings.length; i++) {
				Curve c = perm.get(i);
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
	
	private String a2s(double[] scalings) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < scalings.length; i++) {
			sb.append(scalings[i]);
			if (i != scalings.length-1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
}
