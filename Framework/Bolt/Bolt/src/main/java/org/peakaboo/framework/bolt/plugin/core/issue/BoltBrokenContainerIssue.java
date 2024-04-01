package org.peakaboo.framework.bolt.plugin.core.issue;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

/**
 * This container is broken in some way
 */
public abstract class BoltBrokenContainerIssue<T extends BoltPlugin> implements BoltContainerIssue<T> {
	
	private BoltContainer<T> container;

	protected BoltBrokenContainerIssue(BoltContainer<T> container) {
		this.container = container;
	}
	

	@Override
	public boolean hasFix() {
		return container.isDeletable();
	}
	
	@Override
	public boolean fix() {
		return container.delete();
	}
	
	@Override
	public String fixName() {
		return "Delete";
	}
	
	@Override
	public boolean isFixDestructuve() {
		return true;
	}

	@Override
	public String shortSource() {
		return container.getSourceName();
	}

	@Override
	public String longSource() {
		return container.getSourcePath();
	}
	
	@Override
	public BoltContainer<T> getContainer() {
		return container;
	}
}
