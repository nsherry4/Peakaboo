package org.peakaboo.datasink.model;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasink.model.components.interaction.CallbackInteraction;
import org.peakaboo.datasink.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.executor.ExecutorSet;

public interface DataSink {

	@Deprecated(forRemoval = true, since = "5.4")
	default void write(DataSource source, Path destination) throws IOException {
		OutputStream os = Files.newOutputStream(destination, 
				StandardOpenOption.CREATE, 
				StandardOpenOption.WRITE, 
				StandardOpenOption.TRUNCATE_EXISTING
			);
		write(source, os);
	}
	
	/**
	 * Writes the contents of the given {@link DataSource} to the destination
	 * {@link OutputStream} in this DataSink's format.
	 */
	void write(DataSource source, OutputStream destination) throws IOException;
		
	String getFormatExtension();

	/**
	 * Returns a name for this DataSource Plugin
	 */
	String getFormatName();

	/**
	 * Returns a description for this DataSource Plugin
	 */
	String getFormatDescription();

	Interaction getInteraction();

	void setInteraction(Interaction interaction);

	static ExecutorSet<Void> write(DataSource source, DataSink sink, OutputStream output) {
		
		CallbackInteraction interaction = new CallbackInteraction();
		
		return Plural.build("Writing Data Set", "Writing Scans", (execset, exec) -> {
			interaction.setCallbackAbortRequested(() -> execset.isAborted() || execset.isAbortRequested());
			interaction.setCallbackScansWritten(exec::workUnitCompleted);
			exec.setWorkUnits(source.getScanData().scanCount());
			
			try {
				sink.setInteraction(interaction);
				sink.write(source, output);
				if (interaction.isAbortedRequested()) {
					execset.aborted();
				}
			} catch (IOException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to export data", e);
			}
			
			return null;
		});
		
	}
	
}
