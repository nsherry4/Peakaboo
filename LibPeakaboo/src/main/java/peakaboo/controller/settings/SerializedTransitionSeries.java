package peakaboo.controller.settings;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;


public class SerializedTransitionSeries
{

	public List<String> components;
	public boolean visible;

	public SerializedTransitionSeries()
	{
		components = new ArrayList<String>();
	}
	
	public SerializedTransitionSeries(TransitionSeries ts)
	{
		this();
		
		components.clear();
		for (TransitionSeries bt : ts.getBaseTransitionSeries())
		{
			components.add(bt.element.name() + ":" + bt.type.name());
		}
		
		this.visible = ts.visible;
		
	}
	
	
	public TransitionSeries toTS()
	{
		
		List<TransitionSeries> tss = new ArrayList<TransitionSeries>();
		TransitionSeries created;
		
		for (String tsd : components)
		{
			created = PeakTable.SYSTEM.get(tsd);
			if (created != null) {
				tss.add(created);
			}
		}
		
		TransitionSeries ts = TransitionSeries.summation(tss);
		ts.visible = this.visible;
		
		return ts;
		
	}
	
}
