package peakaboo.ui.swing.plugins;

import java.util.stream.Collectors;

import javax.swing.JTabbedPane;

import peakaboo.datasink.plugin.DataSinkLoader;
import peakaboo.datasource.plugin.DataSourceLoader;
import peakaboo.filter.model.FilterLoader;
import swidget.widgets.ComponentListPanel;

public class PluginsOverview extends JTabbedPane {

	public PluginsOverview() {
	
		ComponentListPanel dsourcePanel = new ComponentListPanel(
			DataSourceLoader
			.getPluginSet()
			.getAll()
			.stream()
			.map(PluginView::new)
			.collect(Collectors.toList())
		);
		this.add("Data Sources", dsourcePanel);
		
		
		ComponentListPanel dsinkPanel = new ComponentListPanel(
				DataSinkLoader
				.getPluginSet()
				.getAll()
				.stream()
				.map(PluginView::new)
				.collect(Collectors.toList())
			);
			this.add("Data Sinks", dsinkPanel);
		
		
		ComponentListPanel filterPanel = new ComponentListPanel(
			FilterLoader
			.getPluginSet()
			.getAll()
			.stream()
			.map(PluginView::new)
			.collect(Collectors.toList())
		);
		this.add("Filters", filterPanel);
		
	}
	
}
