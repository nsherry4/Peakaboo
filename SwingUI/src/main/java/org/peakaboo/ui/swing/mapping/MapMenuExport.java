package org.peakaboo.ui.swing.mapping;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.ui.swing.plotting.toolbar.PlotMenuUtils;

import swidget.icons.StockIcon;

public class MapMenuExport extends JPopupMenu {


	
	private JMenuItem snapshotMenuItem;
	private JMenuItem exportCSV;
	private JMenuItem exportArchive;
	
	public MapMenuExport(MapperPanel plot) {
				
		
		snapshotMenuItem = PlotMenuUtils.createMenuItem(plot,
				"Export as Image\u2026", StockIcon.DEVICE_CAMERA.toMenuIcon(), "Exports the current plot as an image",
				e -> plot.actionSavePicture(),
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), KeyEvent.VK_P
		);
		this.add(snapshotMenuItem);

		exportCSV = PlotMenuUtils.createMenuItem(plot,
				"Export as CSV", null, "Export the current map as a comma separated value file",
				e -> plot.actionSaveCSV(),
				null, null
		);
		this.add(exportCSV);
		

		exportArchive = PlotMenuUtils.createMenuItem(plot,
				"All-In-One Zip Archive", null, "Export all selected maps as images and comma separated value files in a zip archive",
				e -> plot.actionSaveArchive(),
				null, null
		);
		this.add(exportArchive);
		

		
	}

	
}
