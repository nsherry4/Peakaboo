package peakaboo.ui.swing.plotting.fitting.unfitted;



import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import peakaboo.ui.swing.widgets.Spacing;



public class ProposalRenderer extends DefaultTreeCellRenderer
{

	private TSWidget		tswidget;
	private FittingController	controller;
	private JLabel				tstLabel;


	public ProposalRenderer(FittingController controller)
	{

		this.controller = controller;

		tswidget = new TSWidget(false);
		
		
		tstLabel = new JLabel();
		tstLabel.setOpaque(false);
		tstLabel.setBorder(Spacing.bSmall());
		//tstLabel.setFont(tstLabel.getFont().deriveFont(Font.BOLD).deriveFont(tstLabel.getFont().getSize() * 1.25f));

	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{

		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		
		if (selected)
		{

			tswidget.setBackground(getBackgroundSelectionColor());
			tswidget.setForeground(getTextSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBorderSelectionColor()));

		}
		else
		{
			tswidget.setBackground(getBackgroundNonSelectionColor());
			tswidget.setForeground(getTextNonSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBackgroundNonSelectionColor()));

		}

		Element e;

		if (value instanceof TransitionSeries)
		{
			TransitionSeries ts = (TransitionSeries) value;
			e = ts.element;
			tswidget.setName(e.atomicNumber() + ": " + ts.element.name() + " (" + ts.element + ")");

			tswidget.setSelected(controller.getProposedTransitionSeries().contains(ts));
			// element.setPreferredSize(new Dimension(0, element.getPreferredSize().height));

			return tswidget;
		}
		else if (value instanceof TransitionSeriesType)
		{
			TransitionSeriesType tst = (TransitionSeriesType) value;

			tstLabel.setText(tst.toString());
			tstLabel.setForeground(c.getForeground());
			tstLabel.setBackground(c.getBackground());
			
			return tstLabel;
		}
		return c;

	}

}
