package org.peakaboo.curvefit.peak.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.curvefit.peak.transition.PrimaryTransitionSeries;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class SerializedPeakTable implements PeakTable {

	private static ScratchEncoder encoder = Serializers.kryo(
			PrimaryTransitionSeries.class,
			PrimaryTransitionSeries[].class,
			Transition.class,
			TransitionShell.class,
			Element.class,
			ArrayList.class
		);
	private List<PrimaryTransitionSeries> series;
	
	public SerializedPeakTable(PeakTable fallback, File file) {
		Path path = file.toPath();
		
		
		if (Files.exists(path)) {
			try {
				series = Arrays.asList(load(path));
			} catch (Exception e) {
				PeakabooLog.get().log(Level.WARNING, "Failed to read serialized peak table", e);
			}
		}
		
		//if it didn't load for whatever reason, create it
		if (series == null) {
			series = fallback.getAll();
			try {
				save(fallback, path);
			} catch (IOException e) {}
		}
		
		
		
	}
	
	private SerializedPeakTable(PrimaryTransitionSeries[] tss) {
		series = Arrays.asList(tss);
	}
	
	private static void save(PeakTable toStore, Path target) throws IOException {
		
		List<PrimaryTransitionSeries> tslist = toStore.getAll();
		PrimaryTransitionSeries[] tss = tslist.toArray(new PrimaryTransitionSeries[0]);
		byte[] serialized = encoder.encode(tss);
		Files.write(target, serialized, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		
	}
	
	private static PrimaryTransitionSeries[] load(Path source) throws IOException {
		
		byte[] serialized = Files.readAllBytes(source);
		PrimaryTransitionSeries[] tss = (PrimaryTransitionSeries[]) encoder.decode(serialized);
		return tss;
		
	}


	@Override
	public List<PrimaryTransitionSeries> getAll() {
		
		List<PrimaryTransitionSeries> copy = new ArrayList<>();
		for (PrimaryTransitionSeries ts : series) {
			copy.add(new PrimaryTransitionSeries(ts));
		}
		return copy;
		
	}
	
}
