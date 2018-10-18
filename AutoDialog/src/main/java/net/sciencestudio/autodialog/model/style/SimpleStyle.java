package net.sciencestudio.autodialog.model.style;

import java.util.Optional;

public class SimpleStyle<T> implements Style<T>{

	private String style;
	private CoreStyle corestyle;
	private Optional<Boolean> vexpand = Optional.empty(), hexpand = Optional.empty();
	
	public SimpleStyle(String style, CoreStyle corestyle) {
		this.style = style;
		this.corestyle = corestyle;
	}

	@Override
	public String getStyle() {
		return style;
	}

	@Override
	public CoreStyle getFallbackStyle() {
		return corestyle;
	}

	@Override
	public Optional<Boolean> getVerticalExpand() {
		return vexpand;
	}

	@Override
	public Style<T> setVerticalExpand(Optional<Boolean> override) {
		vexpand = override;
		return this;
	}

	@Override
	public Optional<Boolean> getHorizontalExpand() {
		return hexpand;
	}

	@Override
	public Style<T> setHorizontalExpand(Optional<Boolean> override) {
		hexpand = override;
		return this;
	}	
	
	
}
