package peakaboo.mapping.calibration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.common.base.Function;

import cyclops.ISpectrum;
import cyclops.Spectrum;
import net.sciencestudio.bolt.plugin.config.BoltConfigPlugin;
import peakaboo.common.YamlSerializer;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class CalibrationReference implements BoltConfigPlugin {

	private String name;
	private String desc;
	private String uuid;
	private String rev;
	private String notes;
	private List<String> citations;
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
		
	public String getNotes() {
		return notes;
	}

	public List<String> getCitations() {
		if (citations == null) { return new ArrayList<>(); }
		return new ArrayList<>(citations);
	}

	public String toString() {
		return getName();
	}
	
	public Map<TransitionSeries, Float> getConcentrations() {
		return new HashMap<>(concentrations);
	}
	
	/**
	 * returns a sorted list of TransitionSeries in this reference 
	 */
	public List<TransitionSeries> getTransitionSeries(TransitionSeriesType tst) {
		List<TransitionSeries> tss = concentrations
				.keySet()
				.stream()
				.filter(ts -> ts.type == tst)
				.sorted((a, b) -> Integer.compare(a.element.ordinal(), b.element.ordinal()))
				.collect(Collectors.toList());
		return tss;
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
		empty.notes = "";
		empty.citations = new ArrayList<>();
		return empty;
	}
		
	
	public static String save(CalibrationReference reference) {
		SerializedCalibrationReference serialized = new SerializedCalibrationReference();
		serialized.name = reference.name;
		serialized.uuid = reference.uuid;
		serialized.desc = reference.desc;
		serialized.rev = reference.rev;
		serialized.notes = reference.notes;
		serialized.citations = reference.citations;
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
		reference.notes = serialized.notes;
		reference.citations = serialized.citations;
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
		reference.notes = lines.remove(0);
		String[] citations = lines.remove(0).split("\\|");
		reference.citations = new ArrayList<>(Arrays.asList(citations));
		reference.anchor = PeakTable.SYSTEM.get(lines.remove(0));
		
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
		
		
		//URL url = PeakTable.class.getResource();
		File file = new File("/home/nathaniel/Desktop/NIST610ReferenceRevised.csv");
		try {
			Scanner s = new Scanner(new FileInputStream(file)).useDelimiter("\\A");
			if (s.hasNext()) {
				CalibrationReference reference = fromCSV(s.next());
				System.out.println(CalibrationReference.save(reference));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		String desc = getDescription();
		if (desc == null) { desc = ""; }
		desc = desc.trim();
		if (desc.length() > 0) { desc = "<b>Description</b><br/>" + desc; }
		
		String notes = getNotes();
		if (notes == null) { notes = ""; }
		notes = notes.trim();
		if (notes.length() > 0) { notes = "<b>Notes</b><br/>" + notes; }
		
		List<String> citelist = getCitations();
		if (citelist == null) { citelist = new ArrayList<>(); }
		String cites = "";
		if (citelist.size() > 0) {
			cites = "<b>Citations</b><ul>" + getCitations().stream().map(s -> "<li>" + s + "</li>").reduce("", (a, b) -> a+b) + "</ul>";
		}
		
		StringBuilder sb = new StringBuilder();
		if (desc.length() > 0) {
			sb.append(desc);
			sb.append("<br/><br/>");
		}
		
		if (notes.length() > 0) {
			sb.append(notes);
			sb.append("<br/><br/>");
		}
		
		if (cites.length() > 0) {
			sb.append(cites);
		}
		
		return sb.toString();
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
	public List<String> citations;
	public String notes;
	public Map<String, Float> concentrations = new HashMap<>();
}
