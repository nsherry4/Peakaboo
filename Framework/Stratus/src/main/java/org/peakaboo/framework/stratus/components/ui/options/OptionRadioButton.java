package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.lang.model.type.NullType;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

public class OptionRadioButton<T> extends OptionCustomComponent implements OptionFluentControlAPI<T> {

	private JRadioButton button;
	private Consumer<T> listener;
	private T item;
	
	public OptionRadioButton() {
		this(null, null);
	}
	
	public OptionRadioButton(OptionBlock block, ButtonGroup group) {
		this(block, group, null);
	}
	
	public OptionRadioButton(OptionBlock block, ButtonGroup group, T item) {
		super(block, new JRadioButton(), true);
		this.item = item;
		
		button = (JRadioButton) super.getComponent();
		if (group != null) { group.add(button); }
		button.addItemListener(e -> {
			if (this.listener != null) this.listener.accept(this.item);
		});
		
	}
	
	@Override
	protected void onClick() {
		boolean state = !isSelected();
		setSelected(state);
		if (listener != null) listener.accept(null);
	}

	public JRadioButton getButton() {
		return button;
	}

	public void setSelected(boolean b) {
		button.setSelected(b);
	}

	public boolean isSelected() {
		return button.isSelected();
	}

	@Override
	public OptionRadioButton<T> withDescription(String description) {
		super.withDescription(description);
		return this;
	}

	@Override
	public OptionRadioButton<T> withTooltip(String tooltip) {
		super.withTooltip(tooltip);
		return this;
	}

	@Override
	public OptionRadioButton<T> withTitle(String title) {
		super.withTitle(title);
		return this;
	}

	@Override
	public OptionRadioButton<T> withSize(OptionSize size) {
		super.withSize(size);
		return this;
	}
	
	@Override
	public OptionRadioButton<T> withText(String title, String description) {
		super.withText(title, description);
		return this;
	}

	@Override
	public OptionRadioButton<T> withListener(Consumer<T> event) {
		this.listener = e -> {
			if (isSelected()) event.accept(this.item);
		};
		return this;
	}
	
	public OptionRadioButton<T> withListener(Runnable event) {
		return withListener(nul -> event.run());
	}

	public OptionRadioButton withSelection(boolean selected) {
		this.setSelected(selected);
		return this;
	}

	@Override
	public OptionRadioButton withKeyStroke(KeyStroke keystroke, JComponent parent) {
		
		if (keystroke != null) {
			Action action = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setSelected(true);
				}
			};
			parent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke, keystroke.toString());
			parent.getActionMap().put(keystroke.toString(), null);
			parent.getActionMap().put(keystroke.toString(), action);
		}
		
		return this;
	}
	
}
