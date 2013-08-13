package peakaboo.mapping.correction;

import java.io.BufferedReader;
import java.io.IOException;

import commonenvironment.AbstractFile;
import fava.datatypes.Pair;
import fava.functionable.FList;
import fava.signatures.FnCondition;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesMode;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.curvefit.peaktable.Element;


public class Corrections
{

	private String name;
	private FList<Pair<TransitionSeries, Float>> correctionPairs;
	
	public Corrections(AbstractFile file) throws IOException
	{
	
		correctionPairs = new FList<Pair<TransitionSeries, Float>>();
		
		BufferedReader reader = file.getReader();
		name = reader.readLine();
		
		while (true)
		{
			String line = reader.readLine();
			if (line == null) break;
			
			String parts[] = line.split(",");
			
			Element e = Element.values()[Integer.parseInt(parts[1])-1];
			TransitionSeriesType tst = TransitionSeriesType.valueOf(parts[0]);
			
			float factor = Float.parseFloat(parts[2]);
			
			//add a dummy transitionseries -- this will pass the TransitionSeries#equals test with the real thing
			correctionPairs.add(new Pair<TransitionSeries, Float>(new TransitionSeries(e, tst, TransitionSeriesMode.PRIMARY), factor));
		}
		
		
	}
	
	public Float getCorrection(final TransitionSeries ts)
	{		
		FList<Pair<TransitionSeries, Float>> matches = correctionPairs.filter(new FnCondition<Pair<TransitionSeries, Float>>() {
			
			public Boolean f(Pair<TransitionSeries, Float> p)
			{
				return p.first.equals(ts);
			}
		});
		
		if (matches .size() == 0) return null;
		return matches.head().second;
		
	}
	
	public String getName()
	{
		return name;
	}
	
}
