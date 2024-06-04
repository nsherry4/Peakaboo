package org.peakaboo.framework.bolt.plugin.core.issue;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;

/**
 * This plugin is out of date
 */
public class BoltOldPluginIssue<T extends BoltPlugin> implements BoltPluginIssue<T> {

	protected PluginDescriptor<T> proto;
	
	public BoltOldPluginIssue(PluginDescriptor<T> proto) {
		this.proto = proto;
	}
	
	@Override
	public String title() {
		return "Old Plugin Version";
	}

	@Override
	public String description() {
		return "This is an older version of the '" + proto.getName() + "' plugin which is not being used";
	}


	@Override
	public String shortSource() {
		return proto.getContainer().getSourceName();
	}

	@Override
	public String longSource() {
		return proto.getContainer().getSourcePath();
	}

	
}
