package org.peakaboo.controller.plotter.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SavedDataSession {

	public List<Integer> discards = new ArrayList<>();
	public List<String> files = new ArrayList<>();
	public String dataSourcePluginUUID = null;
	public List<Object> dataSourceParameters = null;
	
	public SavedDataSession storeFrom(DataController controller) {
		this.discards = controller.getDiscards().list();
		this.files = controller.getDataPaths().stream().map(p -> p.toString()).collect(Collectors.toList());
		this.dataSourcePluginUUID = controller.getDataSourcePluginUUID();
		this.dataSourceParameters = controller.getDataSourceParameters();
		return this;
	}
	
	public void loadInto(DataController controller) {
		controller.getDiscards().clear();
		for (int i : discards) {
			controller.getDiscards().discard(i);
		}
		controller.setDataSourcePluginUUID(this.dataSourcePluginUUID);
		controller.setDataPaths(this.filesAsDataPaths());

		/*
		 * We don't load the datasource paramters here, since they're used while opening
		 * the actual data source, and may be altered by the user. We don't want to go
		 * clobbering the user's updated parameters. We store the value, and it is read
		 * by the DataLoader directly when opeing associated data. If no data is opened,
		 * this value is of no use to us.
		 */
		//controller.setDataSourceParameters(dataSourceParameters);
	}
	
	public List<Path> filesAsDataPaths() {
		return this.files.stream().map(Paths::get).collect(Collectors.toList());
	}
	
}
