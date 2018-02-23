package peakaboo.ui.javafx.plot.filter;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import peakaboo.controller.plotter.filtering.FilteringController;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterLoader;
import peakaboo.filter.model.FilterType;
import peakaboo.filter.model.plugin.FilterPlugin;
import peakaboo.ui.javafx.change.ChangeController;
import peakaboo.ui.javafx.util.FXUtil;
import peakaboo.ui.javafx.util.IActofUIController;
import peakaboo.ui.javafx.widgets.dialogheader.DialogHeader;


public class FilterUIController extends IActofUIController {

	FilteringController filters;

	@FXML private ListView<Filter> filterList;
	@FXML ListView<BoltPluginController<? extends FilterPlugin>> listBackground, listNoise, listMath, listProgramming, listAdvanced;
	@FXML Accordion availableFilters;
	@FXML private BorderPane overviewPane, addPane;

	@Override
	public void ready() throws IOException {
		
		filterList.setCellFactory(view -> new FilterCellView(this));
		
		// TODO Auto-generated method stub
		showFilters();
		
		DialogHeader addHeader = DialogHeader.load(this::onAcceptAdd, this::onCancelAdd);
		addPane.setTop(addHeader.getNode());
	}

	@Override
	protected void initialize() throws Exception {
		// TODO Auto-generated method stub

	}

	public void setFilteringController(FilteringController filters) {
		this.filters = filters;
		populate();
	}


	public void onAdd() {
		showAdd();
	}


	public void onRemove() {
		int index = filterList.getSelectionModel().getSelectedIndex();
		filterList.getItems().remove(index);
		filters.removeFilter(index);
		getChangeBus().broadcast(new FilterChange(this));
		
	}

	public void onClear() {
		filterList.getItems().clear();
		filters.clearFilters();
		getChangeBus().broadcast(new FilterChange(this));
	}

	public void onUp() {
		int index = filterList.getSelectionModel().getSelectedIndex();
		if (index == 0) { return; }
		filters.moveFilterUp(index);
		Filter filter = filterList.getItems().remove(index);
		filterList.getItems().add(index - 1, filter);
		filterList.getSelectionModel().select(index - 1);
		getChangeBus().broadcast(new FilterChange(this));
	}

	public void onDown() {
		int index = filterList.getSelectionModel().getSelectedIndex();
		if (index == filterList.getItems().size() - 1) { return; }
		filters.moveFilterDown(index);
		Filter filter = filterList.getItems().remove(index);
		filterList.getItems().add(index + 1, filter);
		filterList.getSelectionModel().select(index + 1);
		getChangeBus().broadcast(new FilterChange(this));
	}

	public void onAcceptAdd() {
		
		TitledPane pane = availableFilters.getExpandedPane();
		if (pane != null) {
			ListView<BoltPluginController<? extends FilterPlugin>> list = (ListView<BoltPluginController<? extends FilterPlugin>>) pane.getContent();
			BoltPluginController<? extends FilterPlugin> plugin = list.getSelectionModel().getSelectedItem();
			Filter filter = plugin.create();
			filter.initialize();
			filters.addFilter(filter);
		}
		
		populateFilters();
		showFilters();
		getChangeBus().broadcast(new FilterChange(this));
	}

	public void onCancelAdd() {
		showFilters();
	}


	private void populate() {
		populateFilters();
		populateAdd();
	}
	
	private void populateFilters() {
		
		//populate filter list
		filterList.getItems().clear();
		for (Filter filter : filters.getActiveFilters()) {
			filterList.getItems().add(filter);
		}
	}

	private void populateAdd() {

		for (FilterType type : FilterType.values()) {
			getAddListViewByFilterType(type).getItems().clear();
		}
		
		//populate available filter lists
		for (BoltPluginController<? extends FilterPlugin> plugin : FilterLoader.getPluginSet().getAll()) {
			ListView<BoltPluginController<? extends FilterPlugin>> list = getAddListViewByFilterType(plugin.getReferenceInstance().getFilterType());
			list.getItems().add(plugin);
		}
		
		
	}

	private ListView<BoltPluginController<? extends FilterPlugin>> getAddListViewByFilterType(FilterType type) {
		switch (type) {
		case ADVANCED:
			return listAdvanced;
		case BACKGROUND:
			return listBackground;
		case MATHEMATICAL:
			return listMath;
		case NOISE:
			return listNoise;
		case PROGRAMMING:
			return listProgramming;
		default:
			return null;
		
		}
	}
	
	private void showFilters() {
		overviewPane.setVisible(true);
		addPane.setVisible(false);
	}
	
	private void showAdd() {
		overviewPane.setVisible(false);
		addPane.setVisible(true);
	}
	
	public static FilterUIController load(ChangeController changes) throws IOException {
		return FXUtil.load(FilterUIController.class, "Filter.fxml", changes);
	}

}
