package net.sciencestudio.autodialog.model.style;

public class SimpleStyle<T> implements Style<T>{

	private String style;
	private CoreStyle corestyle;
	
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
	
	
}
