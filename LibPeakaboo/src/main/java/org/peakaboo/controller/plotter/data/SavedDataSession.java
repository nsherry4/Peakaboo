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
	
	public SavedDataSession storeFrom(DataController controller) {
		this.discards = controller.getDiscards().list();
		this.files = controller.getDataPaths().stream().map(p -> p.toString()).collect(Collectors.toList());
		this.dataSourcePluginUUID = controller.getDataSourcePluginUUID();
		return this;
	}
	
	public void loadInto(DataController controller) {
		controller.getDiscards().clear();
		for (int i : discards) {
			controller.getDiscards().discard(i);
		}
		controller.setDataSourcePluginUUID(this.dataSourcePluginUUID);
		controller.setDataPaths(this.filesAsDataPaths());
	}
	
	public List<Path> filesAsDataPaths() {
		return this.files.stream().map(Paths::get).collect(Collectors.toList());
	}
	
}
