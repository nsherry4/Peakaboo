package peakaboo.ui.swing.fitting;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.ui.swing.widgets.Spacing;


public class FittingEditor extends AbstractCellEditor implements TreeCellEditor
{
	
	private JCheckBox tsCheck;
	private TransitionSeries t;
	
	private ElementWidget element;
	private FittingController controller;
	private boolean isTS;
	private DefaultTreeCellRenderer renderer;
	

	public FittingEditor(DefaultTreeCellRenderer renderer, FittingController controller)
	{
		
		super();
		
		this.renderer = renderer;
		this.controller = controller;
		
		
		
		element = new ElementWidget();	
		
		element.getCheckBox().addItemListener(new ItemListener() {
		
			public void itemStateChanged(ItemEvent e)
			{
				fireEditingStopped();
			}
		});
		
		
		tsCheck = new JCheckBox("");
		tsCheck.setBorder(Spacing.bNone());
		tsCheck.setOpaque(false);
		tsCheck.setFont(tsCheck.getFont().deriveFont(Font.PLAIN));
		
		tsCheck.addItemListener(new ItemListener() {
		
			public void itemStateChanged(ItemEvent e)
			{
				fireEditingStopped();
			}
		});
	}
	


	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row)
	{
		
		if (value instanceof TransitionSeries){
		
			isTS = true;
			
			t = (TransitionSeries)value;
			double intensity = controller.getTransitionSeriesIntensityForElement(t.element, t.type);
			
			tsCheck.setText(t.toString() + " (" + SigDigits.roundFloatTo(intensity, 1) + ")");
			tsCheck.setSelected(t.visible);
			
			
			
			if (isSelected){
				tsCheck.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBorderSelectionColor()));
				tsCheck.setBackground(renderer.getBackgroundSelectionColor());
				tsCheck.setForeground(renderer.getTextSelectionColor());
				tsCheck.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBorderSelectionColor()));
			} else {
				tsCheck.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBackgroundNonSelectionColor()));
				//ts.setBorder(c.getBorder());
				tsCheck.setBackground(renderer.getBackgroundNonSelectionColor());
				tsCheck.setForeground(renderer.getTextNonSelectionColor());
				tsCheck.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBackgroundNonSelectionColor()));
			}
			
			return tsCheck;
			
		} else if (value instanceof Element){
			
			isTS = false;
			
			Element e = (Element)value;
			double intensity = controller.getIntensityForElement(e);
			element.setName(e.toString());
			element.setDescription("Atomic #: " + (e.ordinal() + 1) + ", Intensity: " + SigDigits.roundFloatTo(intensity, 1));

			element.setBackground(renderer.getBackgroundSelectionColor());
			element.setForeground(renderer.getTextSelectionColor());
			element.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, renderer.getBorderSelectionColor()));


			element.setSelected(controller.getElementVisibility(e));
			return element;
			
		}
		
		return null;
		
	}


	public Object getCellEditorValue()
	{
		if (isTS) return tsCheck.isSelected();
		return element.isSelected();
	}

	@Override
	public boolean isCellEditable(EventObject arg0)
	{
		return true;
	}


}
