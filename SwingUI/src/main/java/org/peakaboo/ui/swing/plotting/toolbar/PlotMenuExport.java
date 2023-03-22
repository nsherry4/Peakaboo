package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.datasink.plugin.DataSinkPluginManager;
import org.peakaboo.datasink.plugin.JavaDataSinkPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menuitem.FluentMenuItem;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuExport extends JPopupMenu {


	private JMenuItem exportFittings;
	private JMenuItem exportFilteredData;
	private JMenuItem exportFilteredSpectrum;
	private JMenuItem exportArchive;
	private JMenu exportSinks;
	private JMenuItem exportImage;
	
	public PlotMenuExport(PlotPanel plot) {
				
		exportSinks = makeExportSinks(plot);
		this.add(exportSinks);

		exportImage = makeExportImage(plot);
		this.add(exportImage);

		exportFilteredSpectrum = makeExportFilteredSpectrum(plot);
		this.add(exportFilteredSpectrum);
		
		exportFilteredData = makeExportFilteredDataset(plot);
		this.add(exportFilteredData);
		
		exportFittings = makeExportFittings(plot);
		this.add(exportFittings);

		exportArchive = makeExportArchive(plot);
		this.add(exportArchive);
		

		
	}
	
	
	public void setWidgetState(boolean hasData) {
		exportImage.setEnabled(hasData);
		exportFittings.setEnabled(hasData);
		exportFilteredData.setEnabled(hasData);
		exportFilteredSpectrum.setEnabled(hasData);
		exportArchive.setEnabled(hasData);
		exportSinks.setEnabled(hasData);
	}
	
	public static JMenu makeExportSinks(PlotPanel plot) {
		JMenu exportSinks = new JMenu("Raw Data");
		
		for (BoltPluginPrototype<? extends JavaDataSinkPlugin> plugin : DataSinkPluginManager.system().getPlugins()) {
			exportSinks.add(new FluentMenuItem()
					.withText(plugin.getName())
					.withAction(() -> plot.actionExportData(plugin.create()))
				);
		}
		
		return exportSinks;
	}
	
	public static JMenuItem makeExportImage(PlotPanel plot) {
		return new FluentMenuItem()
				.withText("Plot as Image\u2026")
				.withTooltip("Saves the current plot as an image")
				.withIcon(StockIcon.MIME_RASTER_SYMBOLIC)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_P)
				.withAction(plot::actionSavePicture);
	}
	
	public static JMenuItem makeExportFilteredSpectrum(PlotPanel plot) {
		return new FluentMenuItem()
				.withText("Filtered Spectrum as CSV")
				.withTooltip("Saves the filtered spectrum to a CSV file")
				.withIcon(StockIcon.DOCUMENT_EXPORT_SYMBOLIC)
				.withAction(plot::actionSaveFilteredSpectrum);
	}
	
	public static JMenuItem makeExportFilteredDataset(PlotPanel plot) {
		return new FluentMenuItem()
				.withText("Filtered Data Set as CSV")
				.withTooltip("Saves the filtered dataset to a CSV file")
				.withIcon(StockIcon.DOCUMENT_EXPORT_SYMBOLIC)
				.withAction(plot::actionSaveFilteredDataSet);
	}
	
	public static JMenuItem makeExportFittings(PlotPanel plot) {
		return new FluentMenuItem()
				.withText("Fittings as Text")
				.withTooltip("Saves the current fitting data to a text file")
				.withAction(plot::actionSaveFittingInformation);
	}
	
	public static JMenuItem makeExportArchive(PlotPanel plot) {
		return new FluentMenuItem()
				.withText("All-In-One Zip Archive")
				.withTooltip("Saves the plot, session file, fittings, and related data")
				.withAction(plot::actionExportArchive);
	}
	
}
