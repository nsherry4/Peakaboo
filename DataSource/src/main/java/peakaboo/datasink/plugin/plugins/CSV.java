package peakaboo.datasink.plugin.plugins;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.stream.Collectors;

import peakaboo.datasink.plugin.JavaDataSinkPlugin;
import peakaboo.datasource.model.DataSource;
import scitypes.ReadOnlySpectrum;

public class CSV implements JavaDataSinkPlugin {

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginName() {
		return getFormatName();
	}

	@Override
	public String pluginDescription() {
		return getFormatDescription();
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public void write(DataSource source, OutputStream destination) throws IOException {
		Writer writer = new OutputStreamWriter(destination);
		for (ReadOnlySpectrum s : source.getScanData()) {
			String spectrum = s.stream().map(f -> Float.toString(f)).collect(Collectors.joining(", "));
			writer.write(spectrum);
			writer.write("\n");
		}
		writer.close();		
	}

	@Override
	public String getFormatExtension() {
		return "csv";
	}

	@Override
	public String getFormatName() {
		return "Comma Separated Value";
	}

	@Override
	public String getFormatDescription() {
		return "Comma Separated Value file with one spectrum per line";
	}

}
