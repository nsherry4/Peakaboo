package peakaboo.curvefit.fitting.solver;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.fitting.Curve;
import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.fitting.FittingResult;
import peakaboo.curvefit.fitting.FittingResultSet;
import peakaboo.curvefit.fitting.FittingSet;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class GreedyFittingSolver implements FittingSolver {

	
	/**
	 * Fit this FittingSet against spectrum data
	 */
	@Override
	public FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings) {

		
		Spectrum resultTotalFit = new ISpectrum(data.size());
		List<FittingResult> resultFits = new ArrayList<>();
		FittingParameters resultParameters = FittingParameters.copy(fittings.getFittingParameters());
		
		// calculate the curves
		for (Curve curve : fittings.getCurves()) {
			if (!curve.getTransitionSeries().visible) { continue; }
			
			FittingResult result = curve.fit(data);
			data = SpectrumCalculations.subtractLists(data, result.getFit(), 0.0f);
			
			//should this be done through a method addFit?
			resultFits.add(result);
			SpectrumCalculations.addLists_inplace(resultTotalFit, result.getFit());
		}

		
		FittingResultSet results = new FittingResultSet(resultTotalFit, data, resultFits, resultParameters);
		return results;
		
	}
	
}
