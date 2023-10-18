package org.peakaboo.ui.swing.mapping.components;

import java.util.stream.Collectors;

import javax.swing.JFrame;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.plotter.SavedSessionV1;
import org.peakaboo.dataset.source.model.internal.SubsetDataSource;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToolbarButton;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedLayerPanel;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotSelectionButton extends FluentToolbarButton {

	private MappingController controller;
	private TabbedInterface<TabbedLayerPanel> plotter;
	
	public PlotSelectionButton(MappingController controller, TabbedInterface<TabbedLayerPanel> plotter) {
		super("Plot Selection", PeakabooIcons.PLOT);
		this.controller = controller;
		this.plotter = plotter;
		this.withSignificance(true).withTooltip("Plot the selection as a new data set");

		this.setEnabled(controller.getSelection().isReplottable());
		controller.addListener(t -> {
			this.setEnabled(controller.getSelection().isReplottable());
		});
		
		this.withAction(this::action);
	}
	
	private void action() {
		if (!controller.getSelection().isReplottable()) {
			return;
		}
		
		SubsetDataSource sds = controller.getSelection().getSubsetDataSource();
		SavedSessionV1 settings = controller.getPlotSavedSettings();
		
		//update the bad scan indexes to match the new data source's indexing scheme
		//TODO: Is there a better way to do this?
		settings.data.discards = settings.data.discards.stream()
				.map(sds::getUpdatedIndex)
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
