package peakaboo.mapping.calibration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import peakaboo.curvefit.peak.table.PeakTable;
import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CalibrationReferenceManager {

	private static List<CalibrationReference> references = new ArrayList<>();
	private static final CalibrationReference empty = CalibrationReference.empty();
	
	static {
		read("NIST610");
	}
	
	public static List<CalibrationReference> getAll() {
		return new ArrayList<>(references);
	}
	
	public static CalibrationReference byUUID(String uuid) {
		for (CalibrationReference ref : references) {
			if (ref.getUuid().equals(uuid)) {
				return ref;
			}
		}
		return null;
	}
	
	public static List<CalibrationReference> forTransitionSeries(TransitionSeries ts) {
		return references.stream().filter(r -> r.contains(ts)).collect(Collectors.toList());
	}
	
	public static CalibrationReference empty() {
		return empty;
	}
	
	
	private static void read(String name) {
		System.out.println(name);
		URL url = PeakTable.class.getResource("/peakaboo/mapping/references/" + name + ".yaml");
		try {
			Scanner s = new Scanner(url.openStream()).useDelimiter("\\A");
			if (s.hasNext()) {
				references.add(CalibrationReference.load(s.next()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		System.out.println(getAll());
		
	}
	
	
}
