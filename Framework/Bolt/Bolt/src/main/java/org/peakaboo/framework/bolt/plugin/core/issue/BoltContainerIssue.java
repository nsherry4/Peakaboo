package org.peakaboo.framework.bolt.plugin.core.issue;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

public interface BoltContainerIssue<T extends BoltPlugin> extends BoltIssue<T> {

	BoltContainer<T> getContainer();
	
}
