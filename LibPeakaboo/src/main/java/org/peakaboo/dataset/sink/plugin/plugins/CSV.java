package org.peakaboo.dataset.sink.plugin.plugins;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.stream.Collectors;

import org.peakaboo.dataset.io.DataOutputAdapter;
import org.peakaboo.dataset.sink.plugin.AbstractDataSink;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class CSV extends AbstractDataSink {

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "b0727d75-2c00-43df-9205-e83cd699be91";
	}
	
	@Override
	public void write(DataSinkContext ctx) throws IOException, DataSinkWriteException {
		DataOutputAdapter destination = ctx.destination();
		DataSource source = ctx.source();
		
		Writer writer = new OutputStreamWriter(destination.getOutputStream());
				
		int counter = 0;
		for (SpectrumView s : source.getScanData()) {
			String spectrum = s.stream().map(f -> Float.toString(f)).collect(Collectors.joining(", "));
			writer.write(spectrum);
			writer.write("\n");
			counter++;
			if (counter == 100) {
				getInteraction().notifyScanWritten(counter);
				counter = 0;
				if (getInteraction().isAbortedRequested()) {
					return;
				}
			}
		}
		writer.flush();
		writer.close();
		
		try {
			destination.close();
		} catch (Exception e) {
			throw new DataSinkWriteException("Failed to close output stream", e);
		}

	}

	@Override
	public String getFormatExtension() {
		return "csv";
	}

	@Override
	public String getFormatName() {
		return "Comma Separated Values";
	}

	@Override
	public String getFormatDescription() {
		return "Comma Separated Value file with one spectrum per line";
	}

}
