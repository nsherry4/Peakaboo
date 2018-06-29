package peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

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

public class LeastSquaresFittingSolver implements FittingSolver {

	@Override
	public String name() {
		return "Least Squares";
	}

	@Override
	public FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings, CurveFitter fitter) {
		List<Curve> visibleCurves = fittings.getVisibleCurves();
		
		if (visibleCurves.size() == 0) {
			return new FittingResultSet(
					new ISpectrum(data.size()), 
					new ISpectrum(data), 
					Collections.emptyList(), 
					FittingParameters.copy(fittings.getFittingParameters())
				);
		}
		
		
		
		
		//number of variables (1 scaling value per curve
		
		int curveCount = visibleCurves.size();
		int channelCount = data.size();
		
		double[] guesses = new double[curveCount];
		for (int curveIndex = 0; curveIndex < visibleCurves.size(); curveIndex++) {
			guesses[curveIndex] = fitter.fit(data, visibleCurves.get(curveIndex)).getCurveScale();
		}
		
		MultivariateJacobianFunction distanceFromData = point -> {
			
			

			FittingResultSet fits = evaluate(point, data, fittings);
			ReadOnlySpectrum residual = fits.getResidual();

		

			RealVector vector = new ArrayRealVector(channelCount);
			RealMatrix matrix = new Array2DRowRealMatrix(channelCount, curveCount);
			
			
			for (int channel = 0; channel < channelCount; channel++) {
				float distance = residual.get(channel);
				float absdist = Math.abs(distance);
				if (distance < 0) {
					absdist *= 1000;
				}
				
				
				vector.setEntry(channel, absdist);
				
				float deriv = 0;
				if (distance != 0) {
					deriv = distance / absdist;
				}
				
				int curveIndex = 0;
				for (Curve curve : visibleCurves) {
					if (curve.getIntenseRanges().contains(channel)) {
						matrix.setEntry(channel, curveIndex, -deriv);
					}
					curveIndex++;
				}
				

			}
			
			// TODO Auto-generated method stub
			return new Pair<>(vector, matrix);
		};
		
		
		LeastSquaresProblem problem = new LeastSquaresBuilder()
			.start(guesses)
			.target(new double[channelCount])
			.model(distanceFromData)
			.lazyEvaluation(false)
			.maxEvaluations(10000)
			.maxIterations(10000)
			.build();
		
		LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
		FittingResultSet fits = evaluate(optimum.getPoint(), data, fittings);
		
		return fits;
		
	}
	
	private FittingResultSet evaluate(RealVector point, ReadOnlySpectrum data, FittingSet fittings) {
		int index = 0;
		List<FittingResult> fits = new ArrayList<>();
		Spectrum total = new ISpectrum(data.size());
		for (Curve curve : fittings.getVisibleCurves()) {
			float scale = (float) point.getEntry(index++);
			Spectrum scaled = curve.scale(scale);
			fits.add(new FittingResult(scaled, curve, scale));
			SpectrumCalculations.addLists_inplace(total, scaled);
		}
		Spectrum residual = SpectrumCalculations.subtractLists(data, total);
		
		return new FittingResultSet(total, residual, fits, fittings.getFittingParameters());
	}

	
	
}
