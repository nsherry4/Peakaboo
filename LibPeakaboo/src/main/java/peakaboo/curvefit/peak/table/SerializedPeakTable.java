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
import peakaboo.curvefit.peak.transition.TransitionSeries;

public class SerializedPeakTable implements PeakTable {

	private static ScratchEncoder encoder = Serializers.fst(TransitionSeries.class, Transition.class);
	private List<TransitionSeries> series;
	
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
	
	private SerializedPeakTable(TransitionSeries[] tss) {
		series = Arrays.asList(tss);
	}
	
	private static void save(PeakTable toStore, Path target) throws IOException {
		
		List<TransitionSeries> tslist = toStore.getAll();
		TransitionSeries[] tss = tslist.toArray(new TransitionSeries[0]);
		byte[] serialized = encoder.encode(tss);
		Files.write(target, serialized, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		
	}
	
	private static TransitionSeries[] load(Path source) throws IOException {
		
		byte[] serialized = Files.readAllBytes(source);
		TransitionSeries[] tss = (TransitionSeries[]) encoder.decode(serialized);
		return tss;
		
	}


	@Override
	public List<TransitionSeries> getAll() {
		
		List<TransitionSeries> copy = new ArrayList<>();
		for (TransitionSeries ts : series) {
			copy.add(new TransitionSeries(ts));
		}
		return copy;
		
	}
	
}
