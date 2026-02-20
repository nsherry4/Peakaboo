package org.peakaboo.controller.plotter.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.peakaboo.app.RecentSessions;
import org.peakaboo.app.Version;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SavedSessionV1;
import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.dataset.DatasetReadResult.ReadStatus;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.model.DataSourceReadException;
import org.peakaboo.dataset.source.plugin.DataSourceLookup;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.accent.AlphaNumericComparitor;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;
import org.peakaboo.framework.plural.executor.ExecutorSet;



public abstract class DataLoader {

	public static class DataLoaderContext {
		public List<DataInputAdapter> datafiles;
		public SavedPlugin dataSource;
		public File sessionFile;
		public Runnable sessionCallback = () -> {}; 
	};
	
	private static final String ERR_EMPTY_CTX = "Pending DataLoader Context cannot be empty";
	
	protected PlotController controller;
	
	// Track information about the data set that we're in the process of opening
	protected Optional<DataLoaderContext> pending = Optional.empty();

	
	protected DataLoader(PlotController controller) {
		this.controller = controller;
	}
	
	/**
	 * Load a new dataset from a list of files/inputs
	 * 
	 * <br/><br/>
	 * Previous Step: None<br/>
	 * Next Step: {@link DataLoader#load(DataLoaderContext)}<br/>
	 */
	public void loadFiles(List<DataInputAdapter> inputs) {
		DataLoaderContext ctx = new DataLoaderContext();
		ctx.datafiles = inputs;
		this.load(ctx);
	}

	
	

	/**
	 * Load a new session / dataset from a DataLoaderContext
	 * 
	 * <br/><br/>
	 * Previous Step: {@link DataLoader#loadFiles(List)}<br/>
	 * Next Step: {@link DataLoader#promptCallback(DataSourcePlugin)} or<br/>
	 * Next Step: {@link DataLoader#loadSession()}<br/>
	 */
	protected void load(DataLoaderContext ctx) {

		// Set this context as the one we're currently trying to load
		this.pending = Optional.of(ctx);
		
		//check if it's a peakaboo session file first
		if (
				ctx.datafiles.size() == 1 && 
				ctx.datafiles.get(0).addressable() && 
				ctx.datafiles.get(0).getFilename().toLowerCase().endsWith(".peakaboo")
			) 
		{
			loadSession();
			return;
		}
		
		/*
		 * look up the data source to use to open this data with. If there is no plugin
		 * specified, we look up all formats
		 */
		List<DataSourcePlugin> formats = new ArrayList<>();
		if (ctx.dataSource != null) {
			var plugin = DataSourceRegistry.system().fromSaved(ctx.dataSource);
			if (plugin.isPresent()) {
				formats.add(plugin.get());
			} else {
				onWarn("Could not find data source plugin requested by saved session.");
			}
		}
		if (formats.isEmpty()) {
			List<DataSourcePlugin> candidates =  DataSourceRegistry.system().newInstances();
			formats = DataSourceLookup.findDataSourcesForFiles(ctx.datafiles, candidates);
		}
		
		if (formats.size() > 1) {
			onSelection(formats, this::promptCallback);
		} else if (formats.isEmpty()) {
			onFail(ctx, "Could not determine the data format of the selected file(s)");
		} else {
			promptCallback(formats.get(0));
		}
		
	}
	
	
	/**
	 * If user input is required for loading a dataset, this function will be called
	 * once that input has been provided.
	 * 
	 * <br/><br/>
	 * Previous Step: {@link DataLoader#load(DataLoaderContext)}<br/>
	 * Next Step: {@link DataLoader#openDataSource(DataSourcePlugin)}<br/>
	 */
	private void promptCallback(DataSourcePlugin dsp) {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException(ERR_EMPTY_CTX);
		}
		var ctx = pending.get();
		
