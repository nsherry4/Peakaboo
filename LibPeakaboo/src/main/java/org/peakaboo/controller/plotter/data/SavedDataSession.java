package org.peakaboo.controller.plotter.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.datasource.model.datafile.DataFile;
import org.peakaboo.datasource.model.datafile.DataFiles;

public class SavedDataSession {

	public List<Integer> discards = new ArrayList<>();
	public List<String> files = new ArrayList<>();
	public String dataSourcePluginUUID = null;
	public List<Object> dataSourceParameters = null;
	public String title = null;
	
	public SavedDataSession storeFrom(DataController controller) {
		this.discards = controller.getDiscards().list();
		if (controller.getDataPaths().size() > 0 && controller.getDataPaths().get(0).addressable()) {
			this.files = controller.getDataPaths().stream().map(p -> p.address().get()).collect(Collectors.toList());	
		}
		this.dataSourcePluginUUID = controller.getDataSourcePluginUUID();
		this.dataSourceParameters = controller.getDataSourceParameters();
		this.title = controller.title;
		return this;
	}
	
	public void loadInto(DataController controller) {
		controller.getDiscards().clear();
		for (int i : discards) {
			controller.getDiscards().discard(i);
		}
		controller.setDataSourcePluginUUID(this.dataSourcePluginUUID);
		controller.setDataPaths(this.filesAsDataPaths());
		controller.setTitle(this.title);

		/*
		 * We don't load the datasource paramters here, since they're used while opening
		 * the actual data source, and may be altered by the user. We don't want to go
		 * clobbering the user's updated parameters. We store the value, and it is read
		 * by the DataLoader directly when opeing associated data. If no data is opened,
		 * this value is of no use to us.
		 */
		//controller.setDataSourceParameters(dataSourceParameters);
	}
	
	public List<DataFile> filesAsDataPaths() {
		//download dir not used by all DataFile impls, but required by some
		Path dldir = DataFiles.createDownloadDirectory();
		return this.files.stream().map(s -> DataFiles.construct(s, dldir)).collect(Collectors.toList());
	}
	
}
