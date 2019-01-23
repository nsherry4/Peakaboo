package peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.common.Version;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.datasink.plugin.DataSinkPlugin;
import org.peakaboo.datasink.plugin.DataSinkPluginManager;

import cyclops.util.Mutable;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.StockIcon;

public class PlotMenuMain extends JPopupMenu {

	private PlotController controller;
	
	
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
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), null
		));
		
		this.add(PlotMenuUtils.createMenuItem(plot,
				"Load Session", null, null,
				e -> plot.actionLoadSession(),
				null, null
		));
		

		

		


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
		
		
		JMenu debug = new JMenu("Logs & Bugs");
		
		
		JMenuItem logs = PlotMenuUtils.createMenuItem(plot,
				"Show Logs", null, null,
				e -> plot.actionShowLogs(),
				null, null
			);
		debug.add(logs);

		Mutable<Boolean> isDebug = new Mutable<>(false);
		JMenuItem debugLog = PlotMenuUtils.createMenuCheckItem(plot,
				"Verbose Logging", null, "Generates extra logging information for troubleshooting purposes",
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
		debug.add(debugLog);
		
		JMenuItem bugreport = PlotMenuUtils.createMenuItem(plot,
				"Report a Bug", null, null,
				e -> plot.actionReportBug(),
				null, null
			);
		debug.add(bugreport);
		
		if (!Version.release) {
			JMenuItem console = PlotMenuUtils.createMenuItem(plot,
					"Debug Console", null, null,
					e -> plot.actionDebugConsole(),
					null, null
				);
			debug.add(console);
		}
		
		
		this.add(debug);
		
		
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

		undo.setEnabled(controller.history().canUndo());
		redo.setEnabled(controller.history().canRedo());
		undo.setText("Undo " + controller.history().getNextUndo());
		redo.setText("Redo " + controller.history().getNextRedo());
		
	}
	
	
	
}
