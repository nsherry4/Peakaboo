package peakaboo.ui.swing.plotting.fitting;

import javax.swing.tree.TreeModel;


public interface MutableTreeModel extends TreeModel
{

	void fireChangeEvent();
	
}
