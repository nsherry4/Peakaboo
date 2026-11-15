package org.peakaboo.framework.stratus.components.ui.itemlist;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderBox;



public abstract class SelectionListControls extends HeaderBox
{
	
	public SelectionListControls(String name, String title){
		
		super();
		
		FluentButton add = new FluentButton()
				.withIcon(StockIcon.CHOOSE_OK)
				.withTooltip("Add Selected " + name)
				.withBordered(false)
				.withAction(() -> approve());
		
		FluentButton cancel = new FluentButton()
				.withIcon(StockIcon.CHOOSE_CANCEL)
				.withTooltip("Discard Selections")
				.withBordered(false)
				.withAction(() -> cancel());

		setBorder(Spacing.bSmall());
		
		setDraggable(false);
		setComponents(cancel, title, add);

	}
	
	/** Double-click on a leaf approves it as the chosen selection */
	public void approveOnDoubleClick(JTree tree) {
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e) || e.getClickCount() != 2) { return; }

				// Non-leaf nodes already expand/collapse on double-click, so leave those alone
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if (path == null || !tree.getModel().isLeaf(path.getLastPathComponent())) { return; }

				tree.setSelectionPath(path);
				approve();
			}
		});
	}

	protected abstract void approve();
	protected abstract void cancel();
	
	@Override
	public void paintComponent(Graphics g) {}
	
}
