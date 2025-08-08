package org.peakaboo.framework.bolt.plugin.config.container;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltBrokenContainerIssue;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.container.BoltJarContainer;

public class BoltBrokenConfigfileIssue<T extends BoltConfigPlugin> extends BoltBrokenContainerIssue<T> {

	private String message;
	
	public BoltBrokenConfigfileIssue(BoltConfigContainer<T> container, String message) {
		super(container);
		this.message = message;
	}

	@Override
	public String title() {
		return "Broken Configfile Container";
	}

	@Override
	public String description() {
		return "Could not read plugin configfile " + shortSource() + ": " + message;
	}

}
