package peakaboo.ui.swing.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginController;
import swidget.icons.IconFactory;
import swidget.icons.IconSize;
import swidget.widgets.ClearPanel;
import swidget.widgets.TextWrapping;

public class PluginView extends ClearPanel {
	
	private JLabel name;
	private JLabel version;
	private JLabel description;
	private JLabel enabled;
	private JLabel type;
	
	public PluginView(BoltPluginController<? extends BoltPlugin> plugin) {
		super(new BorderLayout());
		
		this.setMinimumSize(new Dimension(500, 100));
		
		setOpaque(true);
		setBackground(Color.WHITE);
		setBorder(new LineBorder(Color.GRAY));
		
		String source = "Unknown";
		if (plugin.getSource() != null) {
			source = plugin.getSource().toString();
		}
		
		name = new JLabel(plugin.getName());
		version = new JLabel(plugin.getVersion());
		description = new JLabel(TextWrapping.wrapTextForMultiline(plugin.getDescription() + "<br/><small>From: " + source + "</small>", 375));
		enabled = new JLabel(new ImageIcon(IconFactory.getImageIcon(plugin.isEnabled() ? "choose-ok" : "choose-cancel", IconSize.BUTTON).getImage()));
		type = new JLabel(TextWrapping.wrapTextForMultiline(plugin.getPluginClass().getSimpleName(), 375));
		
		
		name.setBorder(new EmptyBorder(10, 10, 20, 10));
		name.setBorder(new EmptyBorder(5, 5, 5, 5));
		version.setBorder(new EmptyBorder(5, 5, 5, 5));
		description.setBorder(new EmptyBorder(5, 5, 5, 5));
		enabled.setBorder(new EmptyBorder(5, 5, 5, 5));
		type.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		name.setFont(name.getFont().deriveFont(20f));
		description.setForeground(Color.GRAY);
		version.setForeground(Color.GRAY);
		type.setForeground(Color.GRAY);
		
		
		this.add(name, BorderLayout.CENTER);
		this.add(version, BorderLayout.EAST);
		this.add(description, BorderLayout.SOUTH);
		this.add(enabled, BorderLayout.WEST);
		this.add(type, BorderLayout.NORTH);
		
		
	}

}
