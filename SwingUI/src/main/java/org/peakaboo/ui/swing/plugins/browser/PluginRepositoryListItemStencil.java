package org.peakaboo.ui.swing.plugins.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.jdesktop.swingx.graphics.ColorUtilities;
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltPluginIssue;
import org.peakaboo.framework.bolt.repository.IssuePluginRepository;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.api.icons.IconSet;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip.Alignment;
import org.peakaboo.ui.swing.plugins.PluginsController;

class PluginRepositoryListItemStencil extends Stencil<PluginMetadata> {
    private JLabel nameLabel;
    private StatusBarPillStrip pills;
    private KeyValuePill pillVersion, pillCategory, pillRepository;
    private JLabel pillStatus;
    private JLabel descriptionArea;
    private FluentButton downloadButton, removeButton, upgradeButton, fixButton;
    
    private static final ImageIcon PLUGIN_STATUS_HEALTHY = StockIcon.CHOOSE_OK.toImageIcon(IconSize.BUTTON);
    private static final ImageIcon PLUGIN_STATUS_UNHEALTHY = StockIcon.CHOOSE_CANCEL.toImageIcon(IconSize.BUTTON);
    
    private PluginMetadata plugin;
    private BoltIssue<? extends BoltPlugin> issue;
    private PluginsController controller;
    
    private static final Map<String, Color> colorCache = new HashMap<>();

    private ExtensionPointRegistry reg = Tier.provider().getExtensionPoints();
    
