package org.peakaboo.ui.swing.plotting.datasource;

import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.options.OptionChooserLayer;
import org.peakaboo.framework.stratus.components.ui.options.OptionRadioButton;


public class DataSourceSelection extends OptionChooserLayer<DataSourcePlugin> {

	public DataSourceSelection(LayerPanel parent, List<DataSourcePlugin> dsps, Consumer<DataSourcePlugin> onSelect) {
		super(parent, "Please Select Data Format", dsps, onSelect, (dsp) -> {
			return new OptionRadioButton().withText(dsp.getFileFormat().getFormatName(), dsp.getFileFormat().getFormatDescription());
		});		
	}
	
}
