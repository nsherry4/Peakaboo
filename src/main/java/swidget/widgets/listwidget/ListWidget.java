package swidget.widgets.listwidget;

import javax.swing.JPanel;

public abstract class ListWidget<T> extends JPanel {

	private T value;
	
	public void setValue(T value) {
		this.value = value;
		onSetValue(this.value);
	}
	
	public T getValue() {
		return value;
	}
	
	protected abstract void onSetValue(T value);
	
}
