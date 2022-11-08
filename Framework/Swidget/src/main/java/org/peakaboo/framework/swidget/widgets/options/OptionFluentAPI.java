package org.peakaboo.framework.swidget.widgets.options;

import java.util.function.Consumer;

import org.peakaboo.framework.swidget.widgets.options.OptionLabel.TextSize;

public interface OptionFluentAPI<T> {

	public OptionFluentAPI<T> withDescription(String description);
	public OptionFluentAPI<T> withTooltip(String tooltip);
	public OptionFluentAPI<T> withTitle(String title);
	public OptionFluentAPI<T> withText(String title, String description);
	public OptionFluentAPI<T> withTextSize(TextSize size);
	
}
