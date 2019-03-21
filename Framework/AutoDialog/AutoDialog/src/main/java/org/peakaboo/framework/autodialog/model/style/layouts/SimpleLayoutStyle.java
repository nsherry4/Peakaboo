package org.peakaboo.framework.autodialog.model.style.layouts;

import java.util.List;
import java.util.Optional;

import org.peakaboo.framework.autodialog.model.Value;
import org.peakaboo.framework.autodialog.model.style.CoreStyle;
import org.peakaboo.framework.autodialog.model.style.Style;

public class SimpleLayoutStyle implements Style<List<Value<?>>> {

	private String style;
	private Optional<Boolean> vexpand = Optional.empty(), hexpand = Optional.empty();

	
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

	@Override
	public Optional<Boolean> getVerticalExpand() {
		return vexpand;
	}

	@Override
	public Style<List<Value<?>>> setVerticalExpand(Optional<Boolean> override) {
		vexpand = override;
		return this;
	}

	@Override
	public Optional<Boolean> getHorizontalExpand() {
		return hexpand;
	}

	@Override
	public Style<List<Value<?>>> setHorizontalExpand(Optional<Boolean> override) {
		hexpand = override;
		return this;
	}	
	
}
