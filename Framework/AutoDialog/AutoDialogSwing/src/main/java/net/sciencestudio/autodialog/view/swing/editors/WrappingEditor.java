package net.sciencestudio.autodialog.view.swing.editors;

import javax.swing.JComponent;


public abstract class WrappingEditor<T, S extends JComponent> extends AbstractSwingEditor<T> {

	protected S component;
	private boolean expandVertical = false;
	private boolean expandHorizontal = false;
	private LabelStyle labelStyle = LabelStyle.LABEL_ON_SIDE;
	
	public WrappingEditor(S component) {
		this(component, false, false, LabelStyle.LABEL_ON_SIDE);
	}
	
	
	public WrappingEditor(S component, boolean expandVertical, boolean expandHorizontal, LabelStyle labelStyle) {
		this.component = component;
		this.expandVertical = expandVertical;
		this.expandHorizontal = expandHorizontal;
		this.labelStyle = labelStyle;
	}
	

	@Override
	public boolean expandVertical() {
		return expandVertical;
	}

	@Override
	public boolean expandHorizontal() {
		return expandHorizontal;
	}

	@Override
	public LabelStyle getLabelStyle() {
		return labelStyle;
	}


	public void validateFailed() {
		setFromParameter();
	}


	@Override
	public S getComponent() {
		return component;
	}

}
