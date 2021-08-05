package org.peakaboo.framework.autodialog.model.style.layouts;

public class FramedLayoutStyle extends SimpleLayoutStyle {

	private boolean hiddenOnDisable;
	
	public FramedLayoutStyle() {
		super("layout-frames");
	}
	
	public FramedLayoutStyle(boolean hideWhenDisabled) {
		this();
		this.hiddenOnDisable = hideWhenDisabled;
	}

	public boolean isHiddenOnDisable() {
		return hiddenOnDisable;
	}
	
	
	
}
