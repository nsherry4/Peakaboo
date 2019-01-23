package org.peakaboo.ui.swing.plotting.fitting.lookup;

import javax.swing.tree.TreeModel;


interface MutableTreeModel extends TreeModel
{

	void fireNodesChangeEvent();
	void fireStructureChangeEvent();
	
}
