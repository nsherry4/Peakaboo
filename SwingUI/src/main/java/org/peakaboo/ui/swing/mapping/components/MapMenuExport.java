package org.peakaboo.ui.swing.mapping.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menu.FluentMenuItem;
import org.peakaboo.ui.swing.mapping.MapperPanel;

public class MapMenuExport extends JPopupMenu {


	
	private JMenuItem snapshotMenuItem;
	private JMenuItem exportCSV;
	private JMenuItem exportArchive;
	
	public MapMenuExport(MapperPanel plot) {
				
		
		snapshotMenuItem = new FluentMenuItem()
				.withText("Export as Image\u2026")
				.withTooltip("Exports the current plot as an image")
				.withIcon(StockIcon.MIME_RASTER_SYMBOLIC)
				.withKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), plot)
				.withMnemonic(KeyEvent.VK_P)
				.withAction(plot::actionSavePicture);
		this.add(snapshotMenuItem);

		exportCSV = new FluentMenuItem()
				.withText("Export as CSV")
				.withTooltip("Export the current map as a comma separated value file")
				.withAction(plot::actionSaveCSV);
		this.add(exportCSV);
		

		exportArchive = new FluentMenuItem()
				.withText("All-In-One Zip Archive")
				.withTooltip("Export all selected maps as images and comma separated value files in a zip archive")
				.withAction(plot::actionSaveArchive);
		this.add(exportArchive);
		

		
	}

	
}
