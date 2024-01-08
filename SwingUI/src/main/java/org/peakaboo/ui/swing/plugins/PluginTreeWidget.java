package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.peakaboo.dataset.sink.plugin.DataSinkPlugin;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;

class PluginTreeWidget extends Stencil<Object> {

	private JLabel label;
	
	public PluginTreeWidget() {
		this.setLayout(new BorderLayout());
		this.label = new JLabel();
		this.setBackground(Color.RED);
		this.add(label);
		this.setBorder(Spacing.bMedium());
		
		this.setOpaque(true);
		this.label.setOpaque(false);
	}
	
	@Override
	protected void onSetValue(Object object, boolean selected) {
		this.label.setForeground(getForeground());
		
    	if (object instanceof BoltPluginRegistry manager) {
        	label.setText(manager.getInterfaceName());
        	label.setIcon(StockIcon.PLACE_FOLDER.toImageIcon(IconSize.BUTTON));
    	} else if (object instanceof BoltPluginPrototype) {
        	BoltPluginPrototype<? extends BoltPlugin> plugin = (BoltPluginPrototype<? extends BoltPlugin>)object;
        	label.setText(plugin.getName());
        	label.setIcon(getIcon(plugin));	
    	} else if (object instanceof BoltIssue issue) {
    		label.setText(issue.shortSource());
    		label.setIcon(StockIcon.BADGE_ERROR.toImageIcon(IconSize.BUTTON));
    	}
		
	}
	
	private ImageIcon getIcon(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == DataSourcePlugin.class) {
			return StockIcon.DOCUMENT_IMPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == DataSinkPlugin.class) {
			return StockIcon.DOCUMENT_EXPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == Filter.class) {
			return StockIcon.MISC_PLUGIN.toImageIcon(IconSize.BUTTON);
		}

		if (pluginBaseClass == MapFilterPlugin.class) {
			return StockIcon.MISC_PLUGIN.toImageIcon(IconSize.BUTTON);
		}
		
		return IconFactory.getImageIcon(plugin.getRegistry().getAssetPath() + "/icons/", plugin.getRegistry().getName(), IconSize.BUTTON);
		
	}

}
