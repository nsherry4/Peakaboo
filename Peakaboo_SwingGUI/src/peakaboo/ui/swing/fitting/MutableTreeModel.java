package peakaboo.ui.swing.fitting;

import javax.swing.tree.TreeModel;


public interface MutableTreeModel extends TreeModel
{

	public void fireChangeEvent();
	
}
