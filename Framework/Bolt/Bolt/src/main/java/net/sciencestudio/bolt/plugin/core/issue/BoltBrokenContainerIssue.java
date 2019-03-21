package net.sciencestudio.bolt.plugin.core.issue;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

/**
 * This container is broken in some way
 */
public abstract class BoltBrokenContainerIssue<T extends BoltPlugin> implements BoltContainerIssue<T> {
	
	private BoltContainer<T> container;

	public BoltBrokenContainerIssue(BoltContainer<T> container) {
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
