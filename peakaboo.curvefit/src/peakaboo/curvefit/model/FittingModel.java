package peakaboo.curvefit.model;

import peakaboo.curvefit.model.transitionseries.EscapePeakType;



public class FittingModel
{

	public FittingSet			selections;
	public FittingResultSet		selectionResults;
	public FittingSet			proposals;
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
