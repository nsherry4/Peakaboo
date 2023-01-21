package org.peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.StratusColour;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;



class LookupRenderer extends DefaultTreeCellRenderer {

	private FittingController	controller;
	
	private ElementRenderer elementWidget;
	private TSRenderer tsWidget;
	
	public LookupRenderer(FittingController controller)	{
		this.controller = controller;
		tsWidget = new TSRenderer(false);
		elementWidget = new ElementRenderer(false);
	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof ITransitionSeries) {
			ITransitionSeries ts = (ITransitionSeries) value;
			tsWidget.setTransitionSeries(ts);
			tsWidget.setSelected(selected);
			tsWidget.check.setSelected(controller.getProposedTransitionSeries().contains(ts));
			return tsWidget;
		} else if (value instanceof Element) {
			elementWidget.setElement((Element) value);
			elementWidget.setSelected(selected);
			return elementWidget;
		}
		
		return c;

	}
	
	static class ElementRenderer extends ClearPanel {

		private JLabel symbol, name;
		private boolean editor = false;
		
		public ElementRenderer(boolean editor) {
			this.editor = editor;
			
			symbol = new JLabel() {
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(28, super.getPreferredSize().height);
				}
			};
			symbol.setHorizontalAlignment(JLabel.RIGHT);
			symbol.setBorder(new EmptyBorder(0, 0, 0, Spacing.small));
			symbol.setFont(symbol.getFont().deriveFont(Font.BOLD));
			
			name = new JLabel();
			
			add(symbol);
			add(name);
			
			
			
		}
		
		public void setElement(Element e) {
			symbol.setText(e.name());
			name.setText(e.toString());
		}
		
		public void setSelected(boolean selected) {
			Color colour;
			if (selected || editor) {
				colour = Stratus.getTheme().getHighlightText();
			} else {
				colour = Stratus.getTheme().getControlText();
			}
			if (symbol != null) symbol.setForeground(colour);
			if (name != null) name.setForeground(colour);
		}

		
		
	}
	
	static class TSRenderer extends ClearPanel {

		private JLabel name;
		JCheckBox check;
		private boolean editor = false;
		
		public TSRenderer(boolean editor) {
			this.editor = editor;
			setLayout(new FlowLayout(WEST, Spacing.small, Spacing.small));
			name = new JLabel();
			name.setBorder(new EmptyBorder(0, Spacing.small, 0, 0));
			check = new JCheckBox();
			add(check);
			add(name);
			
			setBorder(Spacing.bTiny());
			
		}
		
		public void setTransitionSeries(ITransitionSeries ts) {
			name.setText(ts.getShell().toString());
		}
		
		public void setSelected(boolean selected) {
			Color colour;
			if (selected || editor) {
				colour = Stratus.getTheme().getHighlightText();
			} else {
				colour = Stratus.getTheme().getControlText();
			}
			if (name != null) name.setForeground(colour);
		}

		
	}

}
