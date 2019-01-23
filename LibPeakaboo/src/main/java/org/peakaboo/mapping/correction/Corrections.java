package org.peakaboo.mapping.correction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

import cyclops.Pair;


public class Corrections
{

	private String name;
	private ArrayList<Pair<ITransitionSeries, Float>> correctionPairs;
	
	public Corrections(URL file) throws IOException
	{
	
		correctionPairs = new ArrayList<>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.openStream()));
		name = reader.readLine();
		
		while (true)
		{
			String line = reader.readLine();
			if (line == null) break;
			
			String parts[] = line.split(",");
			
			Element e = Element.values()[Integer.parseInt(parts[1])-1];
			TransitionShell tst = TransitionShell.valueOf(parts[0]);
			
			float factor = Float.parseFloat(parts[2]);
			
			correctionPairs.add(new Pair<ITransitionSeries, Float>(PeakTable.SYSTEM.get(e, tst), factor));
		}
		
		
	}
	
	public Float getCorrection(final ITransitionSeries ts)
	{	
		return correctionPairs.stream().filter(p -> p.first.equals(ts)).findFirst().orElse(new Pair<>()).second;
	}
	
	public String getName()
	{
		return name;
	}
	
}
