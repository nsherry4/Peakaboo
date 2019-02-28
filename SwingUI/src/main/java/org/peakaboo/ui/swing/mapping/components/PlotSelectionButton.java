package org.peakaboo.ui.swing.mapping.components;

import java.util.stream.Collectors;

import javax.swing.JFrame;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.plotter.SavedSession;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.mapping.MapperPanel;
import org.peakaboo.ui.swing.plotting.PlotPanel;

import swidget.widgets.buttons.ToolbarImageButton;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedLayerPanel;

public class PlotSelectionButton extends ToolbarImageButton {

	private MappingController controller;
	private TabbedInterface<TabbedLayerPanel> plotter;
	
	public PlotSelectionButton(MappingController controller, TabbedInterface<TabbedLayerPanel> plotter) {
		super("Plot Selection", "view-subset");
		this.controller = controller;
		this.plotter = plotter;
		this.withSignificance(true).withTooltip("Plot the selection as a new data set");

		this.setEnabled(controller.getSelection().hasSelection());
		controller.addListener(s -> this.setEnabled(controller.getSelection().hasSelection()));
		
		this.withAction(this::action);
	}
	
	private void action() {
		SubsetDataSource sds = controller.getSelection().getSubsetDataSource();
		SavedSession settings = controller.getPlotSavedSettings();
		
		//update the bad scan indexes to match the new data source's indexing scheme
		//TODO: Is there a better way to do this?
		settings.data.discards = settings.data.discards.stream()
				.map(index -> sds.getUpdatedIndex(index))
				.filter(index -> index > 0)
				.collect(Collectors.toList()
			);
	
		PlotPanel subplot = new PlotPanel(plotter);
		subplot.actionLoadSubsetDataSource(sds, settings.serialize());
		plotter.addActiveTab(subplot);
		//Focus and un-minimize
		JFrame plotWindow = plotter.getWindow();
		plotWindow.toFront();
		int windowState = plotWindow.getExtendedState();
		if ((windowState & JFrame.ICONIFIED) == JFrame.ICONIFIED) {
			plotWindow.setExtendedState(windowState ^ JFrame.ICONIFIED);
		}
	}
	
}
