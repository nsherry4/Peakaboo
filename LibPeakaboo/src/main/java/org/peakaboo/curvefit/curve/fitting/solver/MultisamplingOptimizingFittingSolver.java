package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;

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
	public FittingResultSetView solve(FittingSolverContext inputCtx) {
		
		int size = inputCtx.fittings.getVisibleCurves().size();
		if (size == 0) {
			return getEmptyResult(inputCtx);
		}
		
		// Create a shallow copy of the input context and then make a deep copy of the
		// curve list so that we can permute it without impacting the original
		FittingSolverContext permCtx = new FittingSolverContext(inputCtx);
		permCtx.curves = new ArrayList<>(permCtx.curves);
		
		int counter = 0;
		double[] scalings = new double[size];
		EvaluationSpace eval = new EvaluationSpace(permCtx.data.size());
		while (counter <= 10) {
			Collections.shuffle(permCtx.curves, new Random(12345654321l));
			
			
			double[] guess = getInitialGuess(permCtx);
			MultivariateFunction cost = getCostFunction(permCtx, eval);
			PointValuePair result = optimizeCostFunction(cost, guess, 0.02d);
			double[] permScalings = result.getPoint();
			
			//DON'T DO THIS, IT CAUSES ALL THE REST OF THE FITS TO BE BIASED TOWARDS THE FIRST ONE
			//next iteration's guess will be this iterations results
			//this improves performance like crazy over the first initial guess
			//guess = permScalings;
			
			for (int i = 0; i < scalings.length; i++) {
				// Map the scores back to the original permutation of the curves list
				CurveView c = permCtx.curves.get(i);
				int j = inputCtx.curves.indexOf(c);
				scalings[j] += permScalings[i];
			}
			counter++;
			
		}
		
		for (int i = 0; i < scalings.length; i++) {
			scalings[i] /= counter;
		}
		
		return evaluate(scalings, inputCtx);
		
		
	}
	
}
