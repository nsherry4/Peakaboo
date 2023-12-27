package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.curvefit.curve.fitting.CurveView;
import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter.CurveFitterContext;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class GreedyFittingSolver implements FittingSolver {


	public String pluginName() {
		return "Greedy";
	}
	
	@Override
	public String toString() {
		return pluginName();
	}
	
	@Override
	public String pluginDescription() {
		return "Sequentially matches fittings to as much signal as they will fit";
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "648ed04b-83fa-4582-8d25-b67c4770e4ed";
	}

	/**
	 * Fit this FittingSet against spectrum data
	 */
	@Override
	public FittingResultSetView solve(FittingSolverContext ctx) {

		ReadOnlySpectrum data = ctx.data();
		FittingSetView fittings = ctx.fittings();
		CurveFitter fitter = ctx.fitter();
		
		Spectrum resultTotalFit = new ISpectrum(data.size());
		List<FittingResultView> resultFits = new ArrayList<>();
		FittingParametersView resultParameters = fittings.getFittingParameters().copy();
		
		Spectrum remainder = new ISpectrum(data);
		Spectrum scaled = new ISpectrum(data.size());
		
		// calculate the curves
		for (CurveView curve : fittings.getCurves()) {
			if (!curve.getTransitionSeries().isVisible()) { continue; }
			
			FittingResult result = fitter.fit(new CurveFitterContext(remainder, curve));
			curve.scaleInto(result.getCurveScale(), scaled);
			SpectrumCalculations.subtractLists_inplace(remainder, scaled, 0.0f);
			
			//should this be done through a method addFit?
			resultFits.add(result);
			SpectrumCalculations.addLists_inplace(resultTotalFit, scaled);
		}

		
		FittingResultSetView results = new FittingResultSet(resultTotalFit, remainder, resultFits, resultParameters);
		return results;
		
	}


	
}
