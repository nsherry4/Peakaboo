package org.peakaboo.dataset.sink.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.dataset.io.DataOutputAdapter;
import org.peakaboo.dataset.sink.model.components.interaction.CallbackInteraction;
import org.peakaboo.dataset.sink.model.components.interaction.Interaction;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.plural.executor.ExecutorSet;

public interface DataSink {

	public class DataSinkWriteException extends Exception {
		
		public DataSinkWriteException(String message) {
			super(message);
		}
		
		public DataSinkWriteException(String message, Throwable cause) {
			super(message, cause);
		}
	}
	

	/**
	 * Writes the contents of the given {@link DataSource} to the destination
	 * {@link OutputStream} in this DataSink's format.
	 */
	void write(DataSource source, DataOutputAdapter destination) throws IOException, DataSinkWriteException;
		
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

	static ExecutorSet<Void> write(DataSource source, DataSink sink, DataOutputAdapter output) {
		
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
			} catch (IOException | DataSinkWriteException e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to export data", e);
			}
			
			return null;
		});
		
	}
	
}
