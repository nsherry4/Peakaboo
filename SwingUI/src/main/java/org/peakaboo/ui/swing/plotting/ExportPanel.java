package org.peakaboo.ui.swing.plotting;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JPanel;

import org.peakaboo.framework.cyclops.visualization.SurfaceType;
import org.peakaboo.framework.cyclops.visualization.backend.awt.GraphicsPanel;
import org.peakaboo.framework.cyclops.visualization.backend.awt.SavePicture;
import org.peakaboo.framework.cyclops.visualization.backend.awt.SavePicture.DimensionPicker;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;
import org.peakaboo.framework.stratus.components.ui.options.OptionChooserPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;

public class ExportPanel extends JPanel {
	
	private OptionChooserPanel<SurfaceType> formatPicker;
	private DimensionPicker dimensionPicker;

	public ExportPanel(LayerPanel parent, GraphicsPanel canvas, Runnable onAccept) {
		//this.setPreferredSize(new Dimension(500, 350));
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

		
		formatPicker = new OptionChooserPanel<>(Arrays.asList(SurfaceType.values()), item -> {
			return new OptionRadioButton().withText(item.title(), item.description());
		});
		body.add(formatPicker, BorderLayout.CENTER);
		
		this.add(body, BorderLayout.CENTER);
		parent.pushLayer(layer);
		
	}
	
	
	

	
	public SurfaceType getPlotFormat() {
		return formatPicker.getSelected();
	}
	
	public int getImageWidth() {
		return dimensionPicker.getDimensionWidth();
	}
	
	public int getImageHeight() {
		return dimensionPicker.getDimensionHeight();
	}
	
}


