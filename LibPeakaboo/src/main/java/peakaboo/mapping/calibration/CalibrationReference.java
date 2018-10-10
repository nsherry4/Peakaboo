package peakaboo.mapping.calibration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.sciencestudio.bolt.plugin.config.BoltConfigPlugin;
import peakaboo.common.YamlSerializer;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CalibrationReference implements BoltConfigPlugin {

	private String name;
	private String desc;
	private String uuid;
	private String rev;
	private TransitionSeries anchor;
	private Map<TransitionSeries, Float> concentrations;
	
	protected CalibrationReference() {
		name = null;
		uuid = null;
		desc = null;
		rev = null;
		anchor = null;
		concentrations = new HashMap<>();
	}
	
	

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}
	
	public String getDescription() {
		return desc;
	}

	public String getRevision() {
		return rev;
	}
	
	public TransitionSeries getAnchor() {
		return this.anchor;
	}
	
	public String toString() {
		return getName();
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
		empty.desc = "Empty Description Field";
		empty.rev = "1.0";
		return empty;
	}
	
	
	public static String save(CalibrationReference reference) {
		SerializedCalibrationReference serialized = new SerializedCalibrationReference();
		serialized.name = reference.name;
		serialized.uuid = reference.uuid;
		serialized.desc = reference.desc;
		serialized.rev = reference.rev;
		serialized.anchor = reference.anchor.toIdentifierString();
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
		reference.desc = serialized.desc;
		reference.rev = serialized.rev;
		System.out.println(serialized.anchor);
		reference.anchor = PeakTable.SYSTEM.get(serialized.anchor);
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
		reference.desc = lines.remove(0);
		reference.rev = lines.remove(0);
		
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
	
	public static void main(String[] args) throws IOException {

		
//		URL url = PeakTable.class.getResource("/peakaboo/mapping/references/NIST610.yaml");
//		Scanner s = new Scanner(url.openStream()).useDelimiter("\\A");
//		String yaml = s.next();
//		s.close();
//		CalibrationReference ref = CalibrationReference.load(yaml);
//		
//		yaml = CalibrationReference.save(ref);
//		System.out.println(yaml);
		
		
//		URL url = PeakTable.class.getResource("/peakaboo/mapping/references/NIST610Reference.csv");
//		try {
//			Scanner s = new Scanner(url.openStream()).useDelimiter("\\A");
//			if (s.hasNext()) {
//				CalibrationReference reference = fromCSV(s.next());
//				System.out.println(CalibrationReference.save(reference));
//				CalibrationReference reference2 = CalibrationReference.load(CalibrationReference.save(reference));
//				System.out.println(reference2.getConcentration(PeakTable.SYSTEM.get("Cr:K")));
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	//////////////////////////////////////////////
	// PLUGIN METHODS
	//////////////////////////////////////////////
	
	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginName() {
		return getName();
	}

	@Override
	public String pluginDescription() {
		return getDescription();
	}

	@Override
	public String pluginVersion() {
		return getRevision();
	}

	@Override
	public String pluginUUID() {
		return getUuid();
	}
	
}

class SerializedCalibrationReference {
	public String name;
	public String uuid;
	public String desc;
	public String rev;
	public String anchor;
	public Map<String, Float> concentrations = new HashMap<>();
}
