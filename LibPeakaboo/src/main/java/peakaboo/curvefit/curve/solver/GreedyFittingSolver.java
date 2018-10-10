package peakaboo.curvefit.curve.solver;

import java.util.ArrayList;
import java.util.List;

import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;
import peakaboo.curvefit.curve.Curve;
import peakaboo.curvefit.curve.FittingParameters;
import peakaboo.curvefit.curve.FittingResult;
import peakaboo.curvefit.curve.FittingResultSet;
import peakaboo.curvefit.curve.FittingSet;
import peakaboo.curvefit.curve.fitter.CurveFitter;

public class GreedyFittingSolver implements FittingSolver {


	public String name() {
		return "Greedy";
	}
	
	@Override
	public String toString() {
		return name();
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
