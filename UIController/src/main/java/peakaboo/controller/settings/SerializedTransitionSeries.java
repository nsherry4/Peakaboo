package peakaboo.controller.settings;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;


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
		
		
		
		String parts[];
		List<TransitionSeries> tss = new ArrayList<TransitionSeries>();
		TransitionSeries created;
		
		for (String tsd : components)
		{
			parts = tsd.split(":", 2);
			Element e = Element.valueOf(parts[0]);
			TransitionSeriesType tst = TransitionSeriesType.fromTypeString(parts[1]);
			
			created = PeakTable.SYSTEM.get(e, tst);
			
			tss.add(created);
		}
		
		TransitionSeries ts = TransitionSeries.summation(tss);
		ts.visible = this.visible;
		
		return ts;
		
	}
	
}
