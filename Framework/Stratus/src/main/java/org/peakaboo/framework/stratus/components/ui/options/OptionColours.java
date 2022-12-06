package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.peakaboo.framework.stratus.components.ui.colour.ColourChooser;

public class OptionColours extends OptionCustomComponent implements OptionFluentControlAPI<Color> {

	private Consumer<Color> listener;
	private ColourChooser chooser;
	
	public OptionColours(OptionBlock block, List<Color> colours, Color selected) {
		this(block, colours, selected, false);
	}
	
	public OptionColours(OptionBlock block, List<Color> colours, Color selected, boolean allowNull) {
		super(block, new ColourChooser(colours, selected, allowNull), false);
		chooser = (ColourChooser) super.getComponent();
		chooser.addItemListener(e -> {
			if (this.listener != null) this.listener.accept(chooser.getSelected());
		});
		this.setFocusable(false);
	}

	@Override
	public OptionColours withListener(Consumer<Color> listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public OptionColours withKeyStroke(KeyStroke keystroke, JComponent parent) {
		//NOOP
		return this;
	}
	
	public Color getSelected() {
		return chooser.getSelected();
	}
	
	public void setSelected(Color colour) {
		chooser.setSelected(colour);
	}

	public OptionColours withSelection(Color selected) {
		this.setSelected(selected);
		return this;
	}
	
	

	@Override
	public OptionColours withDescription(String description) {
		super.withDescription(description);
		return this;
	}

	@Override
	public OptionColours withTooltip(String tooltip) {
		super.withTooltip(tooltip);
		return this;
	}

	@Override
	public OptionColours withTitle(String title) {
		super.withTitle(title);
		return this;
	}

	@Override
	public OptionColours withSize(OptionSize size) {
		super.withSize(size);
		return this;
	}

	@Override
	public OptionColours withText(String title, String description) {
		super.withText(title, description);
		return this;
	}
	
}
