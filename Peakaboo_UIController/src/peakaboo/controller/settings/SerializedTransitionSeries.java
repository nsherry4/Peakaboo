package peakaboo.controller.settings;

import java.util.ArrayList;
import java.util.List;

import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;


public class SerializedTransitionSeries
{

	public List<String> components;
		

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
			
			created = PeakTable.getTransitionSeries(e, tst);
			
			tss.add(created);
		}
		
				
		return TransitionSeries.summation(tss);
		
	}
	
}
