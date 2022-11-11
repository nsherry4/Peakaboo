package org.peakaboo.ui.swing.plotting.datasource;

import java.util.List;
import java.util.function.Consumer;

import org.peakaboo.datasource.plugin.JavaDataSourcePlugin;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.widgets.OptionChooserLayer;
import org.peakaboo.framework.swidget.widgets.options.OptionRadioButton;


public class DataSourceSelection extends OptionChooserLayer<JavaDataSourcePlugin> {

	public DataSourceSelection(LayerPanel parent, List<JavaDataSourcePlugin> dsps, Consumer<JavaDataSourcePlugin> onSelect) {
		super(parent, "Please Select Data Format", dsps, onSelect, (dsp) -> {
			return new OptionRadioButton().withText(dsp.getFileFormat().getFormatName(), dsp.getFileFormat().getFormatDescription());
		});		
	}
	
}
