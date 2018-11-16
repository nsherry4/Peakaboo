package peakaboo.controller.settings;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;


public class SerializedTransitionSeries
{

	public List<String> components;
	public boolean visible;

	public SerializedTransitionSeries()
	{
		components = new ArrayList<String>();
	}
	
	public SerializedTransitionSeries(LegacyTransitionSeries ts)
	{
		this();
		
		components.clear();
		for (LegacyTransitionSeries bt : ts.getBaseTransitionSeries())
		{
			components.add(bt.getElement().name() + ":" + bt.getShell().name());
		}
		
		this.visible = ts.isVisible();
		
	}
	
	
	public LegacyTransitionSeries toTS()
	{
		
		List<LegacyTransitionSeries> tss = new ArrayList<LegacyTransitionSeries>();
		LegacyTransitionSeries created;
		
		for (String tsd : components)
		{
			created = PeakTable.SYSTEM.get(tsd);
			if (created != null) {
				tss.add(created);
			}
		}
		
		LegacyTransitionSeries ts = LegacyTransitionSeries.summation(tss);
		ts.setVisible(this.visible);
		
		return ts;
		
	}
	
}
