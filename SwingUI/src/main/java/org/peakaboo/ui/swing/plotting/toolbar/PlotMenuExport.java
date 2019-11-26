package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.datasink.plugin.DataSinkPlugin;
import org.peakaboo.datasink.plugin.DataSinkPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.buttons.components.menuitem.SwidgetMenuItem;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuExport extends JPopupMenu {


	private JMenuItem exportFittingsMenuItem;
	private JMenuItem exportFilteredDataMenuItem;
	private JMenuItem exportFilteredSpectrumMenuItem;
	private JMenuItem exportArchive;
	private JMenu exportSinks;
	private JMenuItem snapshotMenuItem;
	
	public PlotMenuExport(PlotPanel plot) {
				
		exportSinks = new JMenu("Raw Data");
		
		for (BoltPluginPrototype<? extends DataSinkPlugin> plugin : DataSinkPluginManager.SYSTEM.getPlugins()) {
			exportSinks.add(new SwidgetMenuItem()
					.withText(plugin.getName())
					.withAction(() -> plot.actionExportData(plugin.create()))
				);
		}
		
		this.add(exportSinks);
		

		
		snapshotMenuItem = new SwidgetMenuItem()
				.withText("Plot as Image\u2026")
				.withTooltip("Saves the current plot as an image")
				.withIcon(StockIcon.DEVICE_CAMERA)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_P)
				.withAction(plot::actionSavePicture);
		this.add(snapshotMenuItem);

		exportFilteredSpectrumMenuItem = new SwidgetMenuItem()
				.withText("Filtered Spectrum as CSV")
				.withTooltip("Saves the filtered spectrum to a CSV file")
				.withIcon(StockIcon.DOCUMENT_EXPORT)
				.withAction(plot::actionSaveFilteredSpectrum);
		this.add(exportFilteredSpectrumMenuItem);
		
		exportFilteredDataMenuItem = new SwidgetMenuItem()
				.withText("Filtered Data Set as CSV")
				.withTooltip("Saves the filtered dataset to a CSV file")
				.withIcon(StockIcon.DOCUMENT_EXPORT)
				.withAction(plot::actionSaveFilteredDataSet);
		this.add(exportFilteredDataMenuItem);
		
		exportFittingsMenuItem = new SwidgetMenuItem()
				.withText("Fittings as Text")
				.withTooltip("Saves the current fitting data to a text file")
				.withAction(plot::actionSaveFittingInformation);
		this.add(exportFittingsMenuItem);

		exportArchive = new SwidgetMenuItem()
				.withText("All-In-One Zip Archive")
				.withTooltip("Saves the plot, session file, z-calibration and fittings")
				.withAction(plot::actionExportArchive);
		this.add(exportArchive);
		

		
	}
	
	
	public void setWidgetState(boolean hasData) {
		snapshotMenuItem.setEnabled(hasData);
		exportFittingsMenuItem.setEnabled(hasData);
		exportFilteredDataMenuItem.setEnabled(hasData);
		exportArchive.setEnabled(hasData);
		exportSinks.setEnabled(hasData);
	}
	
}
