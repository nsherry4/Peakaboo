package org.peakaboo.framework.swidget.widgets.listwidget;

import javax.swing.JPanel;

public abstract class ListWidget<T> extends JPanel {

	private T value;
	private ListWidgetParent parent;
	
	void setParent(ListWidgetParent parent) {
		this.parent = parent;
	}
	
	protected ListWidgetParent getListWidgetParent() {
		return parent;
	}
	
	public void setValue(T value) {
		this.value = value;
		onSetValue(this.value);
	}
	
	public T getValue() {
		return value;
	}
	
	protected abstract void onSetValue(T value);
	
}
