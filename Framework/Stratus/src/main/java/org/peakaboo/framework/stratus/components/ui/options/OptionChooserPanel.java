package org.peakaboo.framework.stratus.components.ui.options;

import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Function;

import javax.swing.ButtonGroup;

import org.peakaboo.framework.stratus.components.panels.ClearPanel;

public class OptionChooserPanel<T> extends ClearPanel {

	private T selected;
	
	public OptionChooserPanel(List<T> items, Function <T, OptionRadioButton> widget) {
		this(items, OptionSize.LARGE, widget);
	}
	
	public OptionChooserPanel(List<T> items, OptionSize size, Function<T, OptionRadioButton> widget) {
		
		OptionBlock block = new OptionBlock();
		ButtonGroup group = new ButtonGroup();
		selected = items.get(0);
		
		for (T item : items) {
			OptionRadioButton option = widget.apply(item);
			group.add(option.getButton());
			option.setSelected(selected.equals(item));
			option.withListener(() -> selected = item).withSize(size);
			option.setBlock(block);
			block.add(option);
		}
		
		setLayout(new BorderLayout());
		this.add(new OptionBlocksPanel(block));
		
	}
	
	public T getSelected() {
		return selected;
	}
	
}
