package net.sciencestudio.bolt.plugin.core.issue;

import java.io.File;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;

/**
 * This plugin is out of date
 */
public class BoltOldPluginIssue implements BoltIssue {

	protected BoltPluginPrototype<? extends BoltPlugin> proto;
	
	public BoltOldPluginIssue(BoltPluginPrototype<? extends BoltPlugin> proto) {
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
		try {
			return new File(proto.getSource().toURI()).getName();
		} catch (Throwable e) {
			String[] parts = proto.getSource().getFile().split("{/,\\}");
			return parts[parts.length-1];
		}
	}

	@Override
	public String longSource() {
		return proto.getSource().getFile();
	}
	
}
