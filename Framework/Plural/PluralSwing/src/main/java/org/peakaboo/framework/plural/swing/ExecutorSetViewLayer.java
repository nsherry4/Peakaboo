package org.peakaboo.framework.plural.swing;

import org.peakaboo.framework.plural.executor.ExecutorSet;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.layers.ModalLayer;

public class ExecutorSetViewLayer extends ModalLayer {

	public ExecutorSetViewLayer(LayerPanel owner, ExecutorSet<?> execset) {
		super(owner, new ExecutorSetView(execset));
		
		execset.addListener(() -> {
			if (execset.getCompleted() || execset.isAborted()) {
				owner.removeLayer(this);
			}
		});
	}
	

	
}
