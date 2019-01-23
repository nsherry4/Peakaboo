package org.peakaboo.mapping.correction;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.curvefit.peak.table.PeakTable;


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
			correctionSets.put("WL", new Corrections(PeakTable.class.getResource("/peakaboo/mapping/correction/wl.csv"))); 
		}
		catch (IOException e)
		{
			PeakabooLog.get().log(Level.SEVERE, "Error reading corrections table", e);
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
