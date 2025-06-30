package org.peakaboo.ui.swing.plugins.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.api.icons.IconSet;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip.Alignment;

class PluginRepositoryListItemStencil extends Stencil<PluginMetadata> {
    private JLabel nameLabel;
    private StatusBarPillStrip pills;
    private KeyValuePill pillVersion, pillCategory, pillRepository;
    private JLabel descriptionArea;
    private FluentButton actionButton;
    private JComponent separator;
    
    private PluginMetadata plugin;

    // Get the registry
    private DataSourceRegistry reg = DataSourceRegistry.system();
    
    public PluginRepositoryListItemStencil(Consumer<PluginMetadata> action) {
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
        setPluginIcon(nameLabel.getForeground());
        
        pillVersion = new KeyValuePill("Version", 3);
        pillCategory = new KeyValuePill("Kind", 10);
        pillRepository = new KeyValuePill("Source", 20);
        
        pills = new StatusBarPillStrip(Alignment.LEFT);
        pills.addPills(pillCategory, pillRepository, pillVersion);
        pills.setPreferredSize(new Dimension(0, 24));
        pills.setOpaque(false);
        
        descriptionArea = new JLabel();
        descriptionArea.setBorder(new EmptyBorder(0, Spacing.medium, 0, 0));
        descriptionArea.setForeground(StratusColour.moreTransparent(descriptionArea.getForeground(), 0.25f));
        descriptionArea.setVerticalAlignment(SwingConstants.TOP);
                
        
        actionButton = new FluentButton().withIcon(StockIcon.GO_BOTTOM, IconSize.TOOLBAR_SMALL);
        actionButton.withBordered(false);
        actionButton.setFocusable(false);
        
        separator = new LineSeparator();

        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 0; c.ipady = Spacing.small;
        c.insets = new Insets(Spacing.medium, 0, Spacing.medium, Spacing.medium);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE; c.weightx = 0.0;
        add(nameLabel, c);

        c.ipadx = Spacing.small; c.ipady = Spacing.small;
        c.insets = Spacing.iMedium();
        c.gridx = 1; c.gridy = 0; c.gridheight = 1; c.weightx = 0; c.weighty = 0; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        add(actionButton, c);
        
        c.gridx = 0;
        c.gridy++;
        c.weightx = 1.0; c.weighty = 0.0; c.gridwidth = 2; c.insets = new Insets(0, 0, 0, 0); c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.WEST;
        add(pills, c);
        
        c.gridy++;
        c.weightx = 1.0; c.weighty = 1.0; c.fill = GridBagConstraints.BOTH; c.anchor = GridBagConstraints.NORTHWEST; c.insets = new Insets(0, 0, 0, 0);
        add(descriptionArea, c);

        //setPreferredSize(new Dimension(400, 90));
        
        actionButton.withAction(() -> {
            action.accept(plugin);
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
            actionButton.setEnabled(false);
            return;
        }
        nameLabel.setText(value.name != null ? value.name : "<No Name>");
        setPluginIcon();
        
        // Check if the plugin is installed locally. This will determine the action we take.
        boolean alreadyInstalled = reg.hasUUID(value.uuid);
        actionButton.withIcon(alreadyInstalled ? StockIcon.EDIT_DELETE : StockIcon.GO_BOTTOM, nameLabel.getForeground());
        actionButton.withTooltip(alreadyInstalled ? "Remove Plugin" : "Download Plugin");
        
                
        // Now lets create some KeyValuePills
        pillVersion.setValue(value.version);
        pillCategory.setValue(value.category != null ? value.category : "<Unknown>");
        pillRepository.setValue(value.repositoryName != null ? value.repositoryName : "<Unknown Repository>");
        descriptionArea.setText(value.description != null ? value.description : "");
        actionButton.setEnabled(value.downloadUrl != null && !value.downloadUrl.isBlank());

    }

	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (nameLabel != null) {
			nameLabel.setForeground(c);
			setPluginIcon();
		}
		if (pillVersion != null) pillVersion.setForeground(c);
		if (pillCategory != null) pillCategory.setForeground(c);
		if (pillRepository != null) pillRepository.setForeground(c);
		if (separator != null) separator.setForeground(StratusColour.moreTransparent(c, 0.7f));		
		if (descriptionArea != null) descriptionArea.setForeground(StratusColour.moreTransparent(c, 0.25f));
		if (actionButton != null) {
			var config = actionButton.getComponentConfig();
			actionButton.withIcon(config.imagepath, config.imagename, IconSize.BUTTON, c);
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
	
    public JButton getDownloadButton() {
        return actionButton;
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
		return StockIcon.BADGE_ERROR;

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