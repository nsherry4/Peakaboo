package plural.swing;

import plural.executor.ExecutorSet;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;

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
