package org.peakaboo.ui.swing.mapping.sidebar.filters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.components.stencil.Stencil;
import org.peakaboo.mapping.filter.model.MapFilter;

class MapFilterWidget extends Stencil<MapFilter> {

	private JLabel label = new JLabel();
	
	public MapFilterWidget() {
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setBorder(Spacing.bHuge());
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		if (label == null) { return; }
		label.setForeground(c);
	}
	
	@Override
	protected void onSetValue(MapFilter filter, boolean selected) {
		label.setText(filter.getFilterName());
	}
	
}