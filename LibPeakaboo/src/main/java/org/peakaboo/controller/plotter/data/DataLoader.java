package org.peakaboo.controller.plotter.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.common.Version;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.SavedSession;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.dataset.DatasetReadResult.ReadStatus;
import org.peakaboo.datasource.plugin.DataSourceLookup;
import org.peakaboo.datasource.plugin.DataSourcePlugin;
import org.peakaboo.datasource.plugin.DataSourcePluginManager;
import org.peakaboo.framework.autodialog.model.Group;

import cyclops.util.StringInput;
import net.sciencestudio.bolt.plugin.core.AlphaNumericComparitor;
import plural.executor.ExecutorSet;




public abstract class DataLoader {

	private PlotController controller;
	private List<Path> paths;
	private String dataSourceUUID = null;
	private List<Object> sessionParameters = null;
	
	//if we're loading a session, we need to do some extra work after loading the dataset
	private Runnable sessionCallback = () -> {}; 
	
	public DataLoader(PlotController controller, List<Path> paths) {
		this.controller = controller;
		this.paths = paths;
	}
	
	private void loadWithDataSource(DataSourcePlugin dsp)
	{
		if (paths != null)
		{
			
			ExecutorSet<DatasetReadResult> reading = controller.data().TASK_readFileListAsDataset(paths, dsp, result -> {
					
				if (result == null || result.status == ReadStatus.FAILED)
				{
					String message = "Peakaboo could not open this dataset";
					message = "\nSource: " + dsp.getFileFormat().getFormatName();
					
					if (result != null && result.message != null) {
						message += "\nMessage: " + result.message;
					}
					if (result != null && result.problem != null) {
						message += "\nProblem: " + result.problem;
					}
					
					PeakabooLog.get().log(Level.WARNING, "Error Opening Data", new RuntimeException(result.message, result.problem));
					onFail(paths, message);
					
				} else {
					sessionCallback.run();
					controller.data().setDataSourceParameters(sessionParameters);
					onSuccess(paths);
				}						
							
			});

			onLoading(reading);
			reading.startWorking();

		}
	}
	
	

	public void load() {
		if (paths.size() == 0) {
			return;
		}

		//check if it's a peakaboo session file first
		if (paths.size() == 1 && paths.get(0).toString().toLowerCase().endsWith(".peakaboo")) {
			loadSession();
			return;
		}
		
		/*
		 * look up the data source to use to open this data with we should prefer a
		 * plugin specified by uuid (eg from a reloaded session). If there is no plugin
		 * specified, we look up all formats
		 */
		List<DataSourcePlugin> formats = new ArrayList<>();
		if (dataSourceUUID != null) {
			formats.add(DataSourcePluginManager.SYSTEM.getByUUID(dataSourceUUID).create());
		}
		if (formats.size() == 0) {
			List<DataSourcePlugin> candidates =  DataSourcePluginManager.SYSTEM.newInstances();
			formats = DataSourceLookup.findDataSourcesForFiles(paths, candidates);
		}
		
		if (formats.size() > 1)
		{
			onSelection(formats, datasource -> {
				prompt(datasource);
			});
		}
		else if (formats.size() == 0)
		{
			onFail(paths, "Could not determine the data format of the selected file(s)");
		}
		else
		{
			prompt(formats.get(0));
		}
		
	}
	
	private void prompt(DataSourcePlugin dsp) {
		Optional<Group> parameters = dsp.getParameters(paths);
		
		if (parameters.isPresent()) {
			Group dsGroup = parameters.get();
			
			/*
			 * if we've alredy loaded a set of parameters from a session we're opening then
			 * we transfer those values into the values for the data source's Parameters
			 */
			if (sessionParameters != null) {
				try {
					dsGroup.deserialize(sessionParameters);
				} catch (RuntimeException e) {
					PeakabooLog.get().log(Level.WARNING, "Failed to load saved Data Source parameters", e);
				}
			}
			
			onParameters(dsGroup, (accepted) -> {
				if (accepted) {
					//user accepted, save a copy of the new parameters
					sessionParameters = dsGroup.serialize();
					loadWithDataSource(dsp);
				}
			});
		} else {
			loadWithDataSource(dsp);
		}
	}
	
	

	private void loadSession() {
		
		File file = paths.get(0).toFile();
		try {
			Optional<SavedSession> optSession = controller.readSavedSettings(StringInput.contents(file));
			
			if (!optSession.isPresent()) {
				onSessionFailure();
				return;
			}
			
			SavedSession session = optSession.get();
			
			
			//chech if the session is from a newer version of Peakaboo, and warn if it is
			Runnable warnVersion = () -> {
				if (AlphaNumericComparitor.compareVersions(Version.longVersionNo, session.version) > 0) {
					onSessionNewer();
				}
			};
			
			List<Path> currentPaths = controller.data().getDataPaths();
			List<Path> sessionPaths = session.data.filesAsDataPaths();
			
			boolean sessionPathsExist = sessionPaths.stream().map(Files::exists).reduce(true, (a, b) -> a && b);
			
			//If the data files in the saved session are different, offer to load the data set from the new session
			if (sessionPathsExist && sessionPaths.size() > 0 && !sessionPaths.equals(currentPaths)) {
				
				onSessionHasData(file, load -> {
					if (load) {
						//they said yes, load the new data, and then apply the session
						//this needs to be done this way b/c loading a new dataset wipes out
						//things like calibration info
						this.paths = sessionPaths;
						this.dataSourceUUID = session.data.dataSourcePluginUUID;
						this.sessionParameters = session.data.dataSourceParameters;
						sessionCallback = () -> {
							controller.loadSessionSettings(session, true);	
							warnVersion.run();
						};
						load();
					} else {
						//load the settings w/o the data, then set the file paths back to the current values
						controller.loadSessionSettings(session, true);
						//they said no, reset the stored paths to the old ones
						controller.data().setDataPaths(currentPaths);
						warnVersion.run();
					}
				});
				
								
			} else {
				//just load the session, as there is either no data associated with it, or it's the same data
				controller.loadSessionSettings(session, true);
				warnVersion.run();
			}
			

			
		} catch (IOException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to load session", e);
		}
	}

	public abstract void onLoading(ExecutorSet<DatasetReadResult> job);
	public abstract void onSuccess(List<Path> paths);
	public abstract void onFail(List<Path> paths, String message);
	public abstract void onParameters(Group parameters, Consumer<Boolean> finished);
	public abstract void onSelection(List<DataSourcePlugin> datasources, Consumer<DataSourcePlugin> selected);
	
	public abstract void onSessionNewer();
	public abstract void onSessionFailure();
	public abstract void onSessionHasData(File sessionFile, Consumer<Boolean> load);
	
}
