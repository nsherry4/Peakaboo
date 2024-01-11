package org.peakaboo.framework.autodialog.model.style.layouts;

public class FramedLayoutStyle extends SimpleLayoutStyle {

	private boolean hiddenOnDisable = false, showBorder = true;
	
	public FramedLayoutStyle() {
		super("layout-frames");
	}
	
	public FramedLayoutStyle(boolean hideWhenDisabled, boolean showBorder) {
		this();
		this.hiddenOnDisable = hideWhenDisabled;
		this.showBorder = showBorder;
	}

	public boolean isHiddenOnDisable() {
		return hiddenOnDisable;
	}

	public boolean isShowBorder() {
		return showBorder;
	}
	
	
	
	
}
