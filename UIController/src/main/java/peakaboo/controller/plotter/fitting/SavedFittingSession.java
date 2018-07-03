package peakaboo.controller.plotter.fitting;

import static java.util.stream.Collectors.toList;

import java.util.List;

import peakaboo.controller.settings.SerializedTransitionSeries;

public class SavedFittingSession {

	public List<SerializedTransitionSeries> fittings;
	
	public SavedFittingSession storeFrom(FittingController controller) {
		
		fittings = controller.fittingModel.selections.getFittedTransitionSeries()
				.stream()
				.map(ts -> new SerializedTransitionSeries(ts))
				.collect(toList());
		
		return this;
	}
	
	public SavedFittingSession loadInto(FittingController controller) {
		controller.fittingModel.selections.clear();		
		//we can't serialize TransitionSeries directly, so we store a list of Ni:K strings instead
		//we now convert them back to TransitionSeries
		for (SerializedTransitionSeries sts : this.fittings) {
			controller.fittingModel.selections.addTransitionSeries(sts.toTS());
		}
		
		return this;
	}
	
}
