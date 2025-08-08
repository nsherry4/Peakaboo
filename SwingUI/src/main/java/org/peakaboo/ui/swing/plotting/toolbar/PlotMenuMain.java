package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.apache.commons.io.FilenameUtils;
import org.peakaboo.app.RecentSessions;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menu.FluentMenuItem;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuMain extends JPopupMenu {

	private PlotController controller;
	private JMenuItem undo, redo, save, saveAs;
	private JMenu export;
	private JMenuItem exportSinks, exportImage, exportFilteredSpectrum, exportFilteredData, exportFittings, exportArchive;
	private List<FluentMenuItem> recents;
	
	public PlotMenuMain(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		FluentMenuItem mOpen = new FluentMenuItem()
				.withText("Open Data\u2026")
				.withTooltip("Opens new data sets.")
				.withIcon(StockIcon.DOCUMENT_OPEN_SYMBOLIC)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_O)
				.withAction(plot::actionOpenData);
		this.add(mOpen);

		save = new FluentMenuItem()
				.withText("Save Session")
				.withIcon(StockIcon.DOCUMENT_SAVE_SYMBOLIC)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), plot)
				.withAction(plot::actionSaveSession);
		this.add(save);

		saveAs = new FluentMenuItem()
				.withText("Save Session As\u2026")
				.withIcon(StockIcon.DOCUMENT_SAVE_AS_SYMBOLIC)
				.withAction(plot::actionSaveSessionAs);
		this.add(saveAs);
		
		FluentMenuItem mLoad = new FluentMenuItem()
				.withText("Load Session")
				.withAction(plot::actionLoadSession);
		this.add(mLoad);
		
		
		
		var recentSessionsMenu = new JMenu("Recent Sessions");
		this.add(recentSessionsMenu);
		
		recents = new ArrayList<>();
		for (int i = 0; i < RecentSessions.SIZE; i++) {
			var item = recentSessionMenuItem(plot, i);
			recents.add(item);
			recentSessionsMenu.add(item);
		}
		updateRecentSessionsMenu();
		RecentSessions.SYSTEM.addListener(this::updateRecentSessionsMenu);
		
		
		

		export = new JMenu("Export");
		this.add(export);
		
		exportSinks = PlotMenuExport.makeExportSinks(plot);
		export.add(exportSinks);

		exportImage = PlotMenuExport.makeExportImage(plot);
		export.add(exportImage);

		exportFilteredSpectrum = PlotMenuExport.makeExportFilteredSpectrum(plot);
		export.add(exportFilteredSpectrum);
		
		exportFilteredData = PlotMenuExport.makeExportFilteredDataset(plot);
		export.add(exportFilteredData);
		
		exportFittings = PlotMenuExport.makeExportFittings(plot);
		export.add(exportFittings);

		exportArchive = PlotMenuExport.makeExportArchive(plot);
		export.add(exportArchive);
		
		

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
	
		
		JMenu debug = new JMenu("Logs & Bugs");
		
		
		JMenuItem logs = new FluentMenuItem()
				.withText("Show Logs")
				.withAction(plot::actionShowLogs);
		debug.add(logs);

		
		JMenuItem bugreport = new FluentMenuItem()
				.withText("Report a Bug")
				.withAction(plot::actionReportBug);
		debug.add(bugreport);
		
		
		this.add(debug);
		
		
		JMenuItem help = new FluentMenuItem()
				.withText("Help")
				.withIcon(StockIcon.APP_HELP)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), plot)
				.withAction(plot::actionHelp);
		this.add(help);
		
		JMenuItem about = new FluentMenuItem()
				.withText("About")
				.withIcon(StockIcon.APP_ABOUT)
				.withAction(plot::actionAbout);
		this.add(about);
	}
	
	
	private FluentMenuItem recentSessionMenuItem(PlotPanel plot, int index) {
		return new FluentMenuItem().withText("").withAction(() -> plot.actionLoadSession(RecentSessions.SYSTEM.getRecentSessionFiles().get(index)));
	}
	
	private void updateRecentSessionsMenu() {
		var sessions = RecentSessions.SYSTEM.getRecentSessionFiles();
		for (int i = 0; i < RecentSessions.SIZE; i++) {
			var recent = recents.get(i);
			if (sessions.size() <= i) {
				recent.setVisible(false);
			} else {
				recent.setVisible(true);
				var session = sessions.get(i);
				recent.setText(FilenameUtils.getBaseName(session.getAbsolutePath()));
				recent.setToolTipText(session.getAbsolutePath());
			}
		}
	}
	
	public void setWidgetState(boolean hasData) {

		undo.setEnabled(controller.history().canUndo());
		redo.setEnabled(controller.history().canRedo());
		undo.setText("Undo " + controller.history().getNextUndo());
		redo.setText("Redo " + controller.history().getNextRedo());
		
		save.setEnabled(hasData && controller.io().getSessionFile() != null && controller.history().hasUnsavedWork());
		saveAs.setEnabled(hasData);
		
		exportImage.setEnabled(hasData);
		exportFittings.setEnabled(hasData);
		exportFilteredData.setEnabled(hasData);
		exportFilteredSpectrum.setEnabled(hasData);
		exportArchive.setEnabled(hasData);
		exportSinks.setEnabled(hasData);
	
	}
	
	
	
}
