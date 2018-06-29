package peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.List;

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

public class GreedyFittingSolver implements FittingSolver {


	public String name() {
		return "Greedy";
	}
	
	/**
	 * Fit this FittingSet against spectrum data
	 */
	@Override
	public FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings, CurveFitter fitter) {

		
		Spectrum resultTotalFit = new ISpectrum(data.size());
		List<FittingResult> resultFits = new ArrayList<>();
		FittingParameters resultParameters = FittingParameters.copy(fittings.getFittingParameters());
		
		// calculate the curves
		for (Curve curve : fittings.getCurves()) {
			if (!curve.getTransitionSeries().visible) { continue; }
						
			FittingResult result = fitter.fit(data, curve);
			data = SpectrumCalculations.subtractLists(data, result.getFit(), 0.0f);
			
			//should this be done through a method addFit?
			resultFits.add(result);
			SpectrumCalculations.addLists_inplace(resultTotalFit, result.getFit());
		}

		
		FittingResultSet results = new FittingResultSet(resultTotalFit, data, resultFits, resultParameters);
		return results;
		
	}
	
}
