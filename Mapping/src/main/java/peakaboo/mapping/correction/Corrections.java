package peakaboo.mapping.correction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.peaktable.PeakTable;
import peakaboo.curvefit.transitionseries.TransitionSeries;
import peakaboo.curvefit.transitionseries.TransitionSeriesMode;
import peakaboo.curvefit.transitionseries.TransitionSeriesType;
import scitypes.Pair;


public class Corrections
{

	private String name;
	private ArrayList<Pair<TransitionSeries, Float>> correctionPairs;
	
	public Corrections(URL file) throws IOException
	{
	
		correctionPairs = new ArrayList<Pair<TransitionSeries, Float>>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.openStream()));
		name = reader.readLine();
		
		while (true)
		{
			String line = reader.readLine();
			if (line == null) break;
			
			String parts[] = line.split(",");
			
			Element e = Element.values()[Integer.parseInt(parts[1])-1];
			TransitionSeriesType tst = TransitionSeriesType.valueOf(parts[0]);
			
			float factor = Float.parseFloat(parts[2]);
			
			correctionPairs.add(new Pair<TransitionSeries, Float>(PeakTable.getTransitionSeries(e, tst), factor));
		}
		
		
	}
	
	public Float getCorrection(final TransitionSeries ts)
	{	
		return correctionPairs.stream().filter(p -> p.first.equals(ts)).findFirst().orElse(new Pair<>()).second;
	}
	
	public String getName()
	{
		return name;
	}
	
}
