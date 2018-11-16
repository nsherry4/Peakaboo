package peakaboo.display.plot.painters;

import cyclops.SigDigits;
import cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter.DataLabel;
import cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import peakaboo.curvefit.curve.fitting.EnergyCalibration;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.peak.transition.TransitionSeries;

public class FittingLabel extends DataLabel {

	protected FittingResult fit;
	protected boolean drawMaxIntensities;
	protected String annotation;
	
	public FittingLabel(FittingResult fit, PlotPalette palette, EnergyCalibration ecal, String annotation, boolean drawMaxIntensities) {
		super(palette, 0, "");
		this.fit = fit;
		this.drawMaxIntensities = drawMaxIntensities;
		this.annotation = annotation;
		
		TransitionSeries ts = fit.getTransitionSeries();
		index = ecal.channelFromEnergy(ts.getStrongestTransition().energyValue);
		super.title = getTitle();
		
		
	}
	
	private String getTitle() {
		StringBuilder sb = new StringBuilder();
		TransitionSeries ts = fit.getTransitionSeries();
		String titleName = ts.toString();

		
		String titleHeight = SigDigits.roundFloatTo(fit.getCurveScale(), 1);

		sb.append(titleName);
		if (drawMaxIntensities) {
			sb.append(" (" + titleHeight + ")");
		}
		
		if (annotation != null) {
			if (sb.length() > 0) {
				sb.append(": ");
			}
			sb.append(annotation);
		}
		
		return sb.toString();
	}
	
	
}