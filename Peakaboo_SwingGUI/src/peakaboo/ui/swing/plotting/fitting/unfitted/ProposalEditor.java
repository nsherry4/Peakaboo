package peakaboo.ui.swing.plotting.fitting.unfitted;



import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import peakaboo.ui.swing.widgets.Spacing;



public class ProposalEditor extends DefaultTreeCellEditor
{

	private TSWidget			tswidget;
	private JLabel					tstLabel;
	private FittingController		controller;
	private DefaultTreeCellRenderer	renderer;


	public ProposalEditor(JTree tree, DefaultTreeCellRenderer renderer, FittingController controller)
	{

		super(tree, renderer);

		this.renderer = renderer;
		this.controller = controller;

		tswidget = new TSWidget(false);
		
		tstLabel = new JLabel();
		tstLabel.setOpaque(false);
		tstLabel.setBorder(Spacing.bSmall());
		//tstLabel.setFont(tstLabel.getFont().deriveFont(Font.BOLD).deriveFont(tstLabel.getFont().getSize() * 1.25f));


		tswidget.getCheckBox().addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e)
			{
				//fireEditingStopped();
				ProposalEditor.this.stopCellEditing();
			}
		});
		
	}


	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row)
	{

		Component c = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
				
		if (value instanceof TransitionSeries)
		{

			TransitionSeries ts = (TransitionSeries) value;
			Element e = ts.element;
			double intensity = controller.getTransitionSeriesIntensity(ts);
			tswidget.setName(e.atomicNumber() + ": " + ts.element.name() + " (" + ts.element + ")");

			tswidget.setBackground(renderer.getBackgroundSelectionColor());
			tswidget.setForeground(renderer.getTextSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBorderSelectionColor()));

			tswidget.setSelected(controller.getProposedTransitionSeries().contains(ts));
			return tswidget;

		}		
		else if (value instanceof TransitionSeriesType)
		{
			TransitionSeriesType tst = (TransitionSeriesType)value;
					
			//if (isSelected)
			//{
				tstLabel.setForeground(renderer.getTextSelectionColor());
			//} else {
			//	tstLabel.setForeground(renderer.getTextNonSelectionColor());
			//}

			tstLabel.setText(tst.toString());
			
			
			return tstLabel;
		}
		
		
		
		return c;

	}


	public Object getCellEditorValue()
	{
		return tswidget.isSelected();
	}


	@Override
	public boolean isCellEditable(EventObject e)
	{
		JTree source = (JTree)e.getSource();
		
		Object selected = tree.getLastSelectedPathComponent();
						
		if (selected == null) return false;
		if (selected instanceof TransitionSeriesType) return false;
		return true;
	}

}
