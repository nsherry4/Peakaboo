package peakaboo.ui.swing.plotting.filters.settings.editors;

import javax.swing.JComponent;
import javax.swing.JSeparator;

public class SeparatorEditor extends JSeparator implements Editor
{
	
	public SeparatorEditor()
	{
		super(JSeparator.HORIZONTAL);
	}

	@Override
	public boolean expandVertical()
	{
		return false;
	}

	@Override
	public boolean expandHorizontal()
	{
		return true;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}

	@Override
	public Style getStyle()
	{
		return Style.LABEL_HIDDEN;
	}

	@Override
	public void setFromParameter()
	{
		//no value to set
	}

	@Override
	public Object getEditorValue()
	{
		return null;
	}
	
}
