package peakaboo.curvefit.curve.fitting.fitter;

public class LeastSquaresCurveFitter extends OptimizingCurveFitter {

	public LeastSquaresCurveFitter() {
		//neutralize the overfit penalty to get proper least-squares behaviour
		overfitPenalty = 1f;
	}
	
	@Override
	public String name() {
		return "Least-Squares";
	}
	
}