    public PluginRepositoryListItemStencil(
    		PluginsController controller,
    		Consumer<PluginMetadata> downloadAction,
    		Consumer<PluginMetadata> removeAction,
    		Consumer<PluginMetadata> upgradeAction,
    		Consumer<BoltIssue<? extends BoltPlugin>> fixAction
    	) {
    	this.controller = controller;
    	
        setLayout(new GridBagLayout());
        setBorder(new CompoundBorder(
        		new MatteBorder(0, 0, 1, 0, Stratus.getTheme().getWidgetBorder()), 
        		Spacing.bHuge()
        	));
        setOpaque(true);

        nameLabel = new JLabel();
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));
        nameLabel.setBorder(new EmptyBorder(0, Spacing.medium, Spacing.small, 0));
        nameLabel.setIcon(getStockIcon("").toImageIcon(IconSize.BUTTON));
        setPluginIcon();
        
        pillVersion = new KeyValuePill("Version", 3);
        pillCategory = new KeyValuePill("Kind", 1);
        pillRepository = new KeyValuePill("Source", 1);
        pillStatus = new JLabel();
        pillStatus.setFont(pillStatus.getFont().deriveFont(Font.PLAIN, 12f));
        pillStatus.setBorder(new EmptyBorder(0, Spacing.small, 0, Spacing.small));
        pillStatus.setForeground(StratusColour.moreTransparent(pillStatus.getForeground(), 0.25f));


        pills = new StatusBarPillStrip(Alignment.LEFT);
        pills.addPills(pillCategory, pillRepository, pillVersion);
        pills.setPreferredSize(new Dimension(0, 24));
        pills.setOpaque(false);
        
        descriptionArea = new JLabel();
        descriptionArea.setBorder(new EmptyBorder(0, Spacing.medium, 0, 0));
        descriptionArea.setForeground(StratusColour.moreTransparent(descriptionArea.getForeground(), 0.25f));
        descriptionArea.setVerticalAlignment(SwingConstants.TOP);
                
        
        downloadButton = new FluentButton()
        		.withIcon(StockIcon.GO_BOTTOM, IconSize.BUTTON)
        		.withText("Install")
        		.withTooltip("Download and Install this Plugin")
        		.withBordered(true);
        downloadButton.setFocusable(false);
        
        removeButton = new FluentButton()
        		.withIcon(StockIcon.EDIT_DELETE, IconSize.BUTTON)
        		.withText("Remove")
        		.withTooltip("Remove Plugin")
        		.withBordered(true);
        removeButton.setFocusable(false);
        
        upgradeButton = new FluentButton()
        		.withIcon(StockIcon.GO_UP, IconSize.BUTTON, Stratus.getTheme().getHighlightText())
        		.withText("Upgrade")
        		.withTooltip("Upgrade Plugin")
        		.withStateDefault()
        		.withBordered(true);
        upgradeButton.setFocusable(false);
        
        fixButton = new FluentButton()
        		.withText("Fix")
        		.withTooltip("Attemp to Fix")
        		.withBordered(true);
        fixButton.setFocusable(false);
        
        
        
        ComponentStrip strip = new ComponentStrip(List.of(downloadButton, removeButton, upgradeButton, fixButton), true, new Insets(0, Spacing.small, 0, 0), 0);


        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = Spacing.small; c.ipady = Spacing.medium;
        
        // ROW 1
        // Title
        c.gridy = 0; c.gridx = 0;
        c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE; c.weightx = 0.0; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(Spacing.small, 0, 0, 0);
        add(nameLabel, c);
        c.insets = new Insets(0, 0, 0, 0);

        // Action Buttons
        c.gridy = 0; c.gridx = 1;
        c.weightx = 0; c.weighty = 0; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        add(strip, c);
        
        
        // ROW 2
        // Info Pills
        c.gridy = 1; c.gridx = 0;
        c.weightx = 1.0; c.weighty = 0.0; c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, Spacing.medium-1, 0, 0);
        add(pills, c);
        c.insets = new Insets(0, 0, 0, 0);
        
        // Health Status Pill
        c.gridy = 1; c.gridx = 1;
        c.weightx = 0; c.weighty = 0; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        add(pillStatus, c);
        
        
        // ROW 3
        // Description
        c.gridy = 2; c.gridx = 0;
        c.weightx = 1.0; c.weighty = 1.0; c.gridwidth = 2; c.fill = GridBagConstraints.BOTH; c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(Spacing.small, Spacing.small, 0, 0);
        add(descriptionArea, c);
        c.insets = new Insets(0, 0, 0, 0);

        //setPreferredSize(new Dimension(400, 90));

        downloadButton.withAction(() -> {
        	downloadAction.accept(plugin);
            getListWidgetParent().editingStopped();			
		});
        removeButton.withAction(() -> {
        	removeAction.accept(plugin);
            getListWidgetParent().editingStopped();			
		});
        upgradeButton.withAction(() -> {
            upgradeAction.accept(plugin);
            getListWidgetParent().editingStopped();			
		});
        fixButton.withAction(() -> {
        	fixAction.accept(issue);
        	getListWidgetParent().editingStopped();
        });
        

        
    }

    @Override
    protected void onSetValue(PluginMetadata value, boolean selected) {
    	this.plugin = value;
    	this.issue = null;
        if (value == null) {
            nameLabel.setText("");
            pillVersion.setValue("?");
            pillCategory.setValue("<Unknown>");
            pillRepository.setValue("<Unknown Repository>");
            descriptionArea.setText("");
            downloadButton.setVisible(false);
            removeButton.setVisible(false);
            upgradeButton.setVisible(false);
            fixButton.setVisible(false);
            return;
        }
        nameLabel.setText(value.name != null ? value.name : "<No Name>");
        setPluginIcon();
        
        // Check if the plugin is installed locally. This will determine the action we take.
        boolean isIssue = IssuePluginRepository.CATEGORY.equals(value.category);
        
        boolean installedPlugin = reg.hasUUID(value.uuid);
        boolean removable = true;
        boolean upgradable = false;
        PluginDescriptor<? extends BoltPlugin> descriptor = null;
        if (installedPlugin) {
            descriptor = reg.getByUUID(value.uuid).get();
            var container = descriptor.getContainer();
            removable &= container.isDeletable();
            upgradable = removable && value.isUpgradeFor(descriptor);
        } else if (isIssue) {
        	// Issues come from this type of repo
        	IssuePluginRepository issueRepo = (IssuePluginRepository) value.sourceRepository();
        	this.issue = issueRepo.getIssueForPlugin(value.uuid);
        	
        }
        
        // Now lets create some KeyValuePills
        pillRepository.setValue(value.sourceRepository().getRepositoryName());
        pillVersion.setValue(upgradable ? descriptor.getVersion() + " → " + value.version : value.version);
        pillCategory.setValue(value.category != null ? value.category : "<Unknown>");
        descriptionArea.setText(value.description != null ? value.description : "");

        pillCategory.setBackground(stringToPastelColor(pillCategory.getValue(), 0.3f));
        
        
        
        // Action button enablement logic
        downloadButton.setVisible(!installedPlugin && value.downloadUrl != null && !value.downloadUrl.isBlank());
        removeButton.setVisible(installedPlugin && removable);
        upgradeButton.setVisible(installedPlugin && upgradable);
        fixButton.setVisible(isIssue && issue.hasFix());
        fixButton.setText(isIssue ? issue.fixName() : "Fix");
        
        
        
		// Get the registry for the plugin's category and (assuming we found one) check its health. If
        // no value was found, we declare it unhealthy.
        boolean healthy = Tier.provider().getExtensionPoints().findRegistryForInterface(value.category).map(r -> {
        	return checkHealthInRegistry(r, value);
        }).orElse(false);
        // Update the status pill based on health
        pillStatus.setText(healthy ? "Healthy" : "Unhealthy");
        pillStatus.setIcon(healthy ? PLUGIN_STATUS_HEALTHY : PLUGIN_STATUS_UNHEALTHY);
        pillStatus.setFont(pillStatus.getFont().deriveFont(healthy ? Font.PLAIN : Font.BOLD));
        pillStatus.setVisible(installedPlugin);
        

    }
    
    // We can only report healthy if the plugin is registered in the registry, and if it's container has no issues
    private <T extends BoltPlugin> boolean checkHealthInRegistry(BoltPluginRegistry<T> registry, PluginMetadata value) {
		Optional<PluginDescriptor<T>> maybePlugin = value.lookupPluginDescriptor(registry);
		if (maybePlugin.isEmpty()) return false;
		PluginDescriptor<T> descriptor = maybePlugin.get();
		return descriptor.getContainer().isHealthy();
	}


	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (nameLabel != null) {
			nameLabel.setForeground(c);
			setPluginIcon();
		}
		for (var pill : new JComponent[] {pillVersion, pillCategory, pillRepository, pillStatus}) {
			if (pill != null) {
				pill.setForeground(c);
			}
		}
		if (pillStatus != null) pillStatus.setForeground(StratusColour.moreTransparent(pillStatus.getForeground(), 0.25f));
		if (descriptionArea != null) descriptionArea.setForeground(StratusColour.moreTransparent(c, 0.25f));
		for (var button : new FluentButton[] {downloadButton, removeButton, fixButton}) {
			if (button != null) {
				var config = button.getComponentConfig();
				button.setForeground(c);
				if (button.getIcon() != null) {
					button.withIcon(config.imagepath, config.imagename, IconSize.BUTTON, c);
				}
			}
		}
	}
    
	private void setPluginIcon() {
		setPluginIcon(nameLabel.getForeground());
	}
	private void setPluginIcon(Color c) {
		var category = plugin == null ? "" : plugin.category;
		var icon = getStockIcon(category);
		var imageIcon = icon.toString().toLowerCase().contains("symbolic") 
			? icon.toImageIcon(IconSize.TOOLBAR_SMALL, c) 
			: icon.toImageIcon(IconSize.TOOLBAR_SMALL);
		nameLabel.setIcon(imageIcon);
	}
    
    private IconSet getStockIcon(String kind) {
    	if (kind == null) {
    		return StockIcon.BADGE_ERROR;
    	}
    	
    	
    	if (DataSourceRegistry.system().getInterfaceName().equals(kind)) {
    		return StockIcon.DOCUMENT_IMPORT_SYMBOLIC;
    	}

    	if (DataSinkRegistry.system().getInterfaceName().equals(kind)) {
    		return StockIcon.DOCUMENT_EXPORT_SYMBOLIC;
    	}
    	
    	// Failed to find a specific icon
		return PeakabooIcons.PLUGIN_SYMBOLIC;

    }   
    
    
    /**
     * Alternative method with customizable lightness for different UI contexts.
     * 
     * @param input The string to map to a color
     * @param lightness Lightness value (0.0 to 1.0). Higher values = lighter colors
     * @return A pastel Color object
     */
    public static Color stringToPastelColor(String input, float lightness) {
        if (input == null) {
            input = "";
        }
        
        String cacheKey = input + "_" + lightness;
        if (colorCache.containsKey(cacheKey)) {
            return colorCache.get(cacheKey);
        }
        
        long hash = djb2(input);
        int steps = 180;
        float hue = Math.abs(hash % steps) / (float)steps;
        
        Color color = ColorUtilities.HSLtoRGB(hue, 1f, Math.max(0.0f, Math.min(1.0f, lightness)));
        colorCache.put(cacheKey, color);
        
        
        return color;
    }
    
    // Better hash function for strings to get better colour autoselection
    public static int djb2(String str) {
        int hash = 5381;
        for (int i = 0; i < str.length(); i++) {
            hash = ((hash << 5) + hash) + str.charAt(i);
        }
        return hash;
    }
        
}