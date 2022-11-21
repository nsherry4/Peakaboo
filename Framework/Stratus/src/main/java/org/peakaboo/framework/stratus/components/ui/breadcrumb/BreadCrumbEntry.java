package org.peakaboo.framework.stratus.components.ui.breadcrumb;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Function;

import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;

public class BreadCrumbEntry<T> {
	private FluentToggleButton button;
	private T item;
	private Function<T, String> formatter;
	protected BreadCrumb<T> parent;
	private int width;
	
	public BreadCrumbEntry(BreadCrumb<T> parent, T item, Function<T, String> formatter) {
		this.item = item;
		this.formatter = formatter;
		this.parent = parent;
		make();
	}
	
	/**
	 * Make the button. This should only be called once during the constructor.
	 */
	protected FluentToggleButton make() {
		button = new FluentToggleButton(formatter.apply(item));
		button.withButtonSize(FluentButtonSize.COMPACT);
		if (item.equals(parent.getSelected())) {
			button.setSelected(true);
		}
		button.withAction(selected -> {
			if (selected) {
				parent.makeSelection(item);
			}
		});
		
		button.addMouseWheelListener(parent.onScroll);
		
		button.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
					parent.doNavigateLeft();
					parent.focusSelectedButton();
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN) {
					parent.doNavigateRight();
					parent.focusSelectedButton();
				}
			}
		});
		
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
	 * Returns the button representing this entry
	 */
	public FluentToggleButton getButton() {
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