package net.sciencestudio.bolt.plugin.core.issue;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

public interface BoltContainerIssue<T extends BoltPlugin> extends BoltIssue<T> {

	BoltContainer<T> getContainer();
	
}
