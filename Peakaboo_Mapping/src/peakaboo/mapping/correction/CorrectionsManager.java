package peakaboo.mapping.correction;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import commonenvironment.AbstractFile;

import peakaboo.curvefit.peaktable.PeakTableReader;


public class CorrectionsManager
{
	
	private static Map<String, Corrections> correctionSets;
	private static boolean initialized = false;
	
	private static void initialize()
	{
		initialized = true;
		correctionSets = new HashMap<String, Corrections>();
		try
		{
			correctionSets.put("WL", new Corrections(new AbstractFile(PeakTableReader.class.getResource("/peakaboo/mapping/correction/wl.csv")))); 
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static Collection<String> getCorrectionList()
	{
		if (!initialized) initialize();
		return correctionSets.keySet();
	}
	
	public static Corrections getCorrections(String name)
	{
		if (!initialized) initialize();
		return correctionSets.get(name);
	}
	
}
