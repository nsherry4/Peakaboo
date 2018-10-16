package swidget.widgets.listwidget;

import javax.swing.JPanel;

public class ListWidget<T> extends JPanel {

	private T value;
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
}
