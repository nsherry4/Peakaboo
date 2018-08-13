package peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.PlotController;
import peakaboo.datasink.plugin.DataSinkPlugin;
import peakaboo.datasink.plugin.DataSinkPluginManager;
import peakaboo.ui.swing.plotting.PlotPanel;
import scitypes.util.Mutable;
import swidget.icons.StockIcon;

public class PlotMenuMain extends JPopupMenu {

	private PlotController controller;
	
	//FILE
	private JMenuItem					snapshotMenuItem;
	private JMenuItem					exportFittingsMenuItem;
	private JMenuItem					exportFilteredDataMenuItem;
	private JMenu 						exportSinks;

	//EDIT
	private JMenuItem					undo, redo;
	
	public PlotMenuMain(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		this.add(PlotMenuUtils.createMenuItem(plot,
				"Open Data\u2026", StockIcon.DOCUMENT_OPEN.toMenuIcon(), "Opens new data sets.",
				e -> plot.actionOpenData(),
				KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), KeyEvent.VK_O
		));
		
		
		this.add(PlotMenuUtils.createMenuItem(plot,
				"Save Session", StockIcon.DOCUMENT_SAVE.toMenuIcon(), null, 
				e -> plot.actionSaveSession(),
				null, null
		));
		
		this.add(PlotMenuUtils.createMenuItem(plot,
				"Load Session", null, null,
				e -> plot.actionLoadSession(),
				null, null
		));
		

		
		JMenu export = new JMenu("Export");
		
		exportSinks = new JMenu("Raw Data");
		
		for (BoltPluginController<? extends DataSinkPlugin> plugin : DataSinkPluginManager.SYSTEM.getPlugins().getAll()) {
			exportSinks.add(PlotMenuUtils.createMenuItem(plot,
					plugin.getName(), null, null,
					e -> plot.actionExportData(plugin.create()),
					null, null
			));
		}
		
		export.add(exportSinks);
		

		
		snapshotMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Plot as Image\u2026", StockIcon.DEVICE_CAMERA.toMenuIcon(), "Saves the current plot as an image",
				e -> plot.actionSavePicture(),
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), KeyEvent.VK_P
		);
		export.add(snapshotMenuItem);
		
		
		exportFilteredDataMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Filtered Data as Text", StockIcon.DOCUMENT_EXPORT.toMenuIcon(), "Saves the filtered data to a text file",
				e -> plot.actionSaveFilteredData(),
				null, null
		);
		export.add(exportFilteredDataMenuItem);
		
		exportFittingsMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Fittings as Text", null, "Saves the current fitting data to a text file",
				e -> plot.actionSaveFittingInformation(),
				null, null
		);
		export.add(exportFittingsMenuItem);


		this.add(export);
		
				

		this.addSeparator();

		
		undo = PlotMenuUtils.createMenuItem(plot,
				"Undo", StockIcon.EDIT_UNDO.toMenuIcon(), "Undoes a previous action",
				e -> controller.history().undo(),
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK), KeyEvent.VK_U
		);
		this.add(undo);

		redo = PlotMenuUtils.createMenuItem(plot,
				"Redo", StockIcon.EDIT_REDO.toMenuIcon(), "Redoes a previously undone action",
				e -> controller.history().redo(),
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK), KeyEvent.VK_R
		);
		this.add(redo);

		this.addSeparator();

		//HELP Menu
		
		JMenuItem plugins = PlotMenuUtils.createMenuItem(plot,
				"Plugins", null, "Shows information about Peakaboo's plugins",
				e -> plot.actionShowPlugins(),
				null, null
			);
		this.add(plugins);
		
		JMenuItem logs = PlotMenuUtils.createMenuItem(plot,
				"Logs", null, null,
				e -> plot.actionShowLogs(),
				null, null
			);
		this.add(logs);

		Mutable<Boolean> isDebug = new Mutable<>(false);
		JMenuItem debugLog = PlotMenuUtils.createMenuCheckItem(plot,
				"Debug", null, "Generates extra logging information for troubleshooting purposes",
				b -> {
					isDebug.set(!isDebug.get());
					if (isDebug.get()) {
						PeakabooLog.getRoot().setLevel(Level.FINE);
					} else {
						PeakabooLog.getRoot().setLevel(Level.INFO);
					}
				},
				null, null
		);
		this.add(debugLog);
		
		JMenuItem bugreport = PlotMenuUtils.createMenuItem(plot,
				"Report a Bug", null, null,
				e -> plot.actionReportBug(),
				null, null
			);
		this.add(bugreport);
		
		JMenuItem help = PlotMenuUtils.createMenuItem(plot,
			"Help", StockIcon.BADGE_HELP.toMenuIcon(), null,
			e -> plot.actionHelp(),
			KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), null
		);
		this.add(help);
		
		JMenuItem about = PlotMenuUtils.createMenuItem(plot,
			"About", StockIcon.MISC_ABOUT.toMenuIcon(), null,
			e -> plot.actionAbout(),
			null, null
		);
		this.add(about);
	}
	
	public void setWidgetState(boolean hasData) {
		
		snapshotMenuItem.setEnabled(hasData);
		exportFittingsMenuItem.setEnabled(hasData);
		exportFilteredDataMenuItem.setEnabled(hasData);
		exportSinks.setEnabled(hasData);
				
		undo.setEnabled(controller.history().canUndo());
		redo.setEnabled(controller.history().canRedo());
		undo.setText("Undo " + controller.history().getNextUndo());
		redo.setText("Redo " + controller.history().getNextRedo());
				
		
	}
	
	
	
}
