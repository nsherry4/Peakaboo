package org.peakaboo.datasink.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasink.model.components.interaction.CallbackInteraction;
import org.peakaboo.datasink.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.DataSource;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.executor.ExecutorSet;

public interface DataSink {

	//TODO: How should this work w/ DataFile being used in DataSource?
	void write(DataSource source, Path destination) throws IOException;

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

	static ExecutorSet<Void> write(DataSource source, DataSink sink, Path path) {
		
		CallbackInteraction interaction = new CallbackInteraction();
		
		return Plural.build("Writing Data Set", "Writing Scans", (execset, exec) -> {
			interaction.setCallbackAbortRequested(() -> execset.isAborted() || execset.isAbortRequested());
			interaction.setCallbackScansWritten(i -> exec.workUnitCompleted(i));
			exec.setWorkUnits(source.getScanData().scanCount());
			
			try {
				sink.setInteraction(interaction);
				sink.write(source, path);
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
