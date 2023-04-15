package org.peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.ui.swing.plotting.fitting.lookup.LookupRenderer.ElementRenderer;
import org.peakaboo.ui.swing.plotting.fitting.lookup.LookupRenderer.TSRenderer;



class LookupEditor extends DefaultTreeCellEditor {

	private FittingController			controller;
	private DefaultTreeCellRenderer		cellRenderer;
	
	private ElementRenderer elementWidget;
	private TSRenderer tsWidget;
	

	public LookupEditor(JTree tree, DefaultTreeCellRenderer renderer, FittingController controller) {

		super(tree, renderer);

		this.cellRenderer = renderer;
		this.controller = controller;

		elementWidget = new ElementRenderer(true);
		tsWidget = new TSRenderer(true);
		tsWidget.check.addItemListener(e -> stopCellEditing());
		
	}


	@Override
	public Component getTreeCellEditorComponent(
			JTree tree, 
			Object value, 
			boolean selected, 
			boolean expanded,
			boolean leaf, 
			int row) {

		Component c = super.getTreeCellEditorComponent(tree, value, selected, expanded, leaf, row);

		if (value instanceof ITransitionSeries ts) {
			tsWidget.setTransitionSeries(ts);
			tsWidget.setSelected(selected);
			tsWidget.check.setSelected(controller.getProposedTransitionSeries().contains(ts));
			return tsWidget;

		} else if (value instanceof Element element) {
			elementWidget.setElement(element);
			elementWidget.setSelected(selected);
			return elementWidget;

		}



		return c;

	}


	@Override
	public Object getCellEditorValue() {
		return tsWidget.check.isSelected();
	}


	@Override
	public boolean isCellEditable(EventObject e) {
		Object selected = tree.getLastSelectedPathComponent();

		if (selected == null) return false;
		if (selected instanceof ITransitionSeries) return true;

		return false;
	}

}
