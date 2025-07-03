package org.peakaboo.ui.swing.plugins.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourcePlugin;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
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
    private FluentButton downloadButton, removeButton, upgradeButton;
    private JComponent separator;
    
    private static final ImageIcon PLUGIN_STATUS_HEALTHY = StockIcon.CHOOSE_OK.toImageIcon(IconSize.BUTTON);
    private static final ImageIcon PLUGIN_STATUS_UNHEALTHY = StockIcon.CHOOSE_CANCEL.toImageIcon(IconSize.BUTTON);
    
    private PluginMetadata plugin;
    private PluginsController controller;

    // Get the registry
    private DataSourceRegistry reg = DataSourceRegistry.system();
    
    public PluginRepositoryListItemStencil(
    		PluginsController controller,
    		Consumer<PluginMetadata> downloadAction,
    		Consumer<PluginMetadata> removeAction,
    		Consumer<PluginMetadata> upgradeAction
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
        nameLabel.setBorder(new EmptyBorder(0, Spacing.medium, 0, 0));
        nameLabel.setIcon(getStockIcon("").toImageIcon(IconSize.BUTTON));
        setPluginIcon();
        
        pillVersion = new KeyValuePill("Version", 3);
        pillCategory = new KeyValuePill("Kind", 1);
        pillRepository = new KeyValuePill("Source", 1);
        pillStatus = new JLabel();
        pillStatus.setFont(pillStatus.getFont().deriveFont(Font.BOLD, 12f));
        pillStatus.setBorder(new EmptyBorder(0, Spacing.huge, 0, Spacing.huge));


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
        		.withIcon(StockIcon.GO_UP, IconSize.BUTTON)
        		.withText("Upgrade")
        		.withTooltip("Upgrade Plugin")
        		.withBordered(true);
        upgradeButton.setFocusable(false);
        
        
        ComponentStrip strip = new ComponentStrip(pillStatus, downloadButton, removeButton, upgradeButton);

        separator = new LineSeparator();


        GridBagConstraints c = new GridBagConstraints();
        
        // ROW 1
        // Title
        c.gridy = 0; c.gridx = 0; 
        c.ipadx = 0; c.ipady = Spacing.small;
        c.insets = new Insets(Spacing.medium, 0, Spacing.medium, Spacing.medium);
        c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE; c.weightx = 0.0; c.anchor = GridBagConstraints.WEST;
        add(nameLabel, c);

        // Action Buttons
        c.gridy = 0; c.gridx = 1;
        c.ipadx = Spacing.small; c.ipady = Spacing.small;
        c.insets = Spacing.iMedium();
        c.weightx = 0; c.weighty = 0; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        add(strip, c);
        
        
        // ROW 2
        // Info Pills
        c.gridy = 1; c.gridx = 0;
        c.weightx = 1.0; c.weighty = 0.0; c.insets = new Insets(0, 0, 0, 0); c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.WEST;
        add(pills, c);
        
        // Health Status Pill
        c.gridy = 1; c.gridx = 1;
        c.ipadx = Spacing.small; c.ipady = Spacing.small;
        c.insets = Spacing.iMedium();
        c.weightx = 0; c.weighty = 0; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        //add(pillStatus, c);
        
        
        // ROW 3
        // Description
        c.gridy = 2; c.gridx = 0;
        c.weightx = 1.0; c.weighty = 1.0; c.gridwidth = 2; c.fill = GridBagConstraints.BOTH; c.anchor = GridBagConstraints.NORTHWEST; c.insets = new Insets(0, 0, 0, 0);
        add(descriptionArea, c);

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
        

        
    }

    @Override
    protected void onSetValue(PluginMetadata value, boolean selected) {
    	this.plugin = value;
        if (value == null) {
            nameLabel.setText("");
            pillVersion.setValue("?");
            pillCategory.setValue("<Unknown>");
            pillRepository.setValue("<Unknown Repository>");
            descriptionArea.setText("");
            downloadButton.setVisible(false);
            removeButton.setVisible(false);
            upgradeButton.setVisible(false);
            return;
        }
        nameLabel.setText(value.name != null ? value.name : "<No Name>");
        setPluginIcon();
        
        // Check if the plugin is installed locally. This will determine the action we take.
        boolean alreadyInstalled = reg.hasUUID(value.uuid);
        boolean removable = true;
        boolean upgradable = false;
        if (alreadyInstalled) {
            var descriptor = reg.getByUUID(value.uuid).get();
            var container = descriptor.getContainer();
            removable &= container.isDeletable();
            upgradable = removable && value.isUpgradeFor(descriptor);
        }
                
        // Now lets create some KeyValuePills
        controller.getRepositoryForPlugin(value).ifPresentOrElse(
        		repo -> pillRepository.setValue(repo.getRepositoryName()),
        		() -> pillRepository.setValue("<Unknown Repository>")
        	);

        pillVersion.setValue(value.version);
        pillCategory.setValue(value.category != null ? value.category : "<Unknown>");
        descriptionArea.setText(value.description != null ? value.description : "");

        // Action button enablement logic
        downloadButton.setVisible(!alreadyInstalled && value.downloadUrl != null && !value.downloadUrl.isBlank());
        removeButton.setVisible(alreadyInstalled && removable);
        upgradeButton.setVisible(upgradable); // Placeholder, implement actual upgrade logic

        
        // TODO replace this with a generic version which goes through all registries
        DataSourceRegistry registry = DataSourceRegistry.system();
        Optional<PluginDescriptor<DataSourcePlugin>> maybePlugin = value.lookupPluginDescriptor(registry);
        boolean healthy = maybePlugin.isPresent() && maybePlugin.get().getContainer().isHealthy();
        pillStatus.setText(healthy ? "Healthy" : "Unhealthy");
        pillStatus.setIcon(healthy ? PLUGIN_STATUS_HEALTHY : PLUGIN_STATUS_UNHEALTHY);
        pillStatus.setVisible(alreadyInstalled);

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
		if (separator != null) separator.setForeground(StratusColour.moreTransparent(c, 0.7f));		
		if (descriptionArea != null) descriptionArea.setForeground(StratusColour.moreTransparent(c, 0.25f));
		for (var button : new FluentButton[] {downloadButton, removeButton, upgradeButton}) {
			if (button != null) {
				var config = button.getComponentConfig();
				button.setForeground(c);
				button.withIcon(config.imagepath, config.imagename, IconSize.BUTTON, c);
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

    // Custom separator that always paints a horizontal line
    private static class LineSeparator extends javax.swing.JComponent {
    	public LineSeparator() {
    		super();
    		this.setOpaque(false);
		}
        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            g.setColor(getForeground() != null ? getForeground() : java.awt.Color.BLACK);
            int y = getHeight() / 2;
            g.drawLine(0, y, getWidth(), y);
        }
        @Override
        public Dimension getPreferredSize() {
            return new java.awt.Dimension(1000, 24); // reasonable default
        }
        @Override
        public Dimension getMinimumSize() {
            return new java.awt.Dimension(1, 24); // allow shrinking
        }
    }
}