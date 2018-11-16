package peakaboo.curvefit.peak.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.serializers.Serializers;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

public class SerializedPeakTable implements PeakTable {

	private static ScratchEncoder encoder = Serializers.fst(LegacyTransitionSeries.class, Transition.class);
	private List<LegacyTransitionSeries> series;
	
	public SerializedPeakTable(PeakTable fallback, File file) {
		Path path = file.toPath();
		
		
		if (Files.exists(path)) {
			try {
				series = Arrays.asList(load(path));
			} catch (Exception e) {}
		}
		
		//if it didn't load for whatever reason, create it
		if (series == null) {
			series = fallback.getAll();
			try {
				save(fallback, path);
			} catch (IOException e) {}
		}
		
		
		
	}
	
	private SerializedPeakTable(LegacyTransitionSeries[] tss) {
		series = Arrays.asList(tss);
	}
	
	private static void save(PeakTable toStore, Path target) throws IOException {
		
		List<LegacyTransitionSeries> tslist = toStore.getAll();
		LegacyTransitionSeries[] tss = tslist.toArray(new LegacyTransitionSeries[0]);
		byte[] serialized = encoder.encode(tss);
		Files.write(target, serialized, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		
	}
	
	private static LegacyTransitionSeries[] load(Path source) throws IOException {
		
		byte[] serialized = Files.readAllBytes(source);
		LegacyTransitionSeries[] tss = (LegacyTransitionSeries[]) encoder.decode(serialized);
		return tss;
		
	}


	@Override
	public List<LegacyTransitionSeries> getAll() {
		
		List<LegacyTransitionSeries> copy = new ArrayList<>();
		for (LegacyTransitionSeries ts : series) {
			copy.add(new LegacyTransitionSeries(ts));
		}
		return copy;
		
	}
	
}
