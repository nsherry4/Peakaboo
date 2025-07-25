package org.peakaboo.ui.swing.plugins.manager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.apache.batik.ext.swing.GridBagConstants;
import org.peakaboo.dataset.sink.plugin.DataSinkPlugin;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.StratusText;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.PropertyPanel;
import org.peakaboo.framework.stratus.components.panels.TitledPanel;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;

class PluginView extends JPanel {
	
	public PluginView(PluginDescriptor<BoltPlugin> plugin) {
		super(new GridBagLayout());
		setBorder(Spacing.bHuge());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstants.HORIZONTAL;
		c.anchor = GridBagConstants.NORTHWEST;
		c.weightx = 1f;
		c.weighty = 0f;
		c.gridx = 0;
		c.gridy = 0;
		
		c.ipadx = Spacing.large;
		c.ipady = Spacing.large;

		Map<String, String> properties = new HashMap<>();
		properties.put("Version", plugin.getVersion());
		properties.put("Enabled", "" + plugin.isEnabled());
		properties.put("Source", plugin.getContainer().getSourcePath());
		properties.put("UUID", plugin.getUUID());
		TitledPanel propertyPanel = new TitledPanel(new PropertyPanel(properties), plugin.getName(), true);
		propertyPanel.setBadge(getIcon(plugin));
		
		
		JLabel description = new JLabel();
		description.setText(StratusText.lineWrapHTML(description, plugin.getDescription(), 420));
		description.setVerticalAlignment(SwingConstants.TOP);
		JScrollPane scroller = new JScrollPane(description);
		scroller.setBorder(Spacing.bLarge());
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				
		this.add(propertyPanel, c);

		c.gridy++;
		c.weighty=1f;
		c.fill = GridBagConstraints.BOTH;
		this.add(scroller, c);
		
		
	}

	private ImageIcon getIcon(PluginDescriptor<BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == DataSourcePlugin.class) {
			return StockIcon.DOCUMENT_IMPORT.toImageIcon(IconSize.ICON);
		}
		
		if (pluginBaseClass == DataSinkPlugin.class) {
			return StockIcon.DOCUMENT_EXPORT.toImageIcon(IconSize.ICON);
		}
		
		if (pluginBaseClass == Filter.class) {
			return StockIcon.MISC_PLUGIN.toImageIcon(IconSize.ICON);
		}
		
		if (pluginBaseClass == MapFilterPlugin.class) {
			return StockIcon.MISC_PLUGIN.toImageIcon(IconSize.ICON);
		}
		
		return IconFactory.getImageIcon(plugin.getRegistry().getAssetPath() + "/icons/", plugin.getRegistry().getSlug(), IconSize.ICON);

	}
	
}
