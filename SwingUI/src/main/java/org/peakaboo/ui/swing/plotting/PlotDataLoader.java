package org.peakaboo.ui.swing.plotting;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.data.DataLoader;
import org.peakaboo.dataset.DatasetReadResult;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.view.swing.SwingAutoPanel;
import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.plural.swing.ExecutorSetViewLayer;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderPanel;
import org.peakaboo.framework.stratus.components.ui.layers.LayerDialog;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;
import org.peakaboo.framework.stratus.components.ui.layers.ToastLayer;
import org.peakaboo.ui.swing.plotting.datasource.DataSourceSelection;

class PlotDataLoader extends DataLoader {

	private final PlotPanel plotPanel;

	PlotDataLoader(PlotPanel plotPanel, PlotController controller) {
		super(controller);
		this.plotPanel = plotPanel;
	}

	@Override
	public void onLoading(ExecutorSet<DatasetReadResult> job) {
		this.plotPanel.pushLayer(new ExecutorSetViewLayer(this.plotPanel, job));
	}

	@Override
	public void onSuccess(DataLoaderContext ctx) {
		// set some controls based on the fact that we have just loaded a
		// new data set
		this.plotPanel.getCanvas().updateCanvasSize();
	}

	@Override
	public void onFail(DataLoaderContext ctx, String message) {
		new LayerDialog(
				"Open Failed", 
				message, 
				StockIcon.BADGE_ERROR
			).showIn(this.plotPanel);
	}

	@Override
	public void onParameters(Group parameters, Consumer<Boolean> finished) {
		HeaderPanel paramPanel = new HeaderPanel();
		ModalLayer layer = new ModalLayer(this.plotPanel, paramPanel);
		
		
		paramPanel.getHeader().setCentre("Options");
		paramPanel.getHeader().setShowClose(false);
		
		FluentButton ok = new FluentButton("OK")
				.withStateDefault()
				.withAction(() -> {
					this.plotPanel.removeLayer(layer);
					finished.accept(true);
				});
		FluentButton cancel = new FluentButton("Cancel")
				.withAction(() -> {
					this.plotPanel.removeLayer(layer);
					finished.accept(false);
				});
		
		paramPanel.getHeader().setLeft(cancel);
		paramPanel.getHeader().setRight(ok);
		
		
		SwingAutoPanel sap = new SwingAutoPanel(parameters);
		sap.setBorder(Spacing.bHuge());
		paramPanel.setBody(sap);
		
		
		this.plotPanel.pushLayer(layer);
	}

	@Override
	public void onSelection(List<DataSourcePlugin> datasources, Consumer<DataSourcePlugin> selected) {
		DataSourceSelection selection = new DataSourceSelection(this.plotPanel, datasources, selected);
		this.plotPanel.pushLayer(selection);
	}

	@Override
	public void onSessionNewer() {
		ToastLayer warning = new ToastLayer(this.plotPanel, "Session is from a newer version of Peakaboo.\nSome settings may not load correctly.");
		this.plotPanel.pushLayer(warning);
	}

	@Override
	public void onSessionHasData(File sessionFile, Consumer<Boolean> load) {
		FluentButton buttonYes = new FluentButton("Yes")
				.withStateDefault()
				.withAction(() -> {
					controller.io().setFromSession(sessionFile);
					load.accept(true);
				});
		
		FluentButton buttonNo = new FluentButton("No")
				.withAction(() -> load.accept(false));
		
		new LayerDialog(
				"Open Associated Data Set?", 
				"This session is associated with another data set.\nDo you want to open that data set now?", 
				StockIcon.BADGE_QUESTION)
			.addRight(buttonYes)
			.addLeft(buttonNo)
			.showIn(this.plotPanel);
		
		buttonYes.grabFocus();
	}

	@Override
	public void onSessionFailure() {
		new LayerDialog(
				"Loading Session Failed", 
				"The selected session file could not be read.\nIt may be corrupted, or from too old a version of Peakaboo.", 
				StockIcon.BADGE_ERROR).showIn(this.plotPanel);
	}

	@Override
	public void onWarn(String message) {
		var toast = new ToastLayer(this.plotPanel, message);
		this.plotPanel.pushLayer(toast);
	}
}