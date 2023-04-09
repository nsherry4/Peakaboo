package org.peakaboo.ui.swing.mapping.sidebar.filters;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.mapper.filtering.MapFilteringController;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.ui.swing.app.PeakabooIcons;

class MapFilterSettingsButton extends Stencil<MapFilter> {
	
	private FluentButton button = new FluentButton(PeakabooIcons.MENU_SETTINGS, IconSize.BUTTON);
	private MapFilter filter;
	
	private ImageIcon imgEdit, imgEditSel;
	
	public MapFilterSettingsButton(MapFilteringController controller, FiltersPanel window) {
		
		setLayout(new BorderLayout());
		add(button, BorderLayout.CENTER);
		
		imgEdit = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON);
		
		button.withBordered(false);
		button.setOpaque(false);
		button.withBorder(new EmptyBorder(0, 0, 0, 0));
				
		button.withAction(() -> {
			window.showSettingsPane(filter);
			getListWidgetParent().editingStopped();
		});
	}
	
	@Override
	protected void onSetValue(MapFilter filter, boolean selected) {
		this.filter = filter;
		button.setVisible(!filter.getParameters().isEmpty());
		if (selected) {
			if (imgEditSel == null) {
				imgEditSel = PeakabooIcons.MENU_SETTINGS.toImageIcon(IconSize.BUTTON, getForeground());
			}
			button.setIcon(imgEditSel);
		} else {
			button.setIcon(imgEdit);
		}
	}
	
}