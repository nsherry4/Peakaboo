package org.peakaboo.controller.plotter.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
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
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.bolt.plugin.core.AlphaNumericComparitor;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.cyclops.util.StringInput;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;
import org.peakaboo.framework.plural.executor.ExecutorSet;




public abstract class DataLoader {

	protected PlotController controller;
	private List<DataInputAdapter> datafiles;
	private SavedPlugin dataSource = null;
	private File sessionFile = null;
	
	//if we're loading a session, we need to do some extra work after loading the dataset
	private Runnable sessionCallback = () -> {}; 
	
	public DataLoader(PlotController controller, List<DataInputAdapter> datafiles) {
		this.controller = controller;
		this.datafiles = datafiles;
	}
	
	private void loadWithDataSource(DataSourcePlugin dsp) {
		
		if (datafiles != null) {
			
			ExecutorSet<DatasetReadResult> reading = controller.data().asyncReadFileListAsDataset(datafiles, dsp, result -> {
					
				if (result == null || result.status == ReadStatus.FAILED) {
					onDataSourceLoadFailure(dsp, result);					
				} else {
					onDataSourceLoadSuccess();
				}						
							
			});

			onLoading(reading);
			reading.startWorking();

		}
	}
	
	private void onDataSourceLoadSuccess() {
		sessionCallback.run();
		controller.data().setDataSourcePlugin(dataSource);
		controller.data().setDataPaths(datafiles);
		
		//Try and set the last-used folder for local UIs to refer to
		//First, check the first data file for its folder
		Optional<File> localDir = datafiles.get(0).localFolder();
		if (localDir.isPresent()) {
			controller.io().setLastFolder(localDir.get());
		} else if (sessionFile != null) {
			controller.io().setLastFolder(sessionFile.getParentFile());
		}
		onSuccess(datafiles, sessionFile);
	}

	private void onDataSourceLoadFailure(DataSourcePlugin dsp, DatasetReadResult result) {
		String message = "\nSource: " + dsp.getFileFormat().getFormatName();
		
		if (result != null && result.message != null) {
			message += "\nMessage: " + result.message;
		}
		if (result != null && result.problem != null) {
			message += "\nProblem: " + result.problem;
		}
		
		if (result != null) {
			PeakabooLog.get().log(Level.WARNING, "Error Opening Data", new RuntimeException(result.message, result.problem));
		} else {
			PeakabooLog.get().log(Level.WARNING, "Error Opening Data", new RuntimeException("Dataset Read Result was null"));
		}
		onFail(datafiles, message);
	}
	
	

