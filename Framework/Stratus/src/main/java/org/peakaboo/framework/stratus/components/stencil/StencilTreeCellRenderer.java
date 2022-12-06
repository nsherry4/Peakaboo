package org.peakaboo.framework.stratus.components.stencil;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.peakaboo.framework.stratus.api.Stratus;

public class StencilTreeCellRenderer<T> implements TreeCellRenderer, StencilParent {

	private Stencil<T> widget;
	private Color selBg, selFg, nonBg, nonFg;
	
	public StencilTreeCellRenderer(Stencil<T> widget) {
		this.widget = widget;
		widget.setParent(this);
		
		var def = new DefaultTreeCellRenderer();
		selFg = Stratus.getTheme().getHighlightText();
		selBg = Stratus.getTheme().getHighlight();
		nonFg = Stratus.getTheme().getRecessedText();
		nonBg = Stratus.getTheme().getRecessedControl();
	}
	
	@Override
	public void editingStopped() {
		// NOOP
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object treenode, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		
		
		
		if (selected) {
			widget.setForeground(selFg);
			widget.setBackground(selBg);
			widget.setOpaque(true);
		} else {
			widget.setForeground(nonFg);
			widget.setBackground(nonBg);
			widget.setOpaque(false);
		}
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treenode;
    	T value = (T) node.getUserObject();
		
		widget.setValue((T) value, selected);
				
		return widget;
		
	}

}
