package org.peakaboo.datasink.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.datasink.model.components.interaction.CallbackInteraction;
import org.peakaboo.datasink.model.components.interaction.Interaction;
import org.peakaboo.datasource.model.DataSource;

import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;

public interface DataSink {

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

		final DummyExecutor writing = new DummyExecutor();
		writing.setWorkUnits(source.getScanData().scanCount());

		ExecutorSet<Void> writer = new ExecutorSet<Void>("Writing Data Set") {

			@Override
			protected Void execute() {

				interaction.setCallbackAbortRequested(() -> isAborted() || isAbortRequested());
				interaction.setCallbackScansWritten(i -> writing.workUnitCompleted(i));

				try {
					sink.setInteraction(interaction);
					writing.advanceState();
					sink.write(source, path);
					writing.advanceState();
					if (interaction.isAbortedRequested()) {
						aborted();
					}
				} catch (IOException e) {
					PeakabooLog.get().log(Level.SEVERE, "Failed to export data", e);
				}

				return null;
			}

		};
		writer.addExecutor(writing, "Writing Data Set");
		
		return writer;
	}

}
