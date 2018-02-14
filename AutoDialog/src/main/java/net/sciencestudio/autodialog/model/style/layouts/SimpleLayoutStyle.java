package net.sciencestudio.autodialog.model.style.layouts;

import java.util.List;

import net.sciencestudio.autodialog.model.Value;
import net.sciencestudio.autodialog.model.style.CoreStyle;
import net.sciencestudio.autodialog.model.style.Style;

public class SimpleLayoutStyle implements Style<List<Value<?>>> {

	private String style;
	
	public SimpleLayoutStyle(String style) {
		this.style = style;
	}
	
	@Override
	public String getStyle() {
		return style;
	}

	@Override
	public CoreStyle getFallbackStyle() {
		return null;
	}

}