		Optional<Group> parameters = Optional.empty();
		try {
			parameters = dsp.getParameters(ctx.datafiles);
		} catch (DataSourceReadException | IOException e1) {
			onFail(ctx, "Data Source failed while loading paramters");
			return;
		}
		
		if (parameters.isPresent()) {
			Group dsGroup = parameters.get();
			ctx.dataSource = new SavedPlugin(dsp);
			
			/*
			 * if we've alredy loaded a set of parameters from a session we're opening then
			 * we transfer those values into the values for the data source's Parameters
			 */
			if (ctx.dataSource.settings != null) {
				try {
					dsGroup.deserialize(ctx.dataSource.settings);
				} catch (RuntimeException e) {
					OneLog.log(Level.WARNING, "Failed to load saved Data Source parameters", e);
				}
			}
			
			onParameters(dsGroup, accepted -> {
				if (accepted) {
					//user accepted, save a copy of the new parameters
					ctx.dataSource = new SavedPlugin(ctx.dataSource, dsGroup.serialize());
					openDataSource(dsp);
				}
			});
		} else {
			ctx.dataSource = new SavedPlugin(dsp);
			openDataSource(dsp);
		}
	}
	
	

	/**
	 * Load a new dataset from a DataSource. This method should only be called once
	 * all necessary work to configure the datasource has been completed
	 * 
	 * <br/><br/>
	 * Previous Step: {@link DataLoader#promptCallback(DataSourcePlugin)}<br/>
	 * Next Step: {@link DataLoader#onLoadFailure(DataSourcePlugin, DatasetReadResult)} or<br/>
	 * Next Step: {@link DataLoader#onLoadCancelled()} or<br/>
	 * Next Step: {@link DataLoader#onLoadSuccess()}<br/>
	 */
	private void openDataSource(DataSourcePlugin dsp) {
		
		if (pending.isPresent() && pending.get().datafiles != null) {
			
			var ctx = this.pending.get();
			
			ExecutorSet<DatasetReadResult> reading = controller.data().asyncReadFileListAsDataset(ctx.datafiles, dsp, result -> {
					
				if (result == null || result.status == ReadStatus.FAILED) {
					onLoadFailure(dsp, result);					
				} else if (result.status == ReadStatus.CANCELLED) {
					onLoadCancelled();
				} else {
					onLoadSuccess();
				}		
							
			});

			onLoading(reading);
			reading.startWorking();

		}
	}
	
	/**
	 * Attempt to find and load a saved Peakaboo session from the data files that
	 * are being loaded. This method will attempt to load either a v1 or v2 session.
	 * 
	 * <br/><br/>
	 * Previous Step: {@link DataLoader#load(DataLoaderContext)}<br/>
	 * Next Step: {@link DataLoader#loadV1Session(SavedSessionV1)} or<br/>
	 * Next Step: {@link DataLoader#loadV2Session(SavedSession)} or<br/>
	 * Next Step: {@link DataLoader#onSessionFailure()}<br/>
	 */
	private void loadSession() {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException(ERR_EMPTY_CTX);
		}
		var ctx = pending.get();
		
		try {
			
			List<DataInputAdapter> datafiles = ctx.datafiles;
			
			//We don't want users saving a session loaded from /tmp
			if (!datafiles.get(0).writable()) {		
				//TODO: is writable the right thing to ask here? local vs non-local maybe?
				//TODO: maybe in later versions, the UI can inspect this when determining if it can save instead of save-as
				throw new IOException("Cannot load session from read-only source");
			}
			
			// Get the File for our session and update our context with it
			File sessionFile = datafiles.get(0).getAndEnsurePath().toFile();
			ctx.sessionFile = sessionFile;
			
			
			String contents = FileUtils.readFileToString(sessionFile, StandardCharsets.UTF_8);
			if (DruthersSerializer.hasFormat(contents)) {
				DruthersSerializer.deserialize(contents, false,
					new DruthersSerializer.FormatLoader<>(
							SavedSession.SESSION_FORMAT, 
							SavedSession.class, 
							this::loadV2Session
						)
				);
				
			} else {
				Optional<SavedSessionV1> optSession = PlotController.readSavedSettings(contents);
				// If we failed to load the session
				if (!optSession.isPresent()) {
					onSessionFailure();
					return;
				}
				loadV1Session(optSession.get());
				
			}

			
		} catch (IOException e) {
			OneLog.log(Level.SEVERE, "Failed to load session", e);
		} catch (IllegalArgumentException e) {
			OneLog.log(Level.SEVERE, "Session contained unrecognised file path", e);
			onSessionFailure();
		}
	}
	
	
	private void loadV2Session(SavedSession session) {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException(ERR_EMPTY_CTX);
		}
		var ctx = pending.get();
		
		//check if the session is from a newer version of Peakaboo, and warn if it is
		Runnable warnVersion = () -> {
			if (AlphaNumericComparitor.compareVersions(Version.LONG_VERSION, session.app.version) < 0) {
				onSessionNewer();
			}
		};
		
		List<DataInputAdapter> currentPaths = controller.data().getDataPaths();
		List<DataInputAdapter> sessionPaths = DataInputAdapter.fromFilenames(session.data.files);
		
		//Verify all paths exist
		boolean sessionPathsExist = true;
		for (DataInputAdapter d : sessionPaths) {
			sessionPathsExist &= d.exists();
		}
		
		//If the data files in the saved session are different, offer to load the data set from the new session
		if (sessionPathsExist && !sessionPaths.isEmpty() && !sessionPaths.equals(currentPaths)) {
			
			onSessionHasData(ctx.sessionFile, load -> {
				if (load) {
					//they said yes, load the new data, and then apply the session
					//this needs to be done this way b/c loading a new dataset wipes out
					//things like calibration info
					ctx.datafiles = sessionPaths;
					ctx.dataSource = session.data.datasource;
					ctx.sessionCallback = () -> {
						controller.load(session, true);
						warnVersion.run();
					};
					load(ctx);
				} else {
					//load the settings w/o the data, then set the file paths back to the current values
					controller.load(session, true);
					//they said no, reset the stored paths to the old ones
					controller.data().setDataPaths(currentPaths);
					warnVersion.run();
				}
				controller.io().setSessionFile(ctx.sessionFile);
			});
			
							
		} else {
			//just load the session, as there is either no data associated with it, or it's the same data
			controller.load(session, true);
			warnVersion.run();
			controller.io().setSessionFile(ctx.sessionFile);
			RecentSessions.SYSTEM.addSessionFile(ctx.sessionFile);
		}
		
	}
	
	@Deprecated(since = "6", forRemoval = true)
	private void loadV1Session(SavedSessionV1 session) {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException(ERR_EMPTY_CTX);
		}
		var ctx = pending.get();

		
		//check if the session is from a newer version of Peakaboo, and warn if it is
		Runnable warnVersion = () -> {
			if (AlphaNumericComparitor.compareVersions(Version.LONG_VERSION, session.version) < 0) {
				onSessionNewer();
			}
		};
		
		List<DataInputAdapter> currentPaths = controller.data().getDataPaths();
		List<DataInputAdapter> sessionPaths = session.data.filesAsDataPaths();

		//Verify all paths exist
		boolean sessionPathsExist = true;
		for (DataInputAdapter d : sessionPaths) {
			if (d == null) {
				sessionPathsExist = false;
				break;
			}
			sessionPathsExist &= d.exists();
		}
		
		//If the data files in the saved session are different, offer to load the data set from the new session
		if (sessionPathsExist && !sessionPaths.isEmpty() && !sessionPaths.equals(currentPaths)) {
			
			onSessionHasData(ctx.sessionFile, load -> {
				if (load) {
					//they said yes, load the new data, and then apply the session
					//this needs to be done this way b/c loading a new dataset wipes out
					//things like calibration info
					ctx.datafiles = sessionPaths;
					ctx.dataSource = new SavedPlugin(session.data.dataSourcePluginUUID, "Data Source", "", session.data.dataSourceParameters);
					ctx.sessionCallback = () -> {
						controller.loadSessionSettingsV1(session, true);	
						warnVersion.run();
					};
					load(ctx);
				} else {
					//load the settings w/o the data, then set the file paths back to the current values
					controller.loadSessionSettingsV1(session, true);
					//they said no, reset the stored paths to the old ones
					controller.data().setDataPaths(currentPaths);
					warnVersion.run();
				}
				controller.io().setSessionFile(ctx.sessionFile);
				RecentSessions.SYSTEM.addSessionFile(ctx.sessionFile);
			});
			
							
		} else {
			//just load the session, as there is either no data associated with it, or it's the same data
			controller.loadSessionSettingsV1(session, true);
			warnVersion.run();
			controller.io().setSessionFile(ctx.sessionFile);
			RecentSessions.SYSTEM.addSessionFile(ctx.sessionFile);
		}
		
	}

	

	
	private void onLoadSuccess() {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException(ERR_EMPTY_CTX);
		}		
		var ctx = this.pending.get();
 		
		// Now that we've successfully loaded the data set, add the session file (if
		// there is one) to the list of recently opened sessions
		if (ctx.sessionFile != null) {
			RecentSessions.SYSTEM.addSessionFile(ctx.sessionFile);
		}
		
		List<DataInputAdapter> datafiles = ctx.datafiles;
		File sessionFile = ctx.sessionFile;
		
		ctx.sessionCallback.run();
		controller.data().setDataSourcePlugin(ctx.dataSource);
		controller.data().setDataPaths(datafiles);
		
		//Try and set the last-used folder for local UIs to refer to
		//First, check the first data file for its folder
		Optional<File> localDir = datafiles.get(0).localFolder();
		if (localDir.isPresent()) {
			controller.io().setLastFolder(localDir.get());
		} else if (sessionFile != null) {
			controller.io().setLastFolder(sessionFile.getParentFile());
		}
		
		//We've successfully loaded the pending context, swap it with the loaded context
		this.pending = Optional.empty();
		
		onSuccess(ctx);
	}
	
	private void onLoadCancelled() {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException("DataLoader Context cannot be empty");
		}

		this.pending = Optional.empty();
	}

	private void onLoadFailure(DataSourcePlugin dsp, DatasetReadResult result) {
		
		if (this.pending.isEmpty()) {
			throw new RuntimeException(ERR_EMPTY_CTX);
		}
		var ctx = pending.get();
				
		String message = "\nSource: " + dsp.getFileFormat().getFormatName();
		
		if (result != null && result.message != null) {
			message += "\nMessage: " + result.message;
		}
		if (result != null && result.problem != null) {
			message += "\nProblem: " + result.problem;
		}
		
		if (result != null) {
			OneLog.log(Level.WARNING, "Error Opening Data", new RuntimeException(result.message, result.problem));
		} else {
			OneLog.log(Level.WARNING, "Error Opening Data", new RuntimeException("Dataset Read Result was null"));
		}
		onFail(ctx, message);
		
		this.pending = Optional.empty();
	}
	
	
	public abstract void onLoading(ExecutorSet<DatasetReadResult> job);
	public abstract void onSuccess(DataLoaderContext ctx);
	public abstract void onWarn(String message);
	public abstract void onFail(DataLoaderContext ctx, String message);
	public abstract void onParameters(Group parameters, Consumer<Boolean> finished);
	public abstract void onSelection(List<DataSourcePlugin> datasources, Consumer<DataSourcePlugin> selected);
	
	public abstract void onSessionNewer();
	public abstract void onSessionFailure();
	public abstract void onSessionHasData(File sessionFile, Consumer<Boolean> load);
	
}
