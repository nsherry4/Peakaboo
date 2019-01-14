package peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.StockIcon;

public class PlotMenuExport extends JPopupMenu {


	private JMenuItem exportFittingsMenuItem;
	private JMenuItem exportFilteredDataMenuItem;
	private JMenuItem exportArchive;
	private JMenu exportSinks;
	private JMenuItem snapshotMenuItem;
	
	public PlotMenuExport(PlotPanel plot) {
				
		exportSinks = new JMenu("Raw Data");
		
		for (BoltPluginPrototype<? extends DataSinkPlugin> plugin : DataSinkPluginManager.SYSTEM.getPlugins().getAll()) {
			exportSinks.add(PlotMenuUtils.createMenuItem(plot,
					plugin.getName(), null, null,
					e -> plot.actionExportData(plugin.create()),
					null, null
			));
		}
		
		this.add(exportSinks);
		

		
		snapshotMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Plot as Image\u2026", StockIcon.DEVICE_CAMERA.toMenuIcon(), "Saves the current plot as an image",
				e -> plot.actionSavePicture(),
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), KeyEvent.VK_P
		);
		this.add(snapshotMenuItem);
		
		
		exportFilteredDataMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Filtered Data as Text", StockIcon.DOCUMENT_EXPORT.toMenuIcon(), "Saves the filtered data to a text file",
				e -> plot.actionSaveFilteredData(),
				null, null
		);
		this.add(exportFilteredDataMenuItem);
		
		exportFittingsMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Fittings as Text", null, "Saves the current fitting data to a text file",
				e -> plot.actionSaveFittingInformation(),
				null, null
		);
		this.add(exportFittingsMenuItem);

		exportArchive = PlotMenuUtils.createMenuItem(plot,
				"All-In-One Zip Archive", null, "Saves the plot, session file, z-calibration and fittings",
				e -> plot.actionExportArchive(),
				null, null
		);
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
