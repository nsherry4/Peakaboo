package peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;

import cyclops.ReadOnlySpectrum;
import cyclops.util.ListOps;
import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;

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
		double[] guess = getInitialGuess(size, curves, fitter, data);
		
		
		Iterable<List<Curve>> permutations = ListOps.permutations(curves);
		int counter = 0;
		double[] scalings = new double[size];
		for (List<Curve> perm : permutations) {
			Collections.shuffle(perm, new Random(12345654321l));
			if (counter > 20) {
				break;
			}
			
			Set<Integer> intenseChannels = getIntenseChannels(perm);
			EvaluationContext context = new EvaluationContext(data, fittings, perm);
			MultivariateFunction cost = getCostFunction(context, intenseChannels);
			PointValuePair result = optimizeCostFunction(cost, guess, 0.01d);
			double[] permScalings = result.getPoint();
			
			//next iteration's guess will be this iterations results
			//this improves performance like crazy over the first initial guess
			guess = permScalings;
			
			//System.out.println(a2s(permScalings));
			
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
		
		//System.out.println(a2s(scalings));
		

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
