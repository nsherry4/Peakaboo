package org.peakaboo.controller.plotter.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.dataset.source.model.datafile.DataFile;
import org.peakaboo.dataset.source.model.datafile.DataFiles;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;

@Deprecated(since = "6", forRemoval = true)
public class SavedDataSessionV1 {

	public List<Integer> discards = new ArrayList<>();
	public List<String> files = new ArrayList<>();
	public String dataSourcePluginUUID = null;
	public Map<String, Object> dataSourceParameters = null;
	public String title = null;
	
	public SavedDataSessionV1 storeFrom(DataController controller) {
		this.discards = controller.getDiscards().list();
		if (!controller.getDataPaths().isEmpty()) {
			DataFile first = controller.getDataPaths().get(0);
			if (first != null && first.addressable()) {
				this.files = controller.getDataPaths().stream().map(p -> p.address().get()).collect(Collectors.toList());
			}
		}
		this.dataSourcePluginUUID = controller.getDataSourcePluginUUID();
		this.dataSourceParameters = controller.getDataSourceParameters();
		this.title = controller.title;
		return this;
	}
	
	@Deprecated(since = "6", forRemoval = true)
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
	
	@Deprecated(since = "6", forRemoval = true)
	List<DataFile> filesAsDataPaths() {
		EventfulCache<Path> lazyDownload = new EventfulNullableCache<>(DataFiles::createDownloadDirectory);
		return this.files.stream()
				.map(f -> DataFiles.construct(f, lazyDownload::getValue))
				.collect(Collectors.toList());
	}
	
}