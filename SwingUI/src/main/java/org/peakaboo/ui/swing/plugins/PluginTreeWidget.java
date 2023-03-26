package org.peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.peakaboo.datasink.plugin.JavaDataSinkPlugin;
import org.peakaboo.datasource.plugin.JavaDataSourcePlugin;
import org.peakaboo.filter.plugins.JavaFilterPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.stencil.StencilTreeCellRenderer;
import org.peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;

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
		
    	if (object instanceof BoltPluginManager) {
    		BoltPluginManager<?> manager = (BoltPluginManager<?>) object;
        	label.setText(manager.getInterfaceName());
        	label.setIcon(StockIcon.PLACE_FOLDER.toImageIcon(IconSize.BUTTON));
    	} else if (object instanceof BoltPluginPrototype) {
        	BoltPluginPrototype<? extends BoltPlugin> plugin = (BoltPluginPrototype<? extends BoltPlugin>)object;
        	label.setText(plugin.getName());
        	label.setIcon(getIcon(plugin));	
    	} else if (object instanceof BoltIssue) {
    		BoltIssue issue = (BoltIssue) object;
    		label.setText(issue.shortSource());
    		label.setIcon(StockIcon.BADGE_ERROR.toImageIcon(IconSize.BUTTON));
    	}
		
	}
	
	private ImageIcon getIcon(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == JavaDataSourcePlugin.class) {
			return StockIcon.DOCUMENT_IMPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == JavaDataSinkPlugin.class) {
			return StockIcon.DOCUMENT_EXPORT.toImageIcon(IconSize.BUTTON);
		}
		
		if (pluginBaseClass == JavaFilterPlugin.class) {
			return StockIcon.MISC_PLUGIN.toImageIcon(IconSize.BUTTON);
		}

		if (pluginBaseClass == JavaMapFilterPlugin.class) {
			return StockIcon.MISC_PLUGIN.toImageIcon(IconSize.BUTTON);
		}
		
		return IconFactory.getImageIcon(plugin.getManager().getAssetPath() + "/icons/", plugin.getManager().getName(), IconSize.BUTTON);
		
	}

}
