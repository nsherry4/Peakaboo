package peakaboo.ui.swing.plotting.filters.settings.editors;

import javax.swing.JComponent;

public interface Editor
{

	public enum Style {
		LABEL_ON_TOP,
		LABEL_ON_SIDE,
		LABEL_HIDDEN
	}
	
	public boolean expandVertical();
	public boolean expandHorizontal();
	public Style getStyle();
	
	public void setFromParameter();
	public Object getEditorValue();
	
	public JComponent getComponent();
	
}
