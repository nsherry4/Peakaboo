package peakaboo.curvefit.model;

import peakaboo.curvefit.model.transitionseries.EscapePeakType;



public class FittingModel
{

	/**
	 * Existing TransitionSeries and their Fitting against raw data.
	 */
	public FittingSet			selections;
	
	/**
	 * Results of fitting existing selections
	 */
	public FittingResultSet		selectionResults;
	
	/**
	 * Proposed TransitionSeries and their Fitting against data after already being fit against current selections
	 */
	public FittingSet			proposals;
	
	/**
	 * Results of fitting proposed selections.
	 */
	public FittingResultSet		proposalResults;
	
	
	
	public FittingModel()
	{
		selections = new FittingSet();
		proposals = new FittingSet();
		selections.setEscapeType(EscapePeakType.getDefault());
		proposals.setEscapeType(EscapePeakType.getDefault());
		selectionResults = null;
		proposalResults = null;
	}
	
}
