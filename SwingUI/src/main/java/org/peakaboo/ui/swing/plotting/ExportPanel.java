package org.peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import cyclops.visualization.SurfaceType;
import cyclops.visualization.backend.awt.GraphicsPanel;
import cyclops.visualization.backend.awt.SavePicture;
import cyclops.visualization.backend.awt.SavePicture.DimensionPicker;
import cyclops.visualization.backend.awt.SavePicture.FormatPicker;
import swidget.widgets.Spacing;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.layout.HeaderBox;

public class ExportPanel extends JPanel {
	
	private FormatPicker formatPicker;
	private DimensionPicker dimensionPicker;

	public ExportPanel(LayerPanel parent, GraphicsPanel canvas, Runnable onAccept) {
		this.setPreferredSize(new Dimension(500, 350));
		this.setLayout(new BorderLayout());
		ModalLayer layer = new ModalLayer(parent, this);
		
		
		HeaderBox header = HeaderBox.createYesNo("Export Archive", 
				"OK", () -> {
					onAccept.run();
					parent.removeLayer(layer);
				}, 
				"Cancel", () -> {
					parent.removeLayer(layer);
				}
			);
		this.add(header, BorderLayout.NORTH);
		
		
		JPanel body = new JPanel();
		body.setBorder(Spacing.bHuge());
		body.setLayout(new BorderLayout(Spacing.huge, Spacing.huge));
		
		dimensionPicker = new SavePicture.DimensionPicker((int)Math.ceil(canvas.getUsedWidth()), (int)Math.ceil(canvas.getUsedHeight()));
		body.add(dimensionPicker, BorderLayout.NORTH);

		
		formatPicker = new SavePicture.FormatPicker();
		body.add(formatPicker, BorderLayout.CENTER);
		
		this.add(body, BorderLayout.CENTER);
		parent.pushLayer(layer);
		
	}
	
	
	

	
	public SurfaceType getPlotFormat() {
		return formatPicker.getSelectedSurfaceType();
	}
	
	public int getImageWidth() {
		return dimensionPicker.getDimensionWidth();
	}
	
	public int getImageHeight() {
		return dimensionPicker.getDimensionHeight();
	}
	
}


