package org.peakaboo.framework.swidget.widgets.breadcrumb;

import java.util.function.Function;

import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonSize;
import org.peakaboo.framework.swidget.widgets.buttons.ToggleImageButton;

public class BreadCrumbEntry<T> {
	private ToggleImageButton button;
	private T item;
	private Function<T, String> formatter;
	private BreadCrumb<T> parent;
	private int width;
	
	public BreadCrumbEntry(BreadCrumb<T> parent, T item, Function<T, String> formatter) {
		this.item = item;
		this.formatter = formatter;
		this.parent = parent;
		make();
	}
	
	/**
	 * Make the button
	 */
	protected ToggleImageButton make() {
		button = new ToggleImageButton(formatter.apply(item));
		button.withButtonSize(ImageButtonSize.COMPACT);
		if (item.equals(parent.getSelected())) {
			button.setSelected(true);
		}
		
		button.addActionListener(e -> {
			if (button.isSelected()) {
				parent.makeSelection(item);
			}
		});
		
		button.addMouseWheelListener(parent.onScroll);
		
		width = button.getPreferredSize().width;
		
		return button;
	}
	
	/**
	 * Called when the breadcrumb selection has been changed externally, allowing
	 * this button to update it's appearance
	 */
	public void onSelection(T selected) {
		if (selected == null || !this.item.equals(selected)) {
			if (button.isSelected()) button.setSelected(false);
		} else {
			if (!button.isSelected()) button.setSelected(true);
		}
	}
	
	/**
	 * The name of this entry in the breadcrumb
	 */
	public String name() {
		return formatter.apply(item);
	}
	
	/**
	 * Returns the button representing this entry
	 */
	public ToggleImageButton getButton() {
		return button;
	}
	
	/**
	 * Returns the item this entry represents
	 */
	public T getItem() {
		return item;
	}
	
	/**
	 * Returns the preferred width of this entry's button
	 */
	public int width() {
		return width;
	}
	
}