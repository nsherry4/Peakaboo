package org.peakaboo.framework.swidget.widgets.options;

public interface OptionFluentAPI<T> {

	public OptionFluentAPI<T> withDescription(String description);
	public OptionFluentAPI<T> withTooltip(String tooltip);
	public OptionFluentAPI<T> withTitle(String title);
	public OptionFluentAPI<T> withText(String title, String description);
	public OptionFluentAPI<T> withSize(OptionSize size);
	
}
