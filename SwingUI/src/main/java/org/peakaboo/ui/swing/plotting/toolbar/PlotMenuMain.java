package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.common.PeakabooLog;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.fluent.menuitem.FluentMenuItem;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuMain extends JPopupMenu {

	private PlotController controller;
	private JMenuItem undo, redo, save, saveAs;
	
	public PlotMenuMain(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		FluentMenuItem mOpen = new FluentMenuItem()
				.withText("Open Data\u2026")
				.withTooltip("Opens new data sets.")
				.withIcon(StockIcon.DOCUMENT_OPEN)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_O)
				.withAction(plot::actionOpenData);
		this.add(mOpen);

		save = new FluentMenuItem()
				.withText("Save Session")
				.withIcon(StockIcon.DOCUMENT_SAVE)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), plot)
				.withAction(plot::actionSaveSession);
		this.add(save);

		saveAs = new FluentMenuItem()
				.withText("Save Session As\u2026")
				.withIcon(StockIcon.DOCUMENT_SAVE_AS)
				.withAction(plot::actionSaveSessionAs);
		this.add(saveAs);
		
		FluentMenuItem mLoad = new FluentMenuItem()
				.withText("Load Session")
				.withAction(plot::actionLoadSession);
		this.add(mLoad);
		

		this.addSeparator();

		
		undo = new FluentMenuItem()
				.withText("Undo")
				.withTooltip("Undoes a previous action")
				.withIcon(StockIcon.EDIT_UNDO)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_U)
				.withAction(() -> controller.history().undo());
		this.add(undo);

		redo = new FluentMenuItem()
				.withText("Redo")
				.withTooltip("Redoes a previously undone action")
				.withIcon(StockIcon.EDIT_REDO)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_R)
				.withAction(() -> controller.history().redo());
		this.add(redo);

		
		this.addSeparator();

		
		//HELP Menu
		JMenuItem plugins = new FluentMenuItem()
				.withText("Plugins")
				.withTooltip("Manage Peakaboo's plugins")
				.withAction(plot::actionShowPlugins);
		this.add(plugins);
		
		
		JMenu debug = new JMenu("Logs & Bugs");
		
		
		JMenuItem logs = new FluentMenuItem()
				.withText("Show Logs")
				.withAction(plot::actionShowLogs);
		debug.add(logs);

		Mutable<Boolean> isDebug = new Mutable<>(false);
		JMenuItem debugLog = new FluentMenuItem()
				.withText("Verbose Logging")
				.withTooltip("Generates extra logging information for troubleshooting purposes")
				.withAction(() -> {
					isDebug.set(!isDebug.get());
					if (isDebug.get()) {
						PeakabooLog.getRoot().setLevel(Level.FINE);
					} else {
						PeakabooLog.getRoot().setLevel(Level.INFO);
					}
				});
		debug.add(debugLog);
		
		JMenuItem bugreport = new FluentMenuItem()
				.withText("Report a Bug")
				.withAction(plot::actionReportBug);
		debug.add(bugreport);
		
		JMenuItem console = new FluentMenuItem()
				.withText("Debug Console")
				.withAction(plot::actionDebugConsole);
		debug.add(console);

		
		this.add(debug);
		
		
		JMenuItem help = new FluentMenuItem()
				.withText("Help")
				.withIcon(StockIcon.BADGE_HELP)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), plot)
				.withAction(plot::actionHelp);
		this.add(help);
		
		JMenuItem about = new FluentMenuItem()
				.withText("About")
				.withIcon(StockIcon.MISC_ABOUT)
				.withAction(plot::actionAbout);
		this.add(about);
	}
	
	public void setWidgetState(boolean hasData) {

		undo.setEnabled(controller.history().canUndo());
		redo.setEnabled(controller.history().canRedo());
		undo.setText("Undo " + controller.history().getNextUndo());
		redo.setText("Redo " + controller.history().getNextRedo());
		
		save.setEnabled(hasData && controller.io().getSessionFile() != null && controller.history().hasUnsavedWork());
		saveAs.setEnabled(hasData);
		
	}
	
	
	
}
