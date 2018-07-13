package peakaboo.ui.swing.plugins;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.batik.ext.swing.GridBagConstants;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import swidget.widgets.Spacing;
import swidget.widgets.TextWrapping;
import swidget.widgets.properties.PropertyViewPanel;

public class PluginView extends JPanel {
	
	private JLabel description;
	
	public PluginView(BoltPluginController<? extends BoltPlugin> plugin) {
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
		
		
		String source = "Unknown";
		if (plugin.getSource() != null) {
			source = plugin.getSource().toString();
		}
		
		Map<String, String> properties = new HashMap<>();
		properties.put("Version", plugin.getVersion());
		properties.put("Enabled", "" + plugin.isEnabled());
		properties.put("Type", plugin.getPluginClass().getSimpleName());
		properties.put("Source", source);
		PropertyViewPanel propertyPanel = new PropertyViewPanel(properties, plugin.getName(), true);
		
		
		description = new JLabel(TextWrapping.wrapTextForMultiline(plugin.getDescription(), 450));
		
		
		this.add(propertyPanel, c);

		c.gridy++;
		c.weighty=1f;
		this.add(description, c);
		
		
	}

}
