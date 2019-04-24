package org.peakaboo.framework.bolt.plugin.core.issue;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

/**
 * All plugins in this container are out of date
 */
public class BoltOldContainerIssue<T extends BoltPlugin> implements BoltContainerIssue<T> {

	private BoltContainer<T> container;
	
	public BoltOldContainerIssue(BoltContainer<T> container) {
		this.container = container;
	}
	
	@Override
	public String title() {
		return "Outdated Plugin Container";
	}

	@Override
	public String description() {
		return "All of the plugins in this container have newer versions loaded from other sources";
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

	@Override
	public boolean hasFix() {
		return true;
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
	
	
	
}