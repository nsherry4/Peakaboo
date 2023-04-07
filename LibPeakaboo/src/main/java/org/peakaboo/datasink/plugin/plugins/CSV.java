package org.peakaboo.datasink.plugin.plugins;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.stream.Collectors;

import org.peakaboo.datasink.model.outputfile.OutputFile;
import org.peakaboo.datasink.plugin.AbstractDataSink;
import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public class CSV extends AbstractDataSink {

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "b0727d75-2c00-43df-9205-e83cd699be91";
	}
	
	@Override
	public void write(DataSource source, OutputFile output) throws IOException, DataSinkWriteException {
		
		Writer writer = new OutputStreamWriter(output.getOutputStream());
				
		int counter = 0;
		for (ReadOnlySpectrum s : source.getScanData()) {
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
			output.close();
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
