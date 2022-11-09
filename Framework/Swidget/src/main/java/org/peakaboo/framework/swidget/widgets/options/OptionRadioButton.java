package org.peakaboo.framework.swidget.widgets.options;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.lang.model.type.NullType;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

public class OptionRadioButton extends OptionBox implements OptionFluentControlAPI<NullType> {

	private JRadioButton button;
	private OptionLabel label;
	private Consumer<NullType> listener;
	
	public OptionRadioButton(OptionBlock block, ButtonGroup group) {
		super(block);
		
		button = new JRadioButton();
		group.add(button);
		
		label = new OptionLabel("", "");
		
		this.add(button);
		this.addSpacer();
		this.add(label);
		this.addExpander();
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				boolean state = !isSelected();
				setSelected(state);
				if (listener != null) listener.accept(null);
			}
		});
		
	}

	public JRadioButton getButton() {
		return button;
	}
	
	public void setTextSize(OptionSize size) {
		label.setTextSize(size);
	}

	public void setSelected(boolean b) {
		button.setSelected(b);
	}

	public boolean isSelected() {
		return button.isSelected();
	}

	@Override
	public OptionRadioButton withDescription(String description) {
		label.withDescription(description);
		return this;
	}

	@Override
	public OptionRadioButton withTooltip(String tooltip) {
		this.setToolTipText(tooltip);
		button.setToolTipText(tooltip);
		return this;
	}

	@Override
	public OptionRadioButton withTitle(String title) {
		label.withTitle(title);
		return this;
	}

	@Override
	public OptionRadioButton withSize(OptionSize size) {
		label.withSize(size);
		this.setPadding(size.getPaddingSize());
		return this;
	}

	@Override
	public OptionRadioButton withListener(Consumer<NullType> event) {
		this.listener = e -> {
			if (isSelected()) event.accept(null);
		};
		button.addChangeListener(e -> this.listener.accept(null));
		return this;
	}
	
	public OptionRadioButton withListener(Runnable event) {
		return withListener(nul -> event.run());
	}

	@Override
	public OptionRadioButton withText(String title, String description) {
		label.withText(title, description);
		return this;
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
