package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingParameters;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.ROFittingParameters;
import org.peakaboo.curvefit.curve.fitting.ROFittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

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
	public FittingResultSet solve(ReadOnlySpectrum data, ROFittingSet fittings, CurveFitter fitter) {

		
		Spectrum resultTotalFit = new ISpectrum(data.size());
		List<FittingResult> resultFits = new ArrayList<>();
		ROFittingParameters resultParameters = fittings.getFittingParameters().copy();
		
		Spectrum remainder = new ISpectrum(data);
		Spectrum scaled = new ISpectrum(data.size());
		
		// calculate the curves
		for (Curve curve : fittings.getCurves()) {
			if (!curve.getTransitionSeries().isVisible()) { continue; }
			
			FittingResult result = fitter.fit(remainder, curve);
			curve.scaleInto(result.getCurveScale(), scaled);
			SpectrumCalculations.subtractLists_inplace(remainder, scaled, 0.0f);
			
			//should this be done through a method addFit?
			resultFits.add(result);
			SpectrumCalculations.addLists_inplace(resultTotalFit, scaled);
		}

		
		FittingResultSet results = new FittingResultSet(resultTotalFit, remainder, resultFits, resultParameters);
		return results;
		
	}
	
}
