package org.peakaboo.framework.stratus.components.ui.options;

public interface OptionFluentAPI {

	public OptionFluentAPI withDescription(String description);
	public OptionFluentAPI withTooltip(String tooltip);
	public OptionFluentAPI withTitle(String title);
	public OptionFluentAPI withText(String title, String description);
	public OptionFluentAPI withSize(OptionSize size);
	
}
