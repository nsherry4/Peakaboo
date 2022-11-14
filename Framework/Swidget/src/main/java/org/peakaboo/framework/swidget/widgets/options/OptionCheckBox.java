package org.peakaboo.framework.swidget.widgets.options;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class OptionCheckBox extends OptionBox implements OptionFluentControlAPI<Boolean> {
	
	private JCheckBox checkbox;
	private OptionLabel label;
	private Consumer<Boolean> listener;
	
	public OptionCheckBox() {
		this(null);
	}
	
	public OptionCheckBox(OptionBlock block) {
		super(block);
		
		checkbox = new JCheckBox();
		label = new OptionLabel("", "");
		
		this.add(checkbox);
		this.addSpacer();
		this.add(label);
		this.addExpander();
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				boolean state = !isSelected();
				setSelected(state);
				if (listener != null) listener.accept(state);
			}
		});
		
	}


	public JCheckBox getCheckbox() {
		return checkbox;
	}

	public void setTextSize(OptionSize size) {
		label.setTextSize(size);
	}

	public boolean isSelected() {
		return checkbox.isSelected();
	}

	public void setSelected(boolean b) {
		checkbox.setSelected(b);
	}


	@Override
	public OptionCheckBox withDescription(String description) {
		label.withDescription(description);
		return this;
	}

	@Override
	public OptionCheckBox withTooltip(String tooltip) {
		this.setToolTipText(tooltip);
		checkbox.setToolTipText(tooltip);
		return this;
	}

	@Override
	public OptionCheckBox withTitle(String title) {
		label.withTitle(title);
		return this;
	}

	@Override
	public OptionCheckBox withSize(OptionSize size) {
		label.withSize(size);
		this.setPadding(size.getPaddingSize());
		return this;
	}

	@Override
	public OptionCheckBox withListener(Consumer<Boolean> listener) {
		this.listener = listener;
		checkbox.addActionListener(e -> listener.accept(isSelected()));
		return this;
	}

	@Override
	public OptionCheckBox withText(String title, String description) {
		label.withText(title, description);
		return this;
	}

	public OptionCheckBox withSelection(boolean selected) {
		this.setSelected(selected);
		return this;
	}

	@Override
	public OptionCheckBox withKeyStroke(KeyStroke keystroke, JComponent parent) {
		
		if (keystroke != null) {
			Action action = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean orig = checkbox.isSelected();
					boolean state = !orig;
					if (e.getSource() == this) {
						//event is from this checkbox so it's state has already been flipped
						state = orig;
					} else {
						//event is from somewhere else, like a keystroke bound to this checkbox
						//so we have to flip the widget state manually.
						setSelected(state);
					}
					
					OptionCheckBox.this.listener.accept(state);

				}
			};
			parent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke, keystroke.toString());
			parent.getActionMap().put(keystroke.toString(), null);
			parent.getActionMap().put(keystroke.toString(), action);
		}
		
		return this;
	}
	
	
}
