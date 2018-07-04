package peakaboo.curvefit.curve.fitting.fitter;



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
import peakaboo.curvefit.curve.fitting.FittingResult;
import scitypes.RangeSet;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

public class LeastSquaresCurveFitter implements CurveFitter {

	@Override
	public String name() {
		return "Least Squares";
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	@Override
	public FittingResult fit(ReadOnlySpectrum data, Curve curve) {
		float scale = this.findLeastSquaresScaleFactor(data, curve);
		ReadOnlySpectrum scaledData = curve.scale(scale);
		FittingResult result = new FittingResult(scaledData, curve, scale);
		return result;
		
	}
	
	private float findLeastSquaresScaleFactor(ReadOnlySpectrum data, Curve curve) {
		RangeSet channels = curve.getIntenseRanges();
		
		//find channel count
		int channelCount = this.channelCount(curve);
		
		MultivariateJacobianFunction distanceFromData = point -> {
				
			float scale = (float) point.getEntry(0);
			Spectrum scaled = curve.scale(scale);
			
			//These store the total distance and distance-per-dimension.
			//Since we have only one dimension, these should be the same..?
			RealVector vector = new ArrayRealVector(channelCount);
			RealMatrix matrix = new Array2DRowRealMatrix(channelCount, 1);
			
			
			int index = 0;
			for (int channel : channels) {
				float distance = scaled.get(channel) - data.get(channel);
				float absdist = Math.abs(distance);
				vector.setEntry(index, absdist);
				float deriv = 0;
				if (distance != 0) {
					deriv = distance / absdist;
				}
				
				matrix.setEntry(index, 0, deriv);
				index++;
			}
			
			// TODO Auto-generated method stub
			return new Pair<>(vector, matrix);
		};
		
		
		LeastSquaresProblem problem = new LeastSquaresBuilder()
			.start(new double[1] )
			.target(new double[channelCount])
			.model(distanceFromData)
			.lazyEvaluation(false)
			.maxEvaluations(1000)
			.maxIterations(1000)
			.build();
		
		LeastSquaresOptimizer.Optimum optimum = new LevenbergMarquardtOptimizer().optimize(problem);
		float scale = (float) optimum.getPoint().getEntry(0);
		
		return scale;
	}
	
	private int channelCount(Curve curve) {
		RangeSet channels = curve.getIntenseRanges();
		
		int count = 0;
		for (int i : channels) {
			count++;
		}
		
		return count;
	}

	
	
}
