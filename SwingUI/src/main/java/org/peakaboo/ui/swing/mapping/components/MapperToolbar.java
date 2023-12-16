package org.peakaboo.ui.swing.mapping.components;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToolbarButton;
import org.peakaboo.tier.Tier;
import org.peakaboo.tier.TierUIAction;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.app.TierWidgetFactory;
import org.peakaboo.ui.swing.mapping.MapperPanel;

public class MapperToolbar extends JToolBar {

	public static final String TIER_LOCATION = "map.toolbar";
	private final List<TierUIAction<MapperPanel, MappingController>> tierItems = Tier.provider().uiComponents(TIER_LOCATION);
	
	private FluentToolbarButton	showConcentrations, examineSubset;

	public MapperToolbar(MapperPanel panel, MappingController controller) {


		this.setFloatable(false);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2, 2, 2, 2);
		
		FluentToolbarButton export = createExportMenuButton(panel);
		this.add(export, c);
		c.gridx++;
		
		this.add(new JToolBar.Separator( null ), c);
		c.gridx++;
		
		
		for (TierUIAction<MapperPanel, MappingController> item : tierItems) {
			var component = TierWidgetFactory.toolbarButton(item, panel, controller);
			this.add(component, c);
			c.gridx++;
			component.setEnabled(item.enabled.apply(controller));
			item.component = component;
		}
		
		
		examineSubset = new PlotSelectionButton(IconSize.TOOLBAR_SMALL, controller, panel.getParentPlotter());
		this.add(examineSubset, c);
		c.gridx++;
		examineSubset.setEnabled(false);
		
		c.weightx = 1.0;
		this.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;
		c.gridx++;
		
		this.add(createOptionsButton(controller), c);
		c.gridx++;
		
		
		
		
		
		controller.addListener(t -> {
			examineSubset.setEnabled(controller.getSelection().isReplottable());
			
			for (TierUIAction<MapperPanel, MappingController> item : tierItems) {
				JComponent component = (JComponent) item.component;
				component.setEnabled(item.enabled.apply(controller));
			}
			
		});
		
		
		
	}
	

	public static FluentToolbarButton createOptionsButton(MappingController controller) {
		
		JPopupMenu menu = new MapMenuView(controller);
		
		FluentToolbarButton opts = new FluentToolbarButton()
				.withIcon(PeakabooIcons.MENU_VIEW)
				.withTooltip("Map Settings Menu");
		
		opts.withAction(() -> {
			int x = (int)(opts.getWidth() - menu.getPreferredSize().getWidth());
			int y = opts.getHeight();
			menu.show(opts, x, y);
		});
		
		return opts;
	}
	
	
	private FluentToolbarButton createExportMenuButton(MapperPanel panel) {
		FluentToolbarButton exportMenuButton = new FluentToolbarButton().withIcon(StockIcon.DOCUMENT_EXPORT).withTooltip("Export Maps");
		JPopupMenu exportMenu = new MapMenuExport(panel);
		exportMenuButton.withAction(() -> exportMenu.show(exportMenuButton, 0, exportMenuButton.getHeight()));
		return exportMenuButton;
	}
	
}
