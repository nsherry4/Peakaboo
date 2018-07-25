package peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.fitting.FittingController;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import swidget.widgets.Spacing;



class LookupRenderer extends DefaultTreeCellRenderer
{

	private TSWidget			tswidget;
	private FittingController	controller;
	private JLabel				tstLabel;

	public LookupRenderer(FittingController controller)
	{

		this.controller = controller;

		tswidget = new TSWidget(false);
		
		tstLabel = new JLabel();
		tstLabel.setOpaque(true);
		tstLabel.setBorder(Spacing.bTiny());
		//tstLabel.setFont(tstLabel.getFont().deriveFont(Font.BOLD).deriveFont(tstLabel.getFont().getSize() * 1.25f));

	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		
		if (selected) {

			tswidget.setBackground(getBackgroundSelectionColor());
			tswidget.setForeground(getTextSelectionColor());
			tswidget.setOpaque(true);

		} else {
			tswidget.setBackground(getBackgroundNonSelectionColor());
			tswidget.setForeground(getTextNonSelectionColor());
			tswidget.setOpaque(false);
		}


		if (value instanceof TransitionSeries)
		{
			TransitionSeries ts = (TransitionSeries) value;
			tswidget.setName(ts.type.toString());

			tswidget.setSelected(controller.getProposedTransitionSeries().contains(ts));
			// element.setPreferredSize(new Dimension(0, element.getPreferredSize().height));

			tswidget.setBorder(Spacing.bTiny());
			
			return tswidget;
		}
		else if (value instanceof Element)
		{
			Element element = (Element) value;
			tstLabel.setText(element.atomicNumber() + " " + element.toString() + " (" + element.name() + ")");
			
			tstLabel.setBackground(getBackgroundSelectionColor());
			
			if (selected)
			{
				tstLabel.setForeground(getTextSelectionColor());
				tstLabel.setOpaque(true);
			} else {
				tstLabel.setForeground(getTextNonSelectionColor());
				tstLabel.setOpaque(false);
			}
			
			tstLabel.setBorder(Spacing.bSmall());
						
			return tstLabel;
		}
		
		return c;

	}

}
