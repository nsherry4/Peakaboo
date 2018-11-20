package peakaboo.controller.settings;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;


public class SerializedTransitionSeries
{

	public List<String> components;
	public boolean visible;

	public SerializedTransitionSeries()
	{
		components = new ArrayList<String>();
	}
	
	public SerializedTransitionSeries(ITransitionSeries ts)
	{
		this();
		
		components.clear();
		for (ITransitionSeries bt : ts.getPrimaryTransitionSeries())
		{
			components.add(bt.getElement().name() + ":" + bt.getShell().name());
		}
		
		this.visible = ts.isVisible();
		
	}
	
	
	public ITransitionSeries toTS()
	{
		
		List<ITransitionSeries> tss = new ArrayList<>();
		ITransitionSeries created;
		
		for (String tsd : components)
		{
			created = PeakTable.SYSTEM.get(tsd);
			if (created != null) {
				tss.add(created);
			}
		}
		
		ITransitionSeries ts = LegacyTransitionSeries.summation(tss);
		ts.setVisible(this.visible);
		
		return ts;
		
	}
	
}
