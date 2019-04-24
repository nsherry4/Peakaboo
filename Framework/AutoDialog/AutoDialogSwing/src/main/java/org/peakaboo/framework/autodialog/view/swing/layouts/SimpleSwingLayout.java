package org.peakaboo.framework.autodialog.view.swing.layouts;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Optional;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.peakaboo.framework.autodialog.view.swing.SwingView;
import org.peakaboo.framework.autodialog.view.swing.editors.SwingEditorFactory;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.autodialog.view.editors.Editor.LabelStyle;
import org.peakaboo.framework.swidget.widgets.Spacing;

public class SimpleSwingLayout extends AbstractSwingLayout {

	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	boolean needsVerticalGlue = true;
	

	public void layout() {
		root.setOpaque(false);
		root.setLayout(layout);
		root.removeAll();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1f;
		root.add(Box.createHorizontalGlue(), c);
		c.insets = Spacing.iSmall();
		
		for (Value<?> param : group.getValue()) {
			
			
			SwingView editor = fromValue(param);
			if (editor == null) { continue; }
			
			JLabel paramLabel = new JLabel(editor.getTitle());
			paramLabel.setFont(paramLabel.getFont().deriveFont(Font.PLAIN));
			paramLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

			boolean expandH = expandHorizontal(editor, param);
			boolean expandV = expandVertical(editor, param);
			
			needsVerticalGlue &= expandV;

			c.weighty = expandV ? 100f : 0f;
			c.weightx = expandH ? 100f : 0f;
			c.gridy += 1;
			c.gridx = 0;
			c.fill = GridBagConstraints.BOTH;
			
			c.anchor = GridBagConstraints.LINE_START;
			
			if (editor.getLabelStyle() == LabelStyle.LABEL_ON_SIDE)
			{
				c.weightx = 0;
				c.fill = GridBagConstraints.BOTH;//expandH ? GridBagConstraints.NONE : GridBagConstraints.BOTH;
				root.add(paramLabel, c);
				
				c.weightx = expandH ? 100f : 0f;
				c.gridx++;
				c.fill = expandH ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.LINE_END;
				
				root.add(component(editor), c);
				
			}
			else if (editor.getLabelStyle() == LabelStyle.LABEL_ON_TOP)
			{
				c.gridwidth = 2;
				
				c.weighty = 0f;
				root.add(paramLabel, c);
	
				c.gridy++;
				
				c.weighty = expandV ? 100f : 0f;
				root.add(component(editor), c);
				
				c.gridwidth = 1;
			}
			else if(editor.getLabelStyle() == LabelStyle.LABEL_HIDDEN)
			{
				c.gridwidth = 2;				
				root.add(component(editor), c);
				c.gridwidth = 1;
			}
		}
		
		if (needsVerticalGlue)
		{
			c.gridy++;
			c.weighty = 100f;
			root.add(Box.createVerticalGlue(), c);
		}
		
		root.doLayout();

	}
	
	private boolean expandVertical(SwingView editor, Value<?> value) {
		Optional<Boolean> override = value.getStyle().getVerticalExpand();
		if (!override.isPresent()) {
			return editor.expandVertical();
		}
		return override.get();
	}
	
	private boolean expandHorizontal(SwingView editor, Value<?> value) {
		Optional<Boolean> override = value.getStyle().getHorizontalExpand();
		if (!override.isPresent()) {
			return editor.expandHorizontal();
		}
		return override.get();
	}


	protected SwingView fromValue(Value<?> value) {
		if (value instanceof Parameter) {
			return SwingEditorFactory.forParameter((Parameter<?>)value);
		} else if (value instanceof Group) {
			return SwingLayoutFactory.forGroup((Group)value);
		} else {
			return null;
		}
	}
	
	
	protected JComponent component(SwingView view) {
		return view.getComponent();
	}

	
}
