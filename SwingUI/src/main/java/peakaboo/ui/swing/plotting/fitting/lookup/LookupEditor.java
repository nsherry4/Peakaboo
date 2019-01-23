package peakaboo.ui.swing.plotting.fitting.lookup;



import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.peakaboo.controller.plotter.fitting.FittingController;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

import swidget.widgets.Spacing;



class LookupEditor extends DefaultTreeCellEditor
{

	private LookupWidget	tswidget;
	private JLabel						tstLabel;
	private FittingController			controller;
	private DefaultTreeCellRenderer		cellRenderer;



	public LookupEditor(JTree tree, DefaultTreeCellRenderer renderer, FittingController controller)
	{

		super(tree, renderer);

		this.cellRenderer = renderer;
		this.controller = controller;

		tswidget = new LookupWidget();
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

		if (value instanceof ITransitionSeries)
		{

			ITransitionSeries ts = (ITransitionSeries) value;
			tswidget.setName(ts.getShell().toString());

			tswidget.setBackground(cellRenderer.getBackgroundSelectionColor());
			tswidget.setForeground(cellRenderer.getTextSelectionColor());
			//tswidget.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBorderSelectionColor()));
			tswidget.setBorder(Spacing.bTiny());
			
			tswidget.setSelected(controller.getProposedTransitionSeries().contains(ts));
			return tswidget;

		}
		else if (value instanceof Element)
		{
			Element element = (Element) value;
			tstLabel.setText(element.atomicNumber() + " " + element.toString() + " (" + element.name() + ")");
			
			tstLabel.setBackground(cellRenderer.getBackgroundSelectionColor());
			tstLabel.setForeground(cellRenderer.getTextSelectionColor());
			tstLabel.setBorder(Spacing.bSmall());			
			
			
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
		if (selected instanceof TransitionShell) return false;
		if (selected instanceof String) return false;
		return true;
	}

}