	public void load() {
		if (datafiles.isEmpty()) {
			return;
		}
		
		//check if it's a peakaboo session file first
		if (
				datafiles.size() == 1 && 
				datafiles.get(0).addressable() && 
				datafiles.get(0).getFilename().toLowerCase().endsWith(".peakaboo")
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
		if (dataSource != null) {
			var plugin = DataSourceRegistry.system().fromSaved(dataSource);
			if (plugin.isPresent()) {
				formats.add(plugin.get());
			} else {
				onWarn("Could not find data source plugin requested by saved session.");
			}
		}
		if (formats.isEmpty()) {
			List<DataSourcePlugin> candidates =  DataSourceRegistry.system().newInstances();
			formats = DataSourceLookup.findDataSourcesForFiles(datafiles, candidates);
		}
		
		if (formats.size() > 1) {
			onSelection(formats, this::prompt);
		} else if (formats.isEmpty()) {
			onFail(datafiles, "Could not determine the data format of the selected file(s)");
		} else {
			prompt(formats.get(0));
		}
		
	}
	
	private void prompt(DataSourcePlugin dsp) {
		
		Optional<Group> parameters = Optional.empty();
		try {
			parameters = dsp.getParameters(datafiles);
		} catch (DataSourceReadException | IOException e1) {
			onFail(datafiles, "Data Source failed while loading paramters");
			return;
		}
		
		if (parameters.isPresent()) {
			Group dsGroup = parameters.get();
			this.dataSource = new SavedPlugin(dsp);
			
			/*
			 * if we've alredy loaded a set of parameters from a session we're opening then
			 * we transfer those values into the values for the data source's Parameters
			 */
			if (dataSource.settings != null) {
				try {
					dsGroup.deserialize(dataSource.settings);
				} catch (RuntimeException e) {
					PeakabooLog.get().log(Level.WARNING, "Failed to load saved Data Source parameters", e);
				}
			}
			
			onParameters(dsGroup, accepted -> {
				if (accepted) {
					//user accepted, save a copy of the new parameters
					this.dataSource = new SavedPlugin(this.dataSource, dsGroup.serialize());
					loadWithDataSource(dsp);
				}
			});
		} else {
			this.dataSource = new SavedPlugin(dsp);
			loadWithDataSource(dsp);
		}
	}
	
	
	/**
	 * Attempt to find and load a saved Peakaboo session from the data files that
	 * are being loaded. This method will attempt to load either a v1 or v2 session.
	 */
	private void loadSession() {
		try {
			//We don't want users saving a session loaded from /tmp
			if (!datafiles.get(0).writable()) {		
				//TODO: is writable the right thing to ask here? local vs non-local maybe?
				//TODO: maybe in later versions, the UI can inspect this when determining if it can save instead of save-as
				throw new IOException("Cannot load session from read-only source");
			}
			sessionFile = datafiles.get(0).getAndEnsurePath().toFile();
			
			String contents = StringInput.contents(sessionFile);
			if (DruthersSerializer.hasFormat(contents)) {
				DruthersSerializer.deserialize(contents, false,
					new DruthersSerializer.FormatLoader<>(
							SavedSession.FORMAT, 
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
			PeakabooLog.get().log(Level.SEVERE, "Failed to load session", e);
		}
	}
	
	
	private void loadV2Session(SavedSession session) {

		
		//chech if the session is from a newer version of Peakaboo, and warn if it is
		Runnable warnVersion = () -> {
			if (AlphaNumericComparitor.compareVersions(Version.longVersionNo, session.app.version) < 0) {
				onSessionNewer();
			}
		};
		
		List<DataInputAdapter> currentPaths = controller.data().getDataPaths();
		List<DataInputAdapter> sessionPaths = DataInputAdapter.fromFilenames(session.data.files);
		
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
			
			onSessionHasData(sessionFile, load -> {
				if (load) {
					//they said yes, load the new data, and then apply the session
					//this needs to be done this way b/c loading a new dataset wipes out
					//things like calibration info
					this.datafiles = sessionPaths;
					this.dataSource = session.data.datasource;
					sessionCallback = () -> {
						controller.load(session, true);
						warnVersion.run();
					};
					load();
				} else {
					//load the settings w/o the data, then set the file paths back to the current values
					controller.load(session, true);
					//they said no, reset the stored paths to the old ones
					controller.data().setDataPaths(currentPaths);
					warnVersion.run();
				}
				controller.io().setSessionFile(sessionFile);
				RecentSessions.SYSTEM.addSessionFile(sessionFile);
			});
			
							
		} else {
			//just load the session, as there is either no data associated with it, or it's the same data
			controller.load(session, true);
			warnVersion.run();
			controller.io().setSessionFile(sessionFile);
			RecentSessions.SYSTEM.addSessionFile(sessionFile);
		}
		
	}
	
	@Deprecated(since = "6", forRemoval = true)
	private void loadV1Session(SavedSessionV1 session) {
		

		
		//chech if the session is from a newer version of Peakaboo, and warn if it is
		Runnable warnVersion = () -> {
			if (AlphaNumericComparitor.compareVersions(Version.longVersionNo, session.version) < 0) {
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
			
			onSessionHasData(sessionFile, load -> {
				if (load) {
					//they said yes, load the new data, and then apply the session
					//this needs to be done this way b/c loading a new dataset wipes out
					//things like calibration info
					this.datafiles = sessionPaths;
					this.dataSource = new SavedPlugin(session.data.dataSourcePluginUUID, "Data Source", "", session.data.dataSourceParameters);
					sessionCallback = () -> {
						controller.loadSessionSettingsV1(session, true);	
						warnVersion.run();
					};
					load();
				} else {
					//load the settings w/o the data, then set the file paths back to the current values
					controller.loadSessionSettingsV1(session, true);
					//they said no, reset the stored paths to the old ones
					controller.data().setDataPaths(currentPaths);
					warnVersion.run();
				}
				controller.io().setSessionFile(sessionFile);
				RecentSessions.SYSTEM.addSessionFile(sessionFile);
			});
			
							
		} else {
			//just load the session, as there is either no data associated with it, or it's the same data
			controller.loadSessionSettingsV1(session, true);
			warnVersion.run();
			controller.io().setSessionFile(sessionFile);
			RecentSessions.SYSTEM.addSessionFile(sessionFile);
		}
		
	}

	public abstract void onLoading(ExecutorSet<DatasetReadResult> job);
	public abstract void onSuccess(List<DataInputAdapter> paths, File session);
	public abstract void onWarn(String message);
	public abstract void onFail(List<DataInputAdapter> paths, String message);
	public abstract void onParameters(Group parameters, Consumer<Boolean> finished);
	public abstract void onSelection(List<DataSourcePlugin> datasources, Consumer<DataSourcePlugin> selected);
	
	public abstract void onSessionNewer();
	public abstract void onSessionFailure();
	public abstract void onSessionHasData(File sessionFile, Consumer<Boolean> load);
	
}
