package org.peakaboo.cli;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.data.DataLoader;
import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.plural.executor.ExecutorSet;

/**
 * A headless and non-interactive {@link DataLoader}. Any info needed must come
 * from a session or a default. Ambiguous file formats fail fast.
 *
 * @author NAS
 */
public class HeadlessDataLoader extends DataLoader {

	public static class HeadlessLoadException extends Exception {
		public HeadlessLoadException(String message) {
			super(message);
		}
	}

	// wait for callback from threaded reader
	private final Semaphore barrier = new Semaphore(0);
	// have we seen a failure message?
	private String failure = null;
	// why did the session (maybe) load without any data?
	private SessionOnlyReason sessionOnlyReason = null;

	public HeadlessDataLoader(PlotController controller) {
		super(controller);
	}

	/** Blocking. Open the given inputs with a session's settings. Ignores the session's data file list */
	public void openDataWithSession(List<DataInputAdapter> inputs, SavedSession session) throws HeadlessLoadException {
		DataLoaderContext ctx = new DataLoaderContext();
		ctx.datafiles = inputs;
		ctx.dataSource = session.data.datasource;
		ctx.sessionCallback = () -> controller.load(session, false);
		load(ctx);
		join();
	}

	/**
	 * Blocking. Open a session file along with the data the session lists. If
	 * the session doesn't list any real data, we still load session settings.
	 */
	public void openSession(File sessionFile) throws HeadlessLoadException {
		DataLoaderContext ctx = new DataLoaderContext();
		ctx.datafiles = DataInputAdapter.fromFilenames(List.of(sessionFile.getAbsolutePath()));
		load(ctx);
		join();
	}

	/** Hands back the observed reason if this session opened without any data */
	public Optional<SessionOnlyReason> sessionOnlyReason() {
		return Optional.ofNullable(sessionOnlyReason);
	}

	/** Blocks until the loader reports success or failure, then rethrows failures. */
	private void join() throws HeadlessLoadException {
		try {
			// Acquire will block until a callback (success/fail) releases it
			barrier.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new HeadlessLoadException("Interrupted while reading dataset");
		}
		// Hand failures back as exceptions
		if (failure != null) {
			throw new HeadlessLoadException(failure);
		}
	}

	private void fail(String message) {
		// Record the failure message and unblock the headless UI
		this.failure = message;
		barrier.release();
	}

	@Override
	public void onLoading(ExecutorSet<DatasetReadResult> job) {
		// Nothing to show without a UI -- maybe later?
	}

	@Override
	public void onSuccess(DataLoaderContext ctx) {
		// Unblock the headless thread
		barrier.release();
	}

	@Override
	public void onWarn(String message) {
		System.err.println("warning: " + message);
	}

	@Override
	public void onFail(DataLoaderContext ctx, String message) {
		// Loader messages are written for a multi-line display, so flatten them
		fail(message.strip().replaceAll("\\s*\\n\\s*", "; "));
	}

	@Override
	public void onParameters(Group parameters, Consumer<Boolean> finished) {
		// Customizing / overriding parameters not supported
		finished.accept(true);
	}

	@Override
	public void onSelection(List<DataSourcePlugin> datasources, Consumer<DataSourcePlugin> selected) {
		// We can't choose between data sources, the user must make it explicit.
		String candidates = datasources.stream()
				.map(ds -> ds.getFileFormat().getFormatName())
				.collect(Collectors.joining(", "));
		fail("More than one format matches these files: " + candidates);
	}

	@Override
	public void onSessionOnly(SessionOnlyReason reason) {
		// We observe a session is loading without data and record the reason for future reference
		this.sessionOnlyReason = reason;
		barrier.release();
	}

	@Override
	public void onSessionNewer() {
		System.err.println("Warning: session is from a newer version of Peakaboo; some settings may not load correctly");
	}

	@Override
	public void onSessionFailure() {
		fail("Session file could not be read; it may be corrupted or from too old a version of Peakaboo");
	}

	@Override
	public void onSessionHasData(File sessionFile, Consumer<Boolean> load) {
		controller.io().setFromSession(sessionFile);
		load.accept(true);
	}

}
