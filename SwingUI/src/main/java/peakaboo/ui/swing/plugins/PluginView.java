package peakaboo.ui.swing.plugins;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.apache.batik.ext.swing.GridBagConstants;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;
import peakaboo.calibration.CalibrationReference;
import peakaboo.datasink.plugin.JavaDataSinkPlugin;
import peakaboo.datasource.plugin.JavaDataSourcePlugin;
import peakaboo.filter.plugins.JavaFilterPlugin;
import peakaboo.mapping.filter.plugin.JavaMapFilterPlugin;
import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.layout.PropertyPanel;
import swidget.widgets.layout.TitledPanel;

public class PluginView extends JPanel {
	
	private JLabel description;
	
	public PluginView(BoltPluginPrototype<? extends BoltPlugin> plugin) {
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
		
		
		String source = "Built-In";
		if (plugin.getSource() != null) {
			source = plugin.getSource().toString();
		}
		
		Map<String, String> properties = new HashMap<>();
		properties.put("Version", plugin.getVersion());
		properties.put("Enabled", "" + plugin.isEnabled());
		properties.put("Source", source);
		properties.put("UUID", plugin.getUUID());
		TitledPanel propertyPanel = new TitledPanel(new PropertyPanel(properties), plugin.getName(), true);
		propertyPanel.setBadge(getIcon(plugin));
		
		
		description = new JLabel();
		description.setText(Swidget.lineWrapHTML(description, plugin.getDescription(), 420));
		description.setVerticalAlignment(SwingConstants.TOP);
		JScrollPane scroller = new JScrollPane(description);
		scroller.setBorder(Spacing.bLarge());
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				
		this.add(propertyPanel, c);

		c.gridy++;
		c.weighty=1f;
		c.fill = GridBagConstraints.BOTH;
		this.add(scroller, c);
		
		
	}

	private ImageIcon getIcon(BoltPluginPrototype<? extends BoltPlugin> plugin) {
		Class<? extends BoltPlugin> pluginBaseClass = plugin.getPluginClass();
		
		if (pluginBaseClass == JavaDataSourcePlugin.class) {
			return StockIcon.DOCUMENT_IMPORT.toImageIcon(IconSize.ICON);
		}
		
		if (pluginBaseClass == JavaDataSinkPlugin.class) {
			return StockIcon.DOCUMENT_EXPORT.toImageIcon(IconSize.ICON);
		}
		
		if (pluginBaseClass == JavaFilterPlugin.class) {
			return StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.ICON);
		}
		
		if (pluginBaseClass == JavaMapFilterPlugin.class) {
			return StockIcon.MISC_EXECUTABLE.toImageIcon(IconSize.ICON);
		}
				
		if (pluginBaseClass == CalibrationReference.class) {
			return IconFactory.getImageIcon("calibration", IconSize.ICON);
		}
		
		return null;
	}
	
}
