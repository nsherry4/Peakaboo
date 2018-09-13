package peakaboo.mapping.calibration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import peakaboo.common.YamlSerializer;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CalibrationReference {

	private String name;
	private String uuid;
	private Map<TransitionSeries, Float> concentrations;
	
	CalibrationReference() {
		name = null;
		uuid = null;
		concentrations = new HashMap<>();
	}
	
	public CalibrationReference(Path referenceFile) {
		concentrations = new HashMap<>();
		//TODO: Read some kind of reference file
	}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public String toString() {
		return "<CalibrationReference " + getName() + " (" + getUuid() + ")>"; 
	}
	
	public Map<TransitionSeries, Float> getConcentrations() {
		return new HashMap<>(concentrations);
	}
	
	public boolean contains(TransitionSeries ts) {
		return concentrations.containsKey(ts);
	}
	
	public float getConcentration(TransitionSeries ts) {
		return concentrations.get(ts);
	}
	
	public static CalibrationReference empty() {
		CalibrationReference empty = new CalibrationReference();
		empty.name = "Empty Calibration Reference";
		empty.uuid = "a3da5ff9-c6c5-4633-a40b-b576efe89da8";
		return empty;
	}
	
	
	public static String save(CalibrationReference reference) {
		SerializedCalibrationReference serialized = new SerializedCalibrationReference();
		serialized.name = reference.name;
		serialized.uuid = reference.uuid;
		for (TransitionSeries ts : reference.concentrations.keySet()) {
			serialized.concentrations.put(ts.toIdentifierString(), reference.concentrations.get(ts));
		}
		return YamlSerializer.serialize(serialized);
	}
	
	public static CalibrationReference load(String yaml) {
		SerializedCalibrationReference serialized = YamlSerializer.deserialize(yaml);
		CalibrationReference reference = new CalibrationReference();
		reference.name = serialized.name;
		reference.uuid = serialized.uuid;
		for (String tsidentifier : serialized.concentrations.keySet()) {
			reference.concentrations.put(PeakTable.SYSTEM.get(tsidentifier), serialized.concentrations.get(tsidentifier));
		}
		return reference;
	}
	
	private static CalibrationReference fromCSV(String data) {
		CalibrationReference reference = new CalibrationReference();
		List<String> lines = new ArrayList<>(Arrays.asList(data.split("\n")));
		
		reference.name = lines.remove(0);
		reference.uuid = lines.remove(0);
		
		for (String line : lines) {
			String[] parts = line.split(",");
			TransitionSeries ts = null;
			Float concentration = null;
			try {
				ts = PeakTable.SYSTEM.get(parts[0]);
				concentration = Float.parseFloat(parts[1]);
			} catch (NumberFormatException e) {
				continue;
			}
			if (ts == null || concentration == null) {
				continue;
			}
			reference.concentrations.put(ts, concentration);
						
		}
		
		return reference;
	}
	
	public static void main(String[] args) {
		URL url = PeakTable.class.getResource("/peakaboo/mapping/references/NIST610Reference.csv");
		try {
			Scanner s = new Scanner(url.openStream()).useDelimiter("\\A");
			if (s.hasNext()) {
				CalibrationReference reference = fromCSV(s.next());
				System.out.println(CalibrationReference.save(reference));
				CalibrationReference reference2 = CalibrationReference.load(CalibrationReference.save(reference));
				System.out.println(reference2.getConcentration(PeakTable.SYSTEM.get("Cr:K")));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

class SerializedCalibrationReference {
	public String name;
	public String uuid;
	public Map<String, Float> concentrations = new HashMap<>();
}
