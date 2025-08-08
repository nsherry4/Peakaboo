package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;

import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BorderStyle;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;

public class OptionSpinner<T extends Number> extends OptionCustomComponent implements OptionFluentControlAPI<T> {

	private JSpinner spinner;
	private Consumer<T> listener;
	private FluentButton resetButton;
	private JPanel wrapper;
	
	public OptionSpinner() {
		this(null, null);
	}
	
	public OptionSpinner(OptionBlock block, SpinnerModel model) {
		super(block, createWrapper(), false);
		
		this.wrapper = (JPanel) super.getComponent();
		if (model == null) {
			this.spinner = new JSpinner();
		} else {
			this.spinner = new JSpinner(model);
		}
		
		wrapper.setLayout(new FlowLayout());
		wrapper.add(spinner);
		
		spinner.addChangeListener(e -> {
			if (this.listener != null) {
				@SuppressWarnings("unchecked")
				T value = (T) spinner.getValue();
				this.listener.accept(value);
			}
		});
	}
	
	private static JPanel createWrapper() {
		return new ClearPanel();
	}

	@Override
	protected void onClick() {
		spinner.requestFocus();
	}

	public JSpinner getSpinner() {
		return spinner;
	}

	@SuppressWarnings("unchecked")
	public T getValue() {
		return (T) spinner.getValue();
	}

	public void setValue(T value) {
		spinner.setValue(value);
	}

	@Override
	public OptionSpinner<T> withDescription(String description) {
		super.withDescription(description);
		return this;
	}

	@Override
	public OptionSpinner<T> withTooltip(String tooltip) {
		super.withTooltip(tooltip);
		return this;
	}

	@Override
	public OptionSpinner<T> withTitle(String title) {
		super.withTitle(title);
		return this;
	}

	@Override
	public OptionSpinner<T> withSize(OptionSize size) {
		super.withSize(size);
		return this;
	}
	
	@Override
	public OptionSpinner<T> withText(String title, String description) {
		super.withText(title, description);
		return this;
	}

	@Override
	public OptionSpinner<T> withListener(Consumer<T> listener) {
		this.listener = listener;
		return this;
	}
	
	public OptionSpinner<T> withValue(T value) {
		setValue(value);
		return this;
	}
	
	public OptionSpinner<T> withResetButton(Supplier<T> defaultValueSupplier) {
		return withResetButton(defaultValueSupplier, "Reset to default");
	}
	
	public OptionSpinner<T> withResetButton(Supplier<T> defaultValueSupplier, String tooltip) {
		if (resetButton == null) {
			resetButton = new FluentButton()
				.withIcon(StockIcon.EDIT_UNDO, IconSize.BUTTON)
				.withBordered(BorderStyle.ACTIVE)
				.withTooltip(tooltip)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withAction(() -> {
					T defaultValue = defaultValueSupplier.get();
					setValue(defaultValue);
					if (listener != null) {
						listener.accept(defaultValue);
					}
				});
			
			wrapper.add(resetButton, 0);
			wrapper.revalidate();
		}
		return this;
	}

	@Override
	public OptionSpinner<T> withKeyStroke(KeyStroke keystroke, JComponent parent) {
		
		if (keystroke != null) {
			Action action = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					spinner.requestFocus();
				}
			};
			parent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke, keystroke.toString());
			parent.getActionMap().put(keystroke.toString(), null);
			parent.getActionMap().put(keystroke.toString(), action);
		}
		
		return this;
	}
	
}