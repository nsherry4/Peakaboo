package org.peakaboo.ui.swing.app.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;

public class StatusBarPillStrip extends ClearPanel {

	private JPanel innerPanel;
	
	public enum Alignment {
		LEFT, CENTER, RIGHT
	}
	
	public StatusBarPillStrip() {
		this(Alignment.CENTER);
	}
	
	public StatusBarPillStrip(Alignment alignment) {
		
		innerPanel = new ClearPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
		
		setLayout(new LayoutManager() {
			
			@Override
			public void removeLayoutComponent(Component arg0) { /* NOOP */ }
			
			@Override
			public Dimension preferredLayoutSize(Container c) {
				return innerPanel.getPreferredSize();
			}
			
			@Override
			public Dimension minimumLayoutSize(Container c) {
				return innerPanel.getMinimumSize();
			}
			
			@Override
			public void layoutContainer(Container c) {
				int w = c.getWidth();
				int h = c.getHeight();
				int pw = innerPanel.getPreferredSize().width;
				int margin = (int)Math.max(0, (w - pw) / 2f);
				switch (alignment) {
					case LEFT -> margin = 0;
					case CENTER -> margin = margin;
					case RIGHT -> margin *= 2;
				}
				innerPanel.setBounds(margin, 0, pw, h);
			}
			
			@Override
			public void addLayoutComponent(String arg0, Component arg1) { /* NOOP */ }
		});
		
		this.add(innerPanel);
		
	}
	
	@Override
	public Dimension getPreferredSize() {
		var inner = innerPanel.getPreferredSize();
		return new Dimension(inner.width, Math.max(24, inner.height));
	}
	
	@Override
	public Dimension getMinimumSize() {
		var inner = innerPanel.getPreferredSize();
		return new Dimension(inner.width, inner.height);
	}
	
	
	public void addPill(KeyValuePill pill) {
		innerPanel.add(pill);
	}
	
	public void addPills(KeyValuePill... pills) {
		for (var pill : pills) {
			innerPanel.add(pill);
		}
	}
	
}
