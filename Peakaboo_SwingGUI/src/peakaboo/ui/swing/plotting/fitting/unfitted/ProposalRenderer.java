package peakaboo.ui.swing.plotting.fitting.unfitted;



import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;



class ProposalRenderer extends DefaultTreeCellRenderer
{

	private TSWidget			tswidget;
	private FittingController	controller;
	private JLabel				tstLabel;
	private JPanel 				tstPanel;

	public ProposalRenderer(FittingController controller)
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
		
		
		if (selected)
		{
			
			
			
			tswidget.setBackground(getBackgroundSelectionColor());
			tswidget.setForeground(getTextSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBorderSelectionColor()));
			tswidget.setOpaque(true);

		}
		else
		{
			tswidget.setBackground(getBackgroundNonSelectionColor());
			tswidget.setForeground(getTextNonSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getBackgroundNonSelectionColor()));
			tswidget.setOpaque(false);
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
			
			tstLabel.setBackground(getBackgroundSelectionColor());
			
			if (selected)
			{
				tstLabel.setForeground(getTextSelectionColor());
				tstLabel.setOpaque(true);
			} else {
				tstLabel.setForeground(getTextNonSelectionColor());
				tstLabel.setOpaque(false);
			}
						
			return tstLabel;
		}
		
		return c;

	}

}
