package peakaboo.controller.plotter.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.bolt.plugin.core.AlphaNumericComparitor;
import peakaboo.common.PeakabooLog;
import peakaboo.common.Version;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.settings.SavedSession;
import peakaboo.dataset.DatasetReadResult;
import peakaboo.dataset.DatasetReadResult.ReadStatus;
import peakaboo.datasource.model.DataSource;
import peakaboo.datasource.plugin.DataSourceLookup;
import peakaboo.datasource.plugin.DataSourcePlugin;
import peakaboo.datasource.plugin.DataSourcePluginManager;
import plural.executor.ExecutorSet;
import scitypes.util.StringInput;




public abstract class DataLoader {

	private PlotController controller;
	private List<Path> paths;

	//if we're loading a session, we need to do some extra work after loading the dataset
	private Runnable sessionCallback = () -> {}; 
	
	public DataLoader(PlotController controller, List<Path> paths) {
		this.controller = controller;
		this.paths = paths;
	}
	
	private void loadWithDataSource(DataSource dsp)
	{
		if (paths != null)
		{
			
			ExecutorSet<DatasetReadResult> reading = controller.data().TASK_readFileListAsDataset(paths, dsp, result -> {
					
				if (result == null || result.status == ReadStatus.FAILED)
				{
					if (result == null) {
						PeakabooLog.get().log(Level.SEVERE, "Error Opening Data", "Peakaboo could not open this dataset from " + dsp.getFileFormat().getFormatName());
					} else if (result.problem != null) {
						PeakabooLog.get().log(Level.SEVERE, "Error Opening Data: Peakaboo could not open this dataset from " + dsp.getFileFormat().getFormatName(), result.problem);
					} else {
						onFail(paths, "Peakaboo could not open this dataset.\n" + result.message);
					}
					
				} else {
					sessionCallback.run();
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
		
		List<DataSourcePlugin> candidates =  DataSourcePluginManager.SYSTEM.getPlugins().newInstances();
		List<DataSource> formats = DataSourceLookup.findDataSourcesForFiles(paths, candidates);
		
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
	
	private void prompt(DataSource dsp) {
		Optional<Group> parameters = dsp.getParameters(paths);
		if (parameters.isPresent()) {
			onParameters(parameters.get(), (accepted) -> {
				if (accepted) {
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
			SavedSession session = controller.readSavedSettings(StringInput.contents(file));
			
			
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
						sessionCallback = () -> {
							controller.loadSessionSettings(session);	
							warnVersion.run();
						};
						load();
					} else {
						//load the settings w/o the data, then set the file paths back to the current values
						controller.loadSessionSettings(session);
						//they said no, reset the stored paths to the old ones
						controller.data().setDataPaths(currentPaths);
						warnVersion.run();
					}
				});
				
								
			} else {
				//just load the session, as there is either no data associated with it, or it's the same data
				controller.loadSessionSettings(session);
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
	public abstract void onSelection(List<DataSource> datasources, Consumer<DataSource> selected);
	
	public abstract void onSessionNewer();
	public abstract void onSessionHasData(File sessionFile, Consumer<Boolean> load);
	
}
