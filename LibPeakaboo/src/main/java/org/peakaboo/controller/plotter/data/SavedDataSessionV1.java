package org.peakaboo.controller.plotter.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.io.DataInputAdapters;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;

@Deprecated(since = "6", forRemoval = true)
public class SavedDataSessionV1 {

	public List<Integer> discards = new ArrayList<>();
	public List<String> files = new ArrayList<>();
	public String dataSourcePluginUUID = null;
	public Map<String, Object> dataSourceParameters = null;
	public String title = null;

	@Deprecated(since = "6", forRemoval = true)
	public void loadInto(DataController controller) {
		controller.getDiscards().clear();
		for (int i : discards) {
			controller.getDiscards().discard(i);
		}
		controller.setDataSourcePlugin(new SavedPlugin(this.dataSourcePluginUUID, "Data Source", "", dataSourceParameters));
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
	List<DataInputAdapter> filesAsDataPaths() {
		EventfulCache<Path> lazyDownload = new EventfulNullableCache<>(DataInputAdapters::createDownloadDirectory);
		return this.files.stream()
				.map(f -> DataInputAdapters.construct(f, lazyDownload::getValue))
				.collect(Collectors.toList());
	}
	
}
