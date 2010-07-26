package peakaboo.controller.settings;

import java.util.List;



import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.curvefit.peaktable.TransitionSeriesType;
import peakaboo.datatypes.DataTypeFactory;


public class SerializedTransitionSeries
{

	public List<String> components;
		

	public SerializedTransitionSeries()
	{
		components = DataTypeFactory.<String>list();
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
		List<TransitionSeries> tss = DataTypeFactory.<TransitionSeries>list();
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
