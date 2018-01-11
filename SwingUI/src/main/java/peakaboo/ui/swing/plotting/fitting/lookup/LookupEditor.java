package peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import peakaboo.controller.plotter.fitting.IFittingController;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesType;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.ui.swing.plotting.fitting.TSWidget;
import swidget.widgets.Spacing;



class LookupEditor extends DefaultTreeCellEditor
{

	private TSWidget	tswidget;
	private JLabel						tstLabel;
	private IFittingController			controller;
	private DefaultTreeCellRenderer		renderer;



	public LookupEditor(JTree tree, DefaultTreeCellRenderer renderer, IFittingController controller)
	{

		super(tree, renderer);

		this.renderer = renderer;
		this.controller = controller;

		tswidget = new TSWidget(false);
		tswidget.setOpaque(true);
		

		tstLabel = new JLabel();
		tstLabel.setOpaque(true);
		tstLabel.setBorder(Spacing.bTiny());

		//tstLabel.setFont(tstLabel.getFont().deriveFont(Font.BOLD).deriveFont(tstLabel.getFont().getSize() * 1.25f));


		tswidget.getCheckBox().addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e)
			{
				//fireEditingStopped();
				LookupEditor.this.stopCellEditing();
			}
		});

	}


	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row)
	{

		Component c = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);

		if (value instanceof TransitionSeries)
		{

			TransitionSeries ts = (TransitionSeries) value;
			Element e = ts.element;
			tswidget.setName(e.atomicNumber() + ": " + ts.element.name() + " (" + ts.element + ")");

			tswidget.setBackground(renderer.getBackgroundSelectionColor());
			tswidget.setForeground(renderer.getTextSelectionColor());
			tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBorderSelectionColor()));
			
			tswidget.setSelected(controller.getProposedTransitionSeries().contains(ts));
			return tswidget;

		}
		else if (value instanceof TransitionSeriesType)
		{
			TransitionSeriesType tst = (TransitionSeriesType) value;

			tstLabel.setBackground(renderer.getBackgroundSelectionColor());
			tstLabel.setForeground(renderer.getTextSelectionColor());
			
			tstLabel.setText(tst.toString());
			
			return tstLabel;
		
		}



		return c;

	}


	@Override
	public Object getCellEditorValue()
	{
		return tswidget.isSelected();
	}


	@Override
	public boolean isCellEditable(EventObject e)
	{
		Object selected = tree.getLastSelectedPathComponent();

		if (selected == null) return false;
		if (selected instanceof TransitionSeriesType) return false;
		if (selected instanceof String) return false;
		return true;
	}

}
