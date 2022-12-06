package org.peakaboo.framework.stratus.components.stencil;

import javax.swing.JPanel;

/**
 * Stencils are widgets following a common interface for creating simple and
 * effective renderers and editors for JLists, JTrees and JTables.
 */

public abstract class Stencil<T> extends JPanel {

	private T value;
	private StencilParent parent;
	
	void setParent(StencilParent parent) {
		this.parent = parent;
	}
	
	protected StencilParent getListWidgetParent() {
		return parent;
	}
	
	public void setValue(T value, boolean selected) {
		this.value = value;
		onSetValue(this.value, selected);
	}
	
	public T getValue() {
		return value;
	}
	
	protected abstract void onSetValue(T value, boolean selected);
	
}
